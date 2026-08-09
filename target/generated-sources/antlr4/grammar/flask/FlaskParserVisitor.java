// Generated from grammar/flask/FlaskParser.g4 by ANTLR 4.13.1
package grammar.flask;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link FlaskParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface FlaskParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link FlaskParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(FlaskParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(FlaskParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#simple_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimple_statement(FlaskParser.Simple_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#small_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSmall_stmt(FlaskParser.Small_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#returnStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStatement(FlaskParser.ReturnStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#passStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPassStatement(FlaskParser.PassStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#breakStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStatement(FlaskParser.BreakStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#continueStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinueStatement(FlaskParser.ContinueStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#delStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDelStatement(FlaskParser.DelStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#targetList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTargetList(FlaskParser.TargetListContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#assertStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssertStatement(FlaskParser.AssertStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#globalStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGlobalStatement(FlaskParser.GlobalStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#raiseStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRaiseStatement(FlaskParser.RaiseStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#nonlocalStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNonlocalStatement(FlaskParser.NonlocalStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#compound_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompound_statement(FlaskParser.Compound_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#asyncFunctionDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAsyncFunctionDef(FlaskParser.AsyncFunctionDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#asyncForStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAsyncForStatement(FlaskParser.AsyncForStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#asyncWithStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAsyncWithStatement(FlaskParser.AsyncWithStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(FlaskParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#forStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStatement(FlaskParser.ForStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#whileStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStatement(FlaskParser.WhileStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#withStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWithStatement(FlaskParser.WithStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#withItem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWithItem(FlaskParser.WithItemContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#tryStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTryStatement(FlaskParser.TryStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#exceptClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExceptClause(FlaskParser.ExceptClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#classStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassStatement(FlaskParser.ClassStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#expression_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_statement(FlaskParser.Expression_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#decoratedDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecoratedDef(FlaskParser.DecoratedDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#decorator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecorator(FlaskParser.DecoratorContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#functionDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDef(FlaskParser.FunctionDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#parameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameters(FlaskParser.ParametersContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(FlaskParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#annotatedAssignmentStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotatedAssignmentStatement(FlaskParser.AnnotatedAssignmentStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#suite}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSuite(FlaskParser.SuiteContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(FlaskParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#importStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportStatement(FlaskParser.ImportStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#importNameStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportNameStatement(FlaskParser.ImportNameStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#importFromStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportFromStatement(FlaskParser.ImportFromStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#relativeImportTarget}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelativeImportTarget(FlaskParser.RelativeImportTargetContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#importAsName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportAsName(FlaskParser.ImportAsNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#importAsNames}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportAsNames(FlaskParser.ImportAsNamesContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#dottedName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDottedName(FlaskParser.DottedNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#assignmentStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentStatement(FlaskParser.AssignmentStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#target}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTarget(FlaskParser.TargetContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#target_trailer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTarget_trailer(FlaskParser.Target_trailerContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#subscriptList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubscriptList(FlaskParser.SubscriptListContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#subscript}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubscript(FlaskParser.SubscriptContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#augmentedAssignmentOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAugmentedAssignmentOp(FlaskParser.AugmentedAssignmentOpContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(FlaskParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#lambdef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLambdef(FlaskParser.LambdefContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#lambdaParameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLambdaParameters(FlaskParser.LambdaParametersContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#lambdaParameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLambdaParameter(FlaskParser.LambdaParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#yield_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitYield_expression(FlaskParser.Yield_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#or_boolean_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOr_boolean_expression(FlaskParser.Or_boolean_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#and_boolean_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnd_boolean_expression(FlaskParser.And_boolean_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#not_boolean_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNot_boolean_expression(FlaskParser.Not_boolean_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#comparison_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_expression(FlaskParser.Comparison_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#comp_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComp_op(FlaskParser.Comp_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#additive_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditive_expression(FlaskParser.Additive_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#multiplicative_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicative_expression(FlaskParser.Multiplicative_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#unary_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnary_expression(FlaskParser.Unary_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#power_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPower_expression(FlaskParser.Power_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#atom_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtom_expression(FlaskParser.Atom_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtom(FlaskParser.AtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#listOrComprehension}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListOrComprehension(FlaskParser.ListOrComprehensionContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#comp_for}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComp_for(FlaskParser.Comp_forContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#comp_iter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComp_iter(FlaskParser.Comp_iterContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#comp_if}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComp_if(FlaskParser.Comp_ifContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#trailer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrailer(FlaskParser.TrailerContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#dict_or_set}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDict_or_set(FlaskParser.Dict_or_setContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#dict_items}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDict_items(FlaskParser.Dict_itemsContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#dict_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDict_item(FlaskParser.Dict_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#expression_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_list(FlaskParser.Expression_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#arglist}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArglist(FlaskParser.ArglistContext ctx);
	/**
	 * Visit a parse tree produced by {@link FlaskParser#argument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgument(FlaskParser.ArgumentContext ctx);
}