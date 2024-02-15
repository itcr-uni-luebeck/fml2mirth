package mainpackage;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.json.JSONObject;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/***
 * Test Class for the App. Tests are more like "integration tests" The App is called with different parameters and
 * testdata to test support for different formats and functionality.
 */
public class AppTest {

    String pathCredentialFile = "path/to/credentialFile.json";

    @Test
    public void generateMirthTransformerCode() {
        mainpackage.App.main(new String[]{
            "--map", "path/to/structureMap",
            "--out", "path/to/output/generated/channel",
        });
    }

    @Test
    /***
     * Simple, early test with small synthetic test data.
     */
    public void testLDTtoFHIR() {
        JSONObject creds = new JSONObject(Utils.readFile(this.pathCredentialFile));
        String user = creds.getString("name");
        String pw = creds.getString("pw");

        mainpackage.App.main(new String[]{
            "--map", "path/to/structureMap",
            "--isLDT",
            "--mUser", user,
            "--mPw", pw,
            "--cID", "<Mirth channel UUID>",
            "--out", "path/to/output/generated/channel",
            "--mS", "https://<MirthServerIP>:8443",
            "--tS", "http://<TerminologieServerIP>:<Port>",
        });
        String message = Utils.readFile("Path/to/ExampleMessage/bspLDT.txt");
        TestMessageSender sender = new TestMessageSender("http:/<MirthServerIP>:<ChannelPort>");
        String response = sender.sendMessage(message);
        //System.out.println(response);

        FhirContext ctx = FhirContext.forR4();
        IParser parser = ctx.newXmlParser();

        Bundle bundle = parser.parseResource(Bundle.class, response);
        Patient pat = (Patient) bundle.getEntry().get(0).getResource();

        assertEquals("Max", pat.getNameFirstRep().getGiven().get(0).toString());
        assertEquals("Mustermann", pat.getNameFirstRep().getFamily().toString());
    }

    /**
     * The example from the FHIR documentation as XML structures
     */
    @Test
    public void testXMLtoXML() {
        JSONObject creds = new JSONObject(Utils.readFile(this.pathCredentialFile));
        String user = creds.getString("name");
        String pw = creds.getString("pw");

        mainpackage.App.main(new String[]{
            "--map", "path/to/structureMap",
            "--mUser", user,
            "--mPw", pw,
            "--cID", "<Mirth channel UUID>",
            "--out", "path/to/output/generated/channel",
            "--mS", "https://<MirthServerIP>:8443",
            "--tS", "http://<TerminologieServerIP>:<Port>",
        });
        String message = Utils.readFile("Path/to/ExampleMessage/bspLDT.xml");
        TestMessageSender sender = new TestMessageSender("http:/<MirthServerIP>:<ChannelPort>");
        String response = sender.sendMessage(message);
        //System.out.println(response);

        assertEquals("<TRight xmlns=\"http://hl7.org/fhir/tutorial\">\n" +
                "    <aa>\n" +
                "        <ab value=\"12345\"/>\n" +
                "    </aa>\n" +
                "    <aa>\n" +
                "        <ab value=\"6789\"/>\n" +
                "    </aa>\n" +
                "</TRight>\n", response);
    }

    /**
     * Test with a simple HL7v2 message
     */
    @Test
    public void testHL72FHIR() {
        JSONObject creds = new JSONObject(Utils.readFile(this.pathCredentialFile));
        String user = creds.getString("name");
        String pw = creds.getString("pw");

        mainpackage.App.main(new String[]{
            "--map", "path/to/structureMap",
            "--mUser", user,
            "--mPw", pw,
            "--cID", "<Mirth channel UUID>",
            "--out", "path/to/output/generated/channel",
            "--mS", "https://<MirthServerIP>:8443",
            "--tS", "http://<TerminologieServerIP>:<Port>",
        });
        String message = Utils.readFile("Path/to/ExampleMessage/bspHL7v2.txt");
        TestMessageSender sender = new TestMessageSender("http:/<MirthServerIP>:<ChannelPort>");
        String response = sender.sendMessage(message);
        //System.out.println(response);

        assertEquals("<Patient>\n" +
                "    <id value=\"PATID1234\"/>\n" +
                "    <name>\n" +
                "        <family value=\"EVERYMAN\"/>\n" +
                "        <given value=\"ADAM\"/>\n" +
                "    </name>\n" +
                "    <gender value=\"M\"/>\n" +
                "</Patient>\n", response);
    }
}