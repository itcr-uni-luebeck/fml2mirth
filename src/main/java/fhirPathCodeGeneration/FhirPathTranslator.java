package fhirPathCodeGeneration;

import fhirePathParser.fhirpathLexer;
import fhirePathParser.fhirpathParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.ByteArrayInputStream;

/**
 * Class that handles the translation from Fhir-path to JavaScript. The two translate-Methods are invoking the
 * generation of the parse-tree of a given fhirPath expression and giving that to the CustomFhirPathVisitor().
 * they return a String holding the JavaScript translation of the Fhir-path input expression.
 */
public class FhirPathTranslator {
    /**
     * Translate method with a variable
     * @param fhirPathExpression
     * @param fhirContext
     * @param varName
     * @return String holding the JavaScript translation of the Fhir-path input expression
     */
    public static String translate(String fhirPathExpression, String fhirContext, String varName) {
        String out = null;
        try {
            ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream((fhirPathExpression).getBytes()));
            fhirpathLexer lexer = new fhirpathLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            fhirpathParser parser = new fhirpathParser(tokens);

            ParseTree tree = parser.expression();
            out = (new CustomFhirPathVisitor(fhirContext, varName).visit(tree)).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Translate method to use without variable
     * @param fhirPathExpression
     * @param fhirContext
     * @return String holding the JavaScript translation of the Fhir-path input expression
     */
    public static String translate(String fhirPathExpression, String fhirContext) {
        String out = null;
        try {
            ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream((fhirPathExpression).getBytes()));
            fhirpathLexer lexer = new fhirpathLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            fhirpathParser parser = new fhirpathParser(tokens);

            ParseTree tree = parser.expression();
            out = (new CustomFhirPathVisitor(fhirContext, null).visit(tree)).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

}
