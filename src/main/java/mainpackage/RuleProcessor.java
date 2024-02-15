package mainpackage;

import org.apache.commons.text.StringSubstitutor;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.StructureMap;
import org.hl7.fhir.r4.model.StructureMap.StructureMapGroupRuleSourceComponent;
import org.hl7.fhir.r4.model.StructureMap.StructureMapGroupRuleTargetComponent;
import org.hl7.fhir.r4.model.StructureMap.StructureMapGroupRuleDependentComponent;
import org.hl7.fhir.r4.model.StructureMap.StructureMapGroupRuleComponent;
import java.util.HashMap;
import java.util.List;

/**
 * Class responsible for processing the rules the Structure Map. Important to note is, that for dependant rules and
 * sub-rules additional RuleProcessors are called. Their returned code is collected in their calling RuleProcessor.
 */
public class RuleProcessor {
    private String name;

    private List<StructureMapGroupRuleSourceComponent> sourceList;
    private List<StructureMapGroupRuleTargetComponent> targetList;
    private List<StructureMapGroupRuleComponent> subRuleList;
    private List<StructureMapGroupRuleDependentComponent> dependentRuleList;
    private StaticCode staticCode;
    private String codeTemplate;
    private String subRuleCode;

    private String dependentRuleCode;



    public RuleProcessor(StructureMap.StructureMapGroupRuleComponent rule, StaticCode staticCode) {
        this.name = rule.getName();
        this.sourceList = rule.getSource();
        this.targetList = rule.getTarget();
        this.subRuleList = rule.getRule();
        this.dependentRuleList = rule.getDependent();

        this.staticCode = staticCode;

        this.codeTemplate = "//${ruleName}\n" +
                            "${ruleBody}";

        this.subRuleCode = processSubRules();
        this.dependentRuleCode = processDependentRules();
    }

    /**
     * Triggers the processing. For the further processing of the body the SourceProcessor is initialized and called.
     * @return
     */
    public String process() {

        HashMap<String, String> varMap = new HashMap<>();
        varMap.put("ruleName", this.name);
        varMap.put("ruleBody", new SourceProcessor(this).process());
        StringSubstitutor substitutor = new StringSubstitutor(varMap);

        return substitutor.replace(this.codeTemplate);

    }


    private String processDependentRules() {
        String dependingRuleCode = "";

        for (StructureMapGroupRuleDependentComponent dependentRule : this.dependentRuleList) {
            if (dependentRule.getName() != null && dependentRule.getVariable() != null) {
                HashMap<String, String> varMap = new HashMap<>();
                varMap.put("ruleName", dependentRule.getName());

                List<StringType> variableList = dependentRule.getVariable();
                String arguments = "";
                for (StringType var : variableList) {
                    arguments = arguments + var.getValue() + ", ";
                }
                arguments = arguments.substring(0, arguments.length() - 2);
                varMap.put("arguments", arguments);
                StringSubstitutor substitutor = new StringSubstitutor(varMap);

                String templ = "\n${ruleName}(${arguments});\n";
                dependingRuleCode = dependingRuleCode + substitutor.replace(templ);
            }
        }
    return dependingRuleCode;
    }

    private String processSubRules() {
        String subRuleCode = "";
        for(StructureMapGroupRuleComponent subRule : this.getSubRuleList()) {
            subRuleCode = subRuleCode + new RuleProcessor(subRule, this.getStaticCode()).process();
        }
        return subRuleCode;
    }

    public List<StructureMapGroupRuleSourceComponent> getSourceList() {
        return this.sourceList;
    }
    public List<StructureMapGroupRuleTargetComponent> getTargetList() {
        return this.targetList;
    }
    public List<StructureMapGroupRuleComponent> getSubRuleList() {
        return subRuleList;
    }
    public List<StructureMapGroupRuleDependentComponent> getDependentRuleList() {
        return dependentRuleList;
    }
    public StaticCode getStaticCode() {
        return staticCode;
    }
    public String getSubRuleCode() {
        return subRuleCode;
    }
    public String getDependentRuleCode() {
        return dependentRuleCode;
    }
}



