package fhirPathCodeGeneration;

import fhirePathParser.fhirpathLexer;
import fhirePathParser.fhirpathParser;
import junit.framework.TestCase;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class CustomFhirPathVisitorTest extends TestCase {

    @Test
    public void testFhirPathEqualNumber() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("tag.subtag = 42").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor(null, null).visit(tree)).toString();
        assertEquals("msg['tag']['subtag']['@value'] == 42", out );
    }

    @Test
    public void testFhirPathEqualExp() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("tag.subtag = tag.subtag").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor(null, null).visit(tree)).toString();
        assertEquals("msg['tag']['subtag']['@value'] == msg['tag']['subtag']['@value']", out );
    }

    @Test
    public void testFhirPathEqualLiteral() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("tag.subtag = 'test'").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor(null, null).visit(tree)).toString();
        assertEquals("msg['tag']['subtag']['@value'] == 'test'", out );
    }

    @Test
    public void testFhirPathNotEqualLiteral() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("tag.subtag != 'test'").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor(null, null).visit(tree)).toString();
        assertEquals("msg['tag']['subtag']['@value'] != 'test'", out );
    }

    @Test
    public void testFhirPathInEqualLiteral() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("tag.subtag < 7").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor(null, null).visit(tree)).toString();
        assertEquals("msg['tag']['subtag']['@value'] < 7", out );
    }

    @Test
    public void testFhirPathMultiSubtags() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("tag.subtag.subsubtag = 'test'").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor(null, null).visit(tree)).toString();
        assertEquals("msg['tag']['subtag']['subsubtag']['@value'] == 'test'", out );
    }

    @Test
    public void testFhirPathVarName() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("var.subtag = 'test'").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor("ctx", "var").visit(tree)).toString();
        assertEquals("var['subtag']['@value'] == 'test'", out );
    }

    @Test
    public void testFhirPathContext() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("ctx.subtag = 'test'").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor("ctx", "var").visit(tree)).toString();
        assertEquals("ctx['subtag']['@value'] == 'test'", out );
    }

    @Test
    public void testFhirIndexedContext() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("ctx.subtag[2] = 'test'").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor("ctx", "var").visit(tree)).toString();
        assertEquals("ctx['subtag'][2]['@value'] == 'test'", out );
    }

    @Test
    public void testFhirIndexedContext2() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("ctx.subtag[2].subsubtag >= 'test'").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor("ctx", "var").visit(tree)).toString();
        assertEquals("ctx['subtag'][2]['subsubtag']['@value'] >= 'test'", out );
    }

    @Test
    public void testFhirVarIndexedContext() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("ctx.subtag[i].subsubtag = 'test'").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor("ctx", "var").visit(tree)).toString();
        assertEquals("ctx['subtag'][i]['subsubtag']['@value'] == 'test'", out );
    }


    @Test
    public void testEvalParsing() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("subtag[0]").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor("ctx", null).visit(tree)).toString();
        assertEquals("ctx['subtag'][0]['@value']", out );
    }

    @Test
    public void testEvalParsingExtracVal() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("tag.subtag").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor("ctx", null).visit(tree)).toString();
        assertEquals("ctx['tag']['subtag']['@value']", out );
    }

    @Test
    public void testEvalFunctionCall() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("subtag[0].count()").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor("ctx", null).visit(tree)).toString();
        assertEquals("ctx['subtag'][0]Ls.length()", out );
    }

    @Test
    public void testSubStrFunctionCall() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("tag.substring(1,2)").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor("ctx", null).visit(tree)).toString();
        assertEquals("ctx['tag']['@value'].toString().substring(1, 4)", out );
    }
    @Test
    public void testSubStrFunctionCall2() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("tag.substring(3)").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor("ctx", null).visit(tree)).toString();
        assertEquals("ctx['tag']['@value'].toString().substring(3)", out );
    }

    @Test
    public void testIncludeFunctionCall2() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("tag.contains('test')").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor("ctx", null).visit(tree)).toString();
        assertEquals("ctx['tag']['@value'].toString().includes('test')", out );
    }

    @Test
    public void testIndexOfFunctionCall2() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("tag.indexOf('test')").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor("ctx", null).visit(tree)).toString();
        assertEquals("ctx['tag']['@value'].toString().indexOf('test')", out );
    }

    @Test
    public void testPByCondCall2() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("ldt_8201.parentByCondition(LDT_8428 = 0, 'LDT_Test')").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor("ldt_8201", null).visit(tree)).toString();
        assertEquals("parentByCondition(ctx => {return ctx['LDT_8428']['@value'] == 0}, ldt_8201, 'LDT_Test')", out );
    }

    @Test
    public void testExistsCall() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("ldt_8201.exists()").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor("ldt_8201", null).visit(tree)).toString();
        assertEquals("typeof ldt_8201 !== 'undefined'", out );
    }

    @Test
    public void testExistsCall3() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("ldt_8201.subtag.exists()").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor("ldt_8201", null).visit(tree)).toString();
        assertEquals("typeof ldt_8201['subtag'] !== 'undefined'", out );
    }


    @Test
    public void testMultiConditionals() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("ldt_8201.subtag = 10 or ldt_8201.subtag != 12").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor("ldt_8201", null).visit(tree)).toString();
        assertEquals("ldt_8201['subtag']['@value'] == 10 || ldt_8201['subtag']['@value'] != 12", out );
    }

    @Test
    public void testMultiConditionals2() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("ldt_8201.subtag <= 10 and ldt_8201.subtag != 12").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor("ldt_8201", null).visit(tree)).toString();
        assertEquals("ldt_8201['subtag']['@value'] <= 10 && ldt_8201['subtag']['@value'] != 12", out );
    }

    @Test
    public void testMultiConditionals3() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("ldt_8201.subtag <= 10 and ldt_8201.subtag != 12 or tag = 'test'").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor("ldt_8201", null).visit(tree)).toString();
        assertEquals("ldt_8201['subtag']['@value'] <= 10 && ldt_8201['subtag']['@value'] != 12 || ldt_8201['tag']['@value'] == 'test'", out );
    }

    @Test
    public void testMultiConditionals4() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(("(testGroup.LDT_8410 = 'GB01') or (testGroup.LDT_8410 = 'GBBX') or (testGroup.LDT_8410 = 'KP01')'").getBytes()));
        fhirpathLexer lexer = new fhirpathLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fhirpathParser parser = new fhirpathParser(tokens);

        ParseTree tree = parser.expression();
        String out = (new CustomFhirPathVisitor("ldt_8201", null).visit(tree)).toString();
        assertEquals("ldt_8201['subtag']['@value'] <= 10 && ldt_8201['subtag']['@value'] != 12 || ldt_8201['tag']['@value'] == 'test'", out );
    }
}