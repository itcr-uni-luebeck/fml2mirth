package mainpackage;

import org.hl7.fhir.r4.model.StructureMap.StructureMapGroupComponent;

/**
 * Class responsible for generating the last part of the transformer-script that invokes the transformation in the
 * mirth-channel
 */
public class MainCode {

    private String code;

    public MainCode() {
        this.code = "// ======================================= Main - Function ======================================\n";
    }

    public String getCode() {
        return this.code;
    }

    /**
     * Generates the code that invodes the whole transformation on the mirth server later. To do this, Code is generated,
     * that creates a new XML-structure is initialized and the first group-function is called on the input message.
     * @param firstGroup
     */
    public void setInitFunc(StructureMapGroupComponent firstGroup) {
        String targetType = firstGroup.getInput().get(1).getType();
        String target = firstGroup.getInput().get(1).getName();
        String funcName = firstGroup.getName();
        String src = firstGroup.getInput().get(0).getName();

        this.code = code + "var src = msg;\n";
        this.code = code + "var " + target + " = create('" + targetType + "');\n";
        this.code = code + funcName + "(" + src + ", " + target + ");\n";
        this.code = code + "msg = " + target + ";";
    }
}
