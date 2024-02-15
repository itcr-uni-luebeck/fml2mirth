package mainpackage;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;



public class TestEuLabBef {

    String credentialPath = "path/to/credentialFile";

    @Test
    public void testEUExample() {
        FhirContext ctx = FhirContext.forR4();
        IParser parser = ctx.newJsonParser();

        String fhirFile = Utils.readFile("/Path/to/EULabReportExample/package/example/Bundle-SimpleChemistryResultReport.json");

        Bundle bundle = parser.parseResource(Bundle.class, fhirFile);

        FhirValidator validator = SetUpValidator.getValidator();
        ValidationResult result = validator.validateWithResult(bundle);
        OperationOutcome opOut = (OperationOutcome) result.toOperationOutcome();

        int idx = 1;
        for (OperationOutcome.OperationOutcomeIssueComponent issue : opOut.getIssue()) {
            if (issue.getSeverity() == OperationOutcome.IssueSeverity.ERROR) {
                System.out.println("ERROR[" + idx + "]: " + issue.getDiagnostics());
                idx++;
            }
        }
        String succ = String.valueOf(result.isSuccessful());
        assertEquals("false", succ);
    }

    @Test
    public void testLDTtoEULab() {
        // credentials for Mirth server
        JSONObject creds = new JSONObject(Utils.readFile(this.credentialPath));
        String user = creds.getString("name");
        String pw = creds.getString("pw");

        mainpackage.App.main(new String[]{
                "--map", "path/to/structureMap",
                "--cID", "<Mirth channel UUID>",
                "--isLDT",
                "--mUser", user,
                "--mPw", pw,
                "--mS", "https://<MirthServerIP>:8443",
                "--tS", "http://<TerminologieServerIP>:<Port>",
                "--out", "path/to/output/generated/channel"
        });

        String message = Utils.readFile("Path/to/ExampleMessage/bspLDT.txt");

        TestMessageSender sender = new TestMessageSender("http://<MirthServerIP>:<ChannelPort>");
        String response = sender.sendMessage(message);
        Utils.writeFile(response, "path/to/output/transformed/message.xml");

        FhirContext ctx = FhirContext.forR4();
        IParser parser = ctx.newXmlParser();

        Bundle bundle = parser.parseResource(Bundle.class, response);

        FhirValidator validator = SetUpValidator.getValidator();
        ValidationResult result = validator.validateWithResult(bundle);
        OperationOutcome opOut = (OperationOutcome) result.toOperationOutcome();

        int idx = 1;
        for (OperationOutcome.OperationOutcomeIssueComponent issue : opOut.getIssue()) {
            if (issue.getSeverity() == OperationOutcome.IssueSeverity.ERROR) {
                System.out.println("ERROR[" + idx + "]: " + issue.getDiagnostics());
                idx++;
            }
        }

        String succ = String.valueOf(result.isSuccessful());
        assertEquals("true", succ);
    }

}
