package fhirPathCodeGeneration;

import fhirePathParser.fhirpathBaseVisitor;
import fhirePathParser.fhirpathParser;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * Class that implements the custom logic to traverse the parse-tree of a Fhir-path string, to generate the associated
 * JavaScript code. Has its own Unit Test class to monitor the functionality and avoid regressions. Implements the
 * visitor-pattern. Each node in the parse-tree has its own method (visitFoo) with its own logic that is executed when
 * the node is visited.
 */
public class CustomFhirPathVisitor extends fhirpathBaseVisitor {

    String context;
    String varName;

    public CustomFhirPathVisitor(String fhirContext, String varName) {
        this.context = fhirContext;
        this.varName = varName;
    }


    @Override
    public Object visitInvocationExpression(fhirpathParser.InvocationExpressionContext ctx) {
        String path = "";
        for(int i=0; i<ctx.getChildCount(); i++) {
            String test = ctx.getChild(i).getText();
            int childCount = ctx.getChildCount();
            if(ctx.getChild(i).getClass().equals(fhirpathParser.TermExpressionContext.class)
                    || ctx.getChild(i).getClass().equals(fhirpathParser.InvocationExpressionContext.class)
                    || ctx.getChild(i).getClass().equals(fhirpathParser.IndexerExpressionContext.class) ) {
                path = path + visit(ctx.getChild(i));
            } else if (ctx.getChild(i).getClass().equals(fhirpathParser.MemberInvocationContext.class)
                        || ctx.getChild(i).getText().equals("]") ) {
                path = path + visit(ctx.getChild(i));
                if (    i == ctx.getChildCount() - 1 &&
                        (ctx.getParent() == null
                        || ctx.getParent().getClass().equals(fhirpathParser.EqualityExpressionContext.class)
                        || ctx.getParent().getClass().equals(fhirpathParser.InequalityExpressionContext.class) ) ) {
                    path = path + "['@value']";
                }
            } else if (ctx.getChild(i).getClass().equals(fhirpathParser.FunctionInvocationContext.class) ) {
                        String funcName = ctx.getChild(i).getChild(0).getChild(0).getText();
                        if(funcName.equals("parentByCondition")) {
                            String funcCall = (String) visit(ctx.getChild(i));
                            path = funcCall.replaceAll("%InputNode%", path+", ");
                        }
                        if(funcName.equals("exists")) {
                            String funcCall = (String) visit(ctx.getChild(i));
                            path = funcCall.replaceAll("%InputNode%", path);
                        }
                        if(funcName.equals("count")
                                || funcName.equals("substring")
                                || funcName.equals("contains")
                                || funcName.equals("indexOf")
                                || funcName.equals("replaceMatches")) {
                                    path = path + visit(ctx.getChild(i));
                                }
                }
            }

        return path;
    }

    @Override
    public Object visitMemberInvocation(fhirpathParser.MemberInvocationContext ctx) {
        return "['"+ visitChildren(ctx) +"']";
    }

    @Override
    public Object visitAndExpression(fhirpathParser.AndExpressionContext ctx) {
        return visit(ctx.getChild(0)) + " && " + visit(ctx.getChild(2));
    }

    @Override
    public Object visitOrExpression(fhirpathParser.OrExpressionContext ctx) {
        String test = visit(ctx.getChild(0)) + " || " + visit(ctx.getChild(2));
        return visit(ctx.getChild(0)) + " || " + visit(ctx.getChild(2));
    }

    @Override
    public Object visitParenthesizedTerm(fhirpathParser.ParenthesizedTermContext ctx) {
        return "(" + visit(ctx.getChild(1)) + ")";
    }

    @Override
    public Object visitInvocationTerm(fhirpathParser.InvocationTermContext ctx) {
        if (this.varName == null && this.context != null && !(this.context.equals(ctx.getText()))) {
            return this.context + visitChildren(ctx);
        } else if ( ( this.varName != null && this.varName.equals(ctx.getText()) )
            || ( this.context != null && this.context.equals(ctx.getText()) )
            || (ctx.getParent().getParent().getClass().equals(fhirpathParser.IndexerExpressionContext.class)) ) {
                return ctx.getText();
        } else {
            return "msg" + visitChildren(ctx);
        }
    }

    @Override
    public Object visitIdentifier(fhirpathParser.IdentifierContext ctx) {
        return ctx.getChild(0).getText();
    }

    @Override
    public Object visitNumberLiteral(fhirpathParser.NumberLiteralContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitEqualityExpression(fhirpathParser.EqualityExpressionContext ctx) {
        String expression = "";
        for(int i=0; i<ctx.getChildCount(); i++) {
            String childText = ctx.getChild(i).getText();
            switch (childText) {
                case "=": {
                    expression = expression + " == ";
                    break;
                }
                case "!=": {
                    expression = expression + " != ";
                    break;
                }
                default: {
                    if(ctx.getChild(i+1) == null
                            && ctx.getParent() != null
                            && (ctx.getParent().getClass().equals(fhirpathParser.EqualityExpressionContext.class)
                            ||  ctx.getParent().getClass().equals(fhirpathParser.InequalityExpressionContext.class)) ) {
                        expression = expression + visit(ctx.getChild(i)) + "['@value']" ;
                    } else {
                        expression = expression + visit(ctx.getChild(i));
                    }
                }
            }
        }
        return expression;
    }

    @Override
    public Object visitInequalityExpression(fhirpathParser.InequalityExpressionContext ctx) {
        String expression = "";
        for(int i=0; i<ctx.getChildCount(); i++) {
            String childText = ctx.getChild(i).getText();
            switch (childText) {
                case "<": {
                    expression = expression + " < ";
                    break;
                }
                case "<=": {
                    expression = expression + " <= ";
                    break;
                }
                case ">": {
                    expression = expression + " > ";
                    break;
                }
                case ">=": {
                    expression = expression + " >= ";
                    break;
                }
                default: {
                    if(ctx.getChild(i+1) == null
                            && ctx.getParent() != null
                            && (ctx.getParent().getClass().equals(fhirpathParser.EqualityExpressionContext.class)
                            ||  ctx.getParent().getClass().equals(fhirpathParser.InequalityExpressionContext.class)) ) {
                        expression = expression + visit(ctx.getChild(i)) + "['@value']" ;
                    } else {
                        expression = expression + visit(ctx.getChild(i));
                    }
                }
            }
        }
        return expression;
    }

    @Override
    public Object visitLiteralTerm(fhirpathParser.LiteralTermContext ctx) {
        return ctx.getText();
    }

    @Override
    public Object visitIndexerExpression(fhirpathParser.IndexerExpressionContext ctx) {
        String path = visit(ctx.getChild(0)) + "[" + visit(ctx.getChild(2)) + "]";
        if (    ctx.getParent() == null
                || ( ctx.getParent().getClass().equals(fhirpathParser.EqualityExpressionContext.class) )
                || ( ctx.getParent().getClass().equals(fhirpathParser.InequalityExpressionContext.class) ) ) {
            path = path + "['@value']";
        }
        return path;

    }

    @Override
    public Object visitTermExpression(fhirpathParser.TermExpressionContext ctx) {
        if(ctx.getParent() != null) {
            if ((ctx.getParent().getClass().equals(fhirpathParser.EqualityExpressionContext.class)
                    || ctx.getParent().getClass().equals(fhirpathParser.InequalityExpressionContext.class))
                    && ctx.getChild(0).getChild(0).getClass().equals(fhirpathParser.MemberInvocationContext.class)) {
                return super.visitTermExpression(ctx) + "['@value']";
            } else {
                String test = ctx.getText();
                return super.visitChildren(ctx);
            }
        } else {
            return super.visitChildren(ctx);
        }
    }


    @Override
    public Object visitFunction(fhirpathParser.FunctionContext ctx) {
        String funcName = ctx.getChild(0).getText();
        String funcCall = "";
        switch (funcName) {
            case "count": {
                funcCall =  "Ls.length()";
                break;
            }
            case "parentByCondition": {
                ParseTree paramList = ctx.getChild(2);
                String condition = (String) visit(paramList.getChild(0));
                condition = removeCtx(condition);
                String newParentNode = paramList.getChild(2).getText();
                funcCall = "parentByCondition(ctx => {return ctx"+ condition + "}, %InputNode%" + newParentNode + ")";
                break;
            }
            case "substring": {
                ParseTree paramList = ctx.getChild(2);
                String startIdx = (String) (paramList.getChild(0).getText());
                if(paramList.getChild(2) != null ) {
                    String offset = (String) (paramList.getChild(2).getText());
                    try {
                        int offsetNum = Integer.parseInt(offset) + 1 + Integer.parseInt(startIdx);
                        offset = String.valueOf(offsetNum);
                    } catch (Exception e) {
                        if (offset.contains("indexOf")) {
                            offset = this.context + "['@value'].toString()." + offset;
//                        } else {
//                            offset = offset + "['@value']";
                        }
                    }
                    String endIdx = offset;
                    funcCall = "['@value'].toString().substring(" + startIdx + ", " + endIdx + ")";
                } else {
                    funcCall = "['@value'].toString().substring(" + startIdx + ")";
                }
                break;
            }
            case "contains": {
                ParseTree paramList = ctx.getChild(2);
                String matchStr = (String) (paramList.getChild(0).getText());
                funcCall =  "['@value'].toString().includes("+ matchStr +")";
                break;
            }
            case "indexOf": {
                ParseTree paramList = ctx.getChild(2);
                String matchStr = (String) (paramList.getChild(0).getText());
                funcCall =  "['@value'].toString().indexOf("+ matchStr +")";
                break;
            }
            case "replaceMatches": {
                ParseTree paramList = ctx.getChild(2);
                String matchStr = (String) (paramList.getChild(0).getText());
                matchStr = matchStr.replace("\\\\", "\\").substring(1, matchStr.length()-2);
                String strToRepl = (String) (paramList.getChild(2).getText());
                funcCall =  "['@value'].toString().replace("+ matchStr +", " + strToRepl + ")";
                break;
            }
            case "exists": {
                funcCall =  "typeof %InputNode% !== 'undefined'";
                break;
            }
        }
        return funcCall;
    }

    /**
     * Helper Function to remove the context when not needed.
     * @param condition: generated JavaScript condition from which the context should be removed
     * @return condition String without the context.
     */
    private String removeCtx(String condition) {
        String[] strArr = condition.split("\\[", 2);
        if( ! strArr[1].startsWith("'") ) {
            throw new RuntimeException("not a valid condition");
        }
        return "[" + strArr[1];
    }

}




