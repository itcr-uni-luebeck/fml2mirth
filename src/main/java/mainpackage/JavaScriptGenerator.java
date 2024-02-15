package mainpackage;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.StructureMap;
import org.hl7.fhir.r4.model.StructureMap.StructureMapGroupComponent;

import java.nio.charset.StandardCharsets;

/**
 * First class concerned with the generation of the JavaScript Transformer code.
 * Is invoked directly from the Main-Method in App.java.
 * From here, all the necessary classes are called to process the StructureMap to generate the JavaScript code.
 */
public class JavaScriptGenerator {

    private FhirContext ctx;
    private StructureMap structMap;

    private StaticCode staticCode;
    private GroupFunctionCode groupCode;
    private MainCode mainCode;


    JavaScriptGenerator(String structureMapStr) {
        this.ctx = FhirContext.forR4();
        this.structMap = genStructMapFromStr(structureMapStr);
    }

    /**
     * parses the StructureMap to a Java Object. Invoked on initialization of the class to initialize the structMap
     * attribute.
     * @param structMap: String of a StructureMap in XML or JSON
     * @return a StructureMap Object
     */
    private StructureMap genStructMapFromStr(String structMap) {
        structMap = structMap.trim();

        IParser parser = null;

        if (structMap.toCharArray()[0] == '<') {
            parser = this.ctx.newXmlParser();
        } else if (structMap.toCharArray()[0] == '{') {
            parser = this.ctx.newJsonParser();
        }

        return parser.parseResource(StructureMap.class, structMap);
    }


    /**
     * Method that invokes the generation of the JavaScript code
     * @param termServUrl is the address of the Terminology server that will provide the translate-operation.
     * @return the complete JavaScript code for the transformer.
     */
    public String genJsCode(String termServUrl) {

        // initializes all tree Objects responsible for processing the tree parts of the transformer-code.
        this.staticCode = new StaticCode(termServUrl);
        this.groupCode = new GroupFunctionCode(this.staticCode);
        this.mainCode = new MainCode();

        // first group is always the  starting point
        StructureMapGroupComponent firstGroup = this.structMap.getGroupFirstRep();
        mainCode.setInitFunc(firstGroup);
        groupCode.processGroups(this.structMap.getGroup());

        // Fetch code from the responsible Objects and concatenate all three parts to one script before returning it
        String allScript = staticCode.getCode() + groupCode.getCode() + mainCode.getCode();

        // change encoding to ISO-8859-1 otherwise special chars like ä,ü,ß,... are not used properly
        byte[] strAsBytes = allScript.getBytes();
        allScript = new String(strAsBytes, StandardCharsets.ISO_8859_1);

        return allScript;
    };



}
