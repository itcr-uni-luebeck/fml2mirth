package mainpackage;

import fhirPathCodeGeneration.FhirPathTranslator;
import org.apache.commons.text.StringSubstitutor;
import org.hl7.fhir.r4.model.StructureMap;
import org.hl7.fhir.r4.model.Type;

import org.hl7.fhir.r4.model.StructureMap.StructureMapGroupRuleSourceComponent;
import org.hl7.fhir.r4.model.StructureMap.StructureMapSourceListMode;
import java.util.HashMap;
import java.util.List;

/**
 * Class responsible for processing the source part of a rule
 */
public class SourceProcessor {
    private RuleProcessor ruleProcessor;
    private List<StructureMap.StructureMapGroupRuleSourceComponent> sourceList;
    private int addTab;

    public SourceProcessor(RuleProcessor ruleProcessor) {
        this.ruleProcessor = ruleProcessor;
        this.sourceList = ruleProcessor.getSourceList();
        this.addTab = 0;
    }


    public String process() {
        String sourceCode ="";

        for(StructureMap.StructureMapGroupRuleSourceComponent source : this.sourceList) {
            sourceCode = sourceCode + processSource(source);
        }

        return sourceCode;
    }

    /**
     * First a template is generated which is then filled with values given by the StructureMap.
     * @param source
     * @return
     */
    private String processSource(StructureMapGroupRuleSourceComponent source) {

        String templStr = "";

        // First a template is generated, dependant on some given parameters.
        if (source.getListMode() != null) {
            templStr = genSourceTemplate(source.getListMode(), source.getVariable(), source.getCondition(), source.getCheck());
        } else {
            templStr = genSourceTemplate(StructureMapSourceListMode.NULL, source.getVariable(), source.getCondition() , source.getCheck());
        }

        // a hash map is created that is than filled with the values from the StructureMap
        HashMap<String, String> varMap = new HashMap<>();

        varMap.put("srcContext", (source.getContext() != null) ? source.getContext() : "");
        varMap.put("srcElement", (source.getElement() != null) ? source.getElement().replace("\'", "") : "");
        varMap.put("srcVariableName", (source.getVariable() != null) ? source.getVariable() : null);
        varMap.put("srcDefaultVal", (source.getDefaultValue() != null) ? String.valueOf(source.getDefaultValue()) : null);

        if(source.getCondition() != null) {
            String translatedFhirPath = FhirPathTranslator.translate(source.getCondition(), source.getContext(), source.getVariable());
            varMap.put("srcCondition", translatedFhirPath);
        }
        if(source.getCheck() != null) {
            String translatedFhirPath = FhirPathTranslator.translate(source.getCheck(), source.getContext(), source.getVariable());
            varMap.put("srcCheck", translatedFhirPath );
        }

        varMap.put("defaultDeclaration", (source.getDefaultValue() != null) ? genDefaultVarStr(source.getDefaultValue()) : "");
        varMap.put("targetCode", Utils.addTabs( new TargetProcessor(this).process(), this.addTab ) );
        varMap.put("subRuleCode", Utils.addTabs( this.ruleProcessor.getSubRuleCode(), this.addTab ));
        varMap.put("dependingRuleCode", Utils.addTabs( this.ruleProcessor.getDependentRuleCode(), this.addTab ));

        String sourceCode = new StringSubstitutor(varMap).replace(templStr);

        return sourceCode;
    }

    /**
     * Method that generates a code template in accordance to the given parameters
     * @param listMode
     * @param sourceVarName
     * @param conditional
     * @param check
     * @return
     */
    public String genSourceTemplate(StructureMapSourceListMode listMode, String sourceVarName, String conditional, String check) {
        HashMap<String, String> varMap = new HashMap<>();

        String baseTemplate = "${srcVariableName} = ${srcVariableName}Ls[${iterVal}];\n"
                            + "${conditionalRules}"
                            + "\n";

        String template = "${checkRules}";
        if(conditional != null) {
            template = "if( ${srcCondition} ) {\n"
                    + "${checkRules}"
                    + "};\n";
            this.addTab = this.addTab + 1;
        }

        varMap.put("conditionalRules", template);
        baseTemplate = new StringSubstitutor(varMap).replace(baseTemplate);

        template = Utils.addTabs("${targetRules}", this.addTab);
        if(check != null) {
            template = "if( !(${srcCheck}) ) {\n" +
                    "   throw \"FHIR_MAPPING_ERROR : Check  (${srcCheck}) failed.\";\n" +
                    "} else {" +
                    "${targetRules}" +
                    "};\n";
            template = Utils.addTabs(template, this.addTab);
            this.addTab = this.addTab + 1;
        }
        varMap.put("checkRules", template);
        baseTemplate = new StringSubstitutor(varMap).replace(baseTemplate);


        template = "${targetCode}${subRuleCode}${dependingRuleCode}\n";
        varMap.put("targetRules", Utils.addTab(template, true));
        baseTemplate = new StringSubstitutor(varMap).replace(baseTemplate);

        if(sourceVarName != null) {
             template =   "let ${srcVariableName} = null;\n"
                    + "let ${srcVariableName}Ls = ${srcContext}.elements('${srcElement}');\n"
                    + "${defaultDeclaration}";

            switch(listMode) {
                case FIRST:
                    varMap.put("baseTemplate", Utils.addTab(baseTemplate, true));
                    template = template + baseTemplate;
                    varMap.put("iterVal", "0");
                    break;
                case NOTFIRST:
                    this.addTab = this.addTab + 1;
                    String outerTemplate = "for(let i=1; i<${srcVariableName}Ls.length(); i++) {\n"
                            + "${baseTemplate}"
                            + "}\n"
                            + "\n";
                    varMap.put("baseTemplate", Utils.addTab(baseTemplate, true));
                    template = template + new StringSubstitutor(varMap).replace(outerTemplate);
                    varMap.put("iterVal", "i");
                    break;
                case LAST:
                    varMap.put("baseTemplate", Utils.addTab(baseTemplate, true));
                    template = template + baseTemplate;
                    varMap.put("iterVal", "${srcVariableName}Ls.length()-1");
                    break;
                case NOTLAST:
                    this.addTab = this.addTab + 1;
                    outerTemplate = "for(let i=0; i<${srcVariableName}Ls.length()-1; i++) {\n"
                            + "${baseTemplate}"
                            + "}\n";
                    varMap.put("baseTemplate", Utils.addTab(baseTemplate, true));
                    template = template + new StringSubstitutor(varMap).replace(outerTemplate);
                    varMap.put("iterVal", "i");
                    break;
                case ONLYONE:
                    varMap.put("iterVal", "0");
                    outerTemplate = "if( ${srcVariableName}Ls.length() > 1) {\n"
                            + "    throw \"ERROR: Not more then one ${srcElement} allowed\";\n"
                            + "} else {"
                            + "${baseTemplate}"
                            + "}\n";
                    varMap.put("baseTemplate", Utils.addTab(baseTemplate, true));
                    template = template + new StringSubstitutor(varMap).replace(outerTemplate);
                    varMap.put("iterVal", "0");
                    break;
                case NULL:
                    this.addTab = this.addTab + 1;
                    outerTemplate = "for(let i=0; i<${srcVariableName}Ls.length(); i++) {\n"
                            + "${baseTemplate}"
                            + "}\n"
                            + "\n";
                    varMap.put("baseTemplate", Utils.addTab(baseTemplate, true));
                    template = template + new StringSubstitutor(varMap).replace(outerTemplate);
                    varMap.put("iterVal", "i");
                    break;
                }

                template = new StringSubstitutor(varMap).replace(template);
            } else {
                template = "${targetCode}${subRuleCode}${dependingRuleCode}\n";
            }
        return template;
    }

    /**
     * If there is a default value given for a particular field this method generates the code to make this default
     * value accessible.
     * @param defaultValue
     * @return
     */
    public static String genDefaultVarStr(Type defaultValue) {
        String defaultTemplateCode = "let default${srcVariableName} = \"" + defaultValue.toString() + "\";\n" +
                "if( default${srcVariableName} != null && ${srcVariableName}Ls.length() == 0 ) {\n" +
                "   let tmpXml = new XML('<${srcElement}></${srcElement}>');\n" +
                "   tmpXml['@value'] = default${srcVariableName};\n" +
                "   ${srcVariableName}Ls[0] = tmpXml;\n" +
                "};\n";
        return defaultTemplateCode;
    }

    public RuleProcessor getRuleProcessor() {
        return ruleProcessor;
    }



}
