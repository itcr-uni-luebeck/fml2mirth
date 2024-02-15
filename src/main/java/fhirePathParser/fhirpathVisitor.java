// Generated from java-escape by ANTLR 4.11.1
package fhirePathParser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link fhirpathParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface fhirpathVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by the {@code indexerExpression}
	 * labeled alternative in {@link fhirpathParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndexerExpression(fhirpathParser.IndexerExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code polarityExpression}
	 * labeled alternative in {@link fhirpathParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPolarityExpression(fhirpathParser.PolarityExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code additiveExpression}
	 * labeled alternative in {@link fhirpathParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditiveExpression(fhirpathParser.AdditiveExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code multiplicativeExpression}
	 * labeled alternative in {@link fhirpathParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicativeExpression(fhirpathParser.MultiplicativeExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code unionExpression}
	 * labeled alternative in {@link fhirpathParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnionExpression(fhirpathParser.UnionExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code orExpression}
	 * labeled alternative in {@link fhirpathParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrExpression(fhirpathParser.OrExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code andExpression}
	 * labeled alternative in {@link fhirpathParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndExpression(fhirpathParser.AndExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code membershipExpression}
	 * labeled alternative in {@link fhirpathParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMembershipExpression(fhirpathParser.MembershipExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code inequalityExpression}
	 * labeled alternative in {@link fhirpathParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInequalityExpression(fhirpathParser.InequalityExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code invocationExpression}
	 * labeled alternative in {@link fhirpathParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInvocationExpression(fhirpathParser.InvocationExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code equalityExpression}
	 * labeled alternative in {@link fhirpathParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqualityExpression(fhirpathParser.EqualityExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code impliesExpression}
	 * labeled alternative in {@link fhirpathParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImpliesExpression(fhirpathParser.ImpliesExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code termExpression}
	 * labeled alternative in {@link fhirpathParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTermExpression(fhirpathParser.TermExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code typeExpression}
	 * labeled alternative in {@link fhirpathParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeExpression(fhirpathParser.TypeExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code invocationTerm}
	 * labeled alternative in {@link fhirpathParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInvocationTerm(fhirpathParser.InvocationTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code literalTerm}
	 * labeled alternative in {@link fhirpathParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteralTerm(fhirpathParser.LiteralTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code externalConstantTerm}
	 * labeled alternative in {@link fhirpathParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExternalConstantTerm(fhirpathParser.ExternalConstantTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenthesizedTerm}
	 * labeled alternative in {@link fhirpathParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenthesizedTerm(fhirpathParser.ParenthesizedTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nullLiteral}
	 * labeled alternative in {@link fhirpathParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullLiteral(fhirpathParser.NullLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code booleanLiteral}
	 * labeled alternative in {@link fhirpathParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanLiteral(fhirpathParser.BooleanLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stringLiteral}
	 * labeled alternative in {@link fhirpathParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringLiteral(fhirpathParser.StringLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code numberLiteral}
	 * labeled alternative in {@link fhirpathParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberLiteral(fhirpathParser.NumberLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code dateLiteral}
	 * labeled alternative in {@link fhirpathParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateLiteral(fhirpathParser.DateLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code dateTimeLiteral}
	 * labeled alternative in {@link fhirpathParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateTimeLiteral(fhirpathParser.DateTimeLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code timeLiteral}
	 * labeled alternative in {@link fhirpathParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimeLiteral(fhirpathParser.TimeLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code quantityLiteral}
	 * labeled alternative in {@link fhirpathParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuantityLiteral(fhirpathParser.QuantityLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link fhirpathParser#externalConstant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExternalConstant(fhirpathParser.ExternalConstantContext ctx);
	/**
	 * Visit a parse tree produced by the {@code memberInvocation}
	 * labeled alternative in {@link fhirpathParser#invocation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberInvocation(fhirpathParser.MemberInvocationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code functionInvocation}
	 * labeled alternative in {@link fhirpathParser#invocation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionInvocation(fhirpathParser.FunctionInvocationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code thisInvocation}
	 * labeled alternative in {@link fhirpathParser#invocation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThisInvocation(fhirpathParser.ThisInvocationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code indexInvocation}
	 * labeled alternative in {@link fhirpathParser#invocation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndexInvocation(fhirpathParser.IndexInvocationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code totalInvocation}
	 * labeled alternative in {@link fhirpathParser#invocation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTotalInvocation(fhirpathParser.TotalInvocationContext ctx);
	/**
	 * Visit a parse tree produced by {@link fhirpathParser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(fhirpathParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link fhirpathParser#paramList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParamList(fhirpathParser.ParamListContext ctx);
	/**
	 * Visit a parse tree produced by {@link fhirpathParser#quantity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuantity(fhirpathParser.QuantityContext ctx);
	/**
	 * Visit a parse tree produced by {@link fhirpathParser#unit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnit(fhirpathParser.UnitContext ctx);
	/**
	 * Visit a parse tree produced by {@link fhirpathParser#dateTimePrecision}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateTimePrecision(fhirpathParser.DateTimePrecisionContext ctx);
	/**
	 * Visit a parse tree produced by {@link fhirpathParser#pluralDateTimePrecision}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPluralDateTimePrecision(fhirpathParser.PluralDateTimePrecisionContext ctx);
	/**
	 * Visit a parse tree produced by {@link fhirpathParser#typeSpecifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeSpecifier(fhirpathParser.TypeSpecifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link fhirpathParser#qualifiedIdentifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQualifiedIdentifier(fhirpathParser.QualifiedIdentifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link fhirpathParser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(fhirpathParser.IdentifierContext ctx);
}