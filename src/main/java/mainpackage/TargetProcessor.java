package mainpackage;

import fhirPathCodeGeneration.FhirPathTranslator;
import org.apache.commons.text.StringSubstitutor;
import org.hl7.fhir.r4.model.StructureMap;
import org.hl7.fhir.r4.model.StructureMap.StructureMapGroupRuleTargetComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Class responsible for the target part of a rule
 */
public class TargetProcessor {
    private SourceProcessor sourceProcessor;
    private List<StructureMapGroupRuleTargetComponent> targetList;
    // needs the staticCode to be able to add the transform methods to the staticCode.
    private StaticCode staticCode;


    public TargetProcessor(SourceProcessor sourceProcessor) {
        this.sourceProcessor = sourceProcessor;
        this.targetList = sourceProcessor.getRuleProcessor().getTargetList();
        this.staticCode = sourceProcessor.getRuleProcessor().getStaticCode();
    }

    public String process() {
        String targetCode = "";
        for(StructureMapGroupRuleTargetComponent target : this.targetList) {
            targetCode = targetCode + "\n" + processTarget(target);
        }
        return targetCode;
    }

    /**
     * Again a Template is generated and then the placeholders are replaced by values from the hashmap.
     * @param target
     * @return
     */
    private String processTarget(StructureMapGroupRuleTargetComponent target) {
        String targetCode = "";

        HashMap<String, String> varMap = new HashMap<>();
        varMap.put("context", target.getContext() != null ? target.getContext() : "");
        varMap.put("element", target.getElement() != null ? target.getElement() : "");
        varMap.put("variable", target.getVariable() != null ? target.getVariable() : "");

        varMap.put("arguments", getTransformArguments(target));
        varMap.put("attribute", ( target.getElement() != null && target.getElement().equals("extension") ) ? "'@url'" : "'@value'");

        String transform = "";
        if (target.getTransform() != null) {
            transform = target.getTransform().name().toLowerCase();
            this.staticCode.addFunc(transform);
            varMap.put("transform", transform);
        }
        String targetCodeTempl = genTargetTemplate(target, varMap.get("arguments"));

        StringSubstitutor substitutor = new StringSubstitutor(varMap);
        targetCode = substitutor.replace(targetCodeTempl);

        return targetCode;
    }


    private String getTransformArguments(StructureMap.StructureMapGroupRuleTargetComponent target) {
        String arguments = "";
        if(target.getTransform() != null && !target.getTransform().name().equalsIgnoreCase("evaluate") ) {
            List<StructureMap.StructureMapGroupRuleTargetParameterComponent> parameterList = target.getParameter();
            for (StructureMap.StructureMapGroupRuleTargetParameterComponent parameter : parameterList) {
                String fhirType = parameter.getValue().fhirType();
                String param = parameter.toString();
                if (fhirType.equals("string")) {
                    arguments = arguments + "'" + param + "', ";

                } else if (fhirType.equals("id")) {
                    arguments = arguments + param + ", ";
                }
            }
            if (!arguments.equals("")) {
                arguments = arguments.substring(0, arguments.length() - 2);
            }
        } else if(target.getTransform() != null && target.getTransform().name().equalsIgnoreCase("evaluate")){
            List<StructureMap.StructureMapGroupRuleTargetParameterComponent> parameterList = target.getParameter();
            if(parameterList.size() != 2) {
                throw new RuntimeException("Two parameter needed for evaluate(). Number of Parameters does not match.");
            } else {
                String ctx = parameterList.get(0).toString();
                String fhirPath = parameterList.get(1).toString();
                if(fhirPath.startsWith("'") && fhirPath.endsWith("'")) {
                    fhirPath = fhirPath.substring(1, fhirPath.length() -1 );
                }

                arguments = FhirPathTranslator.translate(fhirPath, ctx);

            }
        }
        return arguments;
    }


    /**
     * Generates the template in accordance to the input parameters
     * @param target
     * @param arguments
     * @return
     */
    private String genTargetTemplate(StructureMap.StructureMapGroupRuleTargetComponent target, String arguments) {
        String transformCodeTempl = "";


        if (target.getVariable() != null && target.getTransform() == null) {
            transformCodeTempl = "${context}.appendChild( create('${element}') );\n" +
                    "var ${variable} = ${context}.elements('${element}')[${context}.elements('${element}').length()-1];\n";
        } else {
            if(target.getElement() != null && target.getContext() != null) {
                String transform = target.getTransform().name().toLowerCase();
                if (transform.equals("create")) {
                    transformCodeTempl = "${context}.appendChild( create('${element}') );\n"
                            + "var ${element}Tag = ${context}.elements('${element}')[${context}.elements('${element}').length()-1];\n"
                            + "${element}Tag.appendChild( create(${arguments}) );\n";
                } else if (transform.equals("c")) {
                    transformCodeTempl = "${context}.appendChild( create('${element}') );\n"
                            + "var ${element}Tag = ${context}.elements('${element}')[${context}.elements('${element}').length()-1];\n"
                            + "${element}Tag.appendChild( c(${arguments}) );\n";
                } else if (transform.equals("translate")) {
                    transformCodeTempl = "if( ${arguments} != null) {\n"
                            + "    ${context}.appendChild( create('${element}') );\n"
                            + "    var ${element}Tag = ${context}.elements('${element}')[${context}.elements('${element}').length()-1];\n"
                            + "    let transformedVal = ${transform}(${arguments});\n"
                            + "    if(transformedVal.children().length() == 1) {\n"
                            + "         ${element}Tag['@value'] = transformedVal;\n"
                            + "    } else {\n"
                            + "        let childList = transformedVal.children();\n"
                            + "        for(let i=0; i<childList.length(); i++) {\n"
                            + "            ${element}Tag.appendChild(childList[i]);\n"
                            + "        };\n"
                            + "    };\n"
                            + "}\n";
                } else if (transform.equals("evaluate")) {
                    transformCodeTempl = "if( ${arguments} != null) {\n"
                            + "    ${context}.appendChild( create('${element}') );\n"
                            + "    var ${element}Tag = ${context}.elements('${element}')[${context}.elements('${element}').length()-1];\n"
                            + "    let evalVal = ${transform}(${arguments});\n"
                            + "    if(evalVal != \"\") {\n"
                            + "        ${element}Tag['@value'] = evalVal;\n"
                            + "    } else if(typeof(evalVal) == XML || evalVal.children().length() == 0) {\n"
                            + "        ${element}Tag['@value'] = evalVal['@value'];\n"
                            + "    } else {\n"
                            + "         ${element}Tag.appendChild(evalVal);\n"
                            + "    }\n"
                            + "}\n";
                } else {
                    if (Objects.equals(arguments, "")) {
                        transformCodeTempl = "${context}.appendChild( create('${element}') );\n"
                                + "var ${element}Tag = ${context}.elements('${element}')[${context}.elements('${element}').length()-1];\n"
                                + "${element}Tag['@value'] = ${transform}(${arguments});\n";
                        // TODO: Known Issue: bei mehreren args werden diese kommasepariert in das if-statement übertragen -> nur das letzte argument wird auf != null überprüft.
                    } else {
                        transformCodeTempl = "if( ${arguments} != null) {\n"
                                + "    ${context}.appendChild( create('${element}') );\n"
                                + "    var ${element}Tag = ${context}.elements('${element}')[${context}.elements('${element}').length()-1];\n"
                                + "    ${element}Tag[${attribute}] = ${transform}(${arguments});\n"
                                + "}\n";
                    }
                }
                if(target.getVariable() != null && ( transform.equals("create") )) {
                    transformCodeTempl = transformCodeTempl + "var ${variable} = ${element}Tag.elements(${arguments})[${element}Tag.elements(${arguments}).length()-1];\n";
                } else if(target.getVariable() != null) {
                    transformCodeTempl = transformCodeTempl + "var ${variable} = ${element}Tag;\n";
                }
            } else if (target.getVariable() != null && target.getTransform() != null) {
                transformCodeTempl = transformCodeTempl + "var ${variable} = ${transform}(${arguments});\n";
            }
        }

        return transformCodeTempl;
    }

}


