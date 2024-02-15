package mainpackage;

import org.hl7.fhir.r4.model.StructureMap;
import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.List;

/**
 * Class responsible for processing groups in the structure Map (for each Group one Function is generated).
 */
public class GroupProcessor {

    private String name;
    private List<StructureMap.StructureMapGroupInputComponent> inputList;
    private List<StructureMap.StructureMapGroupRuleComponent> ruleList;
    private String codeTemplate;
    private StaticCode staticCode;

    /**
     * initializes a first template that is filled by subsequent objects processing the StructureMap further.
     * @param group
     * @param staticCode
     */
    public GroupProcessor(StructureMap.StructureMapGroupComponent group, StaticCode staticCode) {
        this.name = group.getName();
        this.inputList = group.getInput();
        this.ruleList = group.getRule();
        this.codeTemplate = "function ${funcName}(${funcArgs}) {\n" +
                            "${funcBody}" +
                            "};\n" +
                            "\n";
        this.staticCode = staticCode;
    }

    /**
     * fills the template generated during initialization with processed values from the StructureMap.
     * For the function body the subsequent class RuleProcessor is called in processRules(), which is responsible for
     * processing the rules.
     * @return template with replaced placeholders.
     */
    public String process() {
        HashMap<String, String> varMap = new HashMap<>();
        varMap.put("funcName", this.name);
        varMap.put("funcArgs", processInput());
        varMap.put("funcBody", Utils.addTab( processRules(), true) );
        StringSubstitutor substitutor = new StringSubstitutor(varMap);

        return substitutor.replace(this.codeTemplate);
    }

    private String processInput() {
        String args = "";
        for (StructureMap.StructureMapGroupInputComponent input: this.inputList) {
            String input_name =  input.getName();
            args = args + input_name + ", ";
        }
        args = args.substring(0, args.length()-2);

        return args;
    }

    private String processRules() {
        String funcBody = "";
        for(StructureMap.StructureMapGroupRuleComponent rule : this.ruleList) {
            RuleProcessor ruleProcessor = new RuleProcessor(rule, this.staticCode);
            funcBody = funcBody + ruleProcessor.process();
        }
        return funcBody;
    }

}
