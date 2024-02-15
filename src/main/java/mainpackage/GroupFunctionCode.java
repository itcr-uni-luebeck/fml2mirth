package mainpackage;

import org.hl7.fhir.r4.model.StructureMap;

import java.util.List;

/**
 * Class responsible for the JavaScript part that holds all functions generated from the groups.
 */
public class GroupFunctionCode {

    private String code;
    private StaticCode staticCode;

    public GroupFunctionCode(StaticCode staticCode) {
        this.code = "// ======================================= Group - Functions =======================================\n";
        this.staticCode = staticCode;
    }

    /**
     * for each group a GroupProcessor object is generated that is responsible for processing that group. The code
     * returned from each groupProcessor is accumulated in the code attribute of this Object.
     * @param groups
     */
    public void processGroups(List<StructureMap.StructureMapGroupComponent> groups) {
        for(StructureMap.StructureMapGroupComponent group :groups) {
            GroupProcessor groupProcessor = new GroupProcessor(group, this.staticCode);
            this.code = this.code + groupProcessor.process();
        }
    }


    public String getCode() {
        return this.code;
    }
}
