package flask.ast.builder;

import flask.ast.nodes.ASTNode;
import flask.ast.nodes.ASTRuleNode;
import flask.ast.nodes.Expression;
import flask.ast.nodes.Statement;
import flask.ast.nodes.expressions.access.AttributeAccessNode;
import flask.ast.nodes.expressions.access.FunctionCallNode;
import flask.ast.nodes.expressions.access.SubscriptNode;
import flask.ast.nodes.expressions.atoms.DictNode;
import flask.ast.nodes.expressions.atoms.IdentifierNode;
import flask.ast.nodes.expressions.atoms.LiteralNode;
import flask.ast.nodes.expressions.atoms.ListNode;
import flask.ast.nodes.expressions.atoms.SetNode;
import flask.ast.nodes.expressions.atoms.TupleNode;
import flask.ast.nodes.expressions.comprehensions.ComprehensionNode;
import flask.ast.nodes.expressions.comprehensions.DictComprehensionNode;
import flask.ast.nodes.expressions.comprehensions.GeneratorExpressionNode;
import flask.ast.nodes.expressions.comprehensions.ListComprehensionNode;
import flask.ast.nodes.expressions.comprehensions.SetComprehensionNode;
import flask.ast.nodes.expressions.operations.BinaryOpNode;
import flask.ast.nodes.expressions.operations.CompareNode;
import flask.ast.nodes.expressions.operations.LambdaNode;
import flask.ast.nodes.expressions.operations.UnaryOpNode;
import flask.ast.nodes.helpers.Decorator;
import flask.ast.nodes.helpers.ExceptClause;
import flask.ast.nodes.helpers.Parameter;
import flask.ast.nodes.helpers.WithItem;
import flask.ast.nodes.statements.ProgramNode;
import flask.ast.nodes.statements.compound.ClassDefNode;
import flask.ast.nodes.statements.compound.ForStatementNode;
import flask.ast.nodes.statements.compound.FunctionDefNode;
import flask.ast.nodes.statements.compound.IfStatementNode;
import flask.ast.nodes.statements.compound.TryStatementNode;
import flask.ast.nodes.statements.compound.WhileStatementNode;
import flask.ast.nodes.statements.compound.WithStatementNode;
import flask.ast.nodes.statements.imports.FromImportNode;
import flask.ast.nodes.statements.imports.ImportNode;
import flask.ast.nodes.statements.simple.*;
import grammar.flask.FlaskParser;
import grammar.flask.FlaskParserBaseVisitor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;

public class FlaskASTBuilder extends FlaskParserBaseVisitor<ASTNode> {

	public ASTNode build(ParseTree tree) {
		return visit(tree);
	}

	// ------------------------------------------------------------------
	// Source position helpers
	//
	// Every node constructed by this builder must be given the real
	// line/column of the parser rule that produced it. These two helpers
	// are the single place that knows how to pull that information out of
	// an ANTLR ParserRuleContext, so the logic is written once and reused
	// at every node-construction call site below instead of being
	// duplicated dozens of times.
	// ------------------------------------------------------------------

	private static int lineOf(ParserRuleContext ctx) {
		return ctx.getStart().getLine();
	}

	private static int columnOf(ParserRuleContext ctx) {
		return ctx.getStart().getCharPositionInLine();
	}

	@Override
	public ASTNode visitChildren(RuleNode node) {
		ParserRuleContext ctx = (ParserRuleContext) node;
		List<ASTNode> children = new ArrayList<>();
		for (int i = 0; i < ctx.getChildCount(); i++) {
			ParseTree child = ctx.getChild(i);
			ASTNode childNode = visit(child);
			if (childNode != null) {
				children.add(childNode);
			}
		}
		// Grammar rules that exist only to pick one of several alternatives
		// (statement -> compound_statement -> ifStatement, small_stmt ->
		// assignmentStatement, ...) and have no dedicated visitXxx override
		// carry no meaning of their own: the single real, typed node produced
		// by whichever alternative matched must be returned AS-IS so it keeps
		// being the concrete Statement/Expression subtype callers rely on
		// (e.g. visitProgram's "instanceof Statement" check, and
		// collectStatements' identical check for every suite/body). Wrapping
		// it in a generic ASTRuleNode here would silently make every
		// statement in the program fail that check and disappear from the
		// tree - which is exactly what used to happen before this fix.
		if (children.size() == 1) {
			return children.get(0);
		}
		return new ASTRuleNode(ruleName(ctx), children, lineOf(ctx), columnOf(ctx));
	}

	@Override
	protected ASTNode defaultResult() {
		return null;
	}

	@Override
	protected ASTNode aggregateResult(ASTNode aggregate, ASTNode nextResult) {
		return nextResult != null ? nextResult : aggregate;
	}

	@Override
	public ASTNode visitProgram(FlaskParser.ProgramContext ctx) {
		List<Statement> statements = new ArrayList<>();
		for (FlaskParser.StatementContext statementContext : ctx.statement()) {
			ASTNode node = visit(statementContext);
			if (node instanceof Statement statement) {
				statements.add(statement);
			}
		}
		return new ProgramNode(statements, lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitStatement(FlaskParser.StatementContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public ASTNode visitSimple_statement(FlaskParser.Simple_statementContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public ASTNode visitSmall_stmt(FlaskParser.Small_stmtContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public ASTNode visitImportStatement(FlaskParser.ImportStatementContext ctx) {
		if (ctx.importNameStatement() != null) {
			return visit(ctx.importNameStatement());
		}
		if (ctx.importFromStatement() != null) {
			return visit(ctx.importFromStatement());
		}
		return visitChildren(ctx);
	}

	@Override
	public ASTNode visitImportNameStatement(FlaskParser.ImportNameStatementContext ctx) {
		List<String> imports = new ArrayList<>();
		for (FlaskParser.ImportAsNameContext importAsNameContext : ctx.importAsName()) {
			imports.add(importAsNameContext.getText());
		}
		return new ImportNode(imports, lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitImportFromStatement(FlaskParser.ImportFromStatementContext ctx) {
		String moduleName = ctx.relativeImportTarget().getText();
		List<String> imports = new ArrayList<>();
		if (ctx.importAsNames() != null) {
			for (FlaskParser.ImportAsNameContext importAsNameContext : ctx.importAsNames().importAsName()) {
				imports.add(importAsNameContext.getText());
			}
		} else if (ctx.MUL() != null) {
			imports.add("*");
		}
		return new FromImportNode(moduleName, imports, lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitAssignmentStatement(FlaskParser.AssignmentStatementContext ctx) {
		Expression target = (Expression) visit(ctx.target());
		Expression value = (Expression) visit(ctx.expression());
		if (ctx.augmentedAssignmentOp() != null) {
			String operator = augmentedOperator(ctx.augmentedAssignmentOp());
			value = new BinaryOpNode(target, operator, value, lineOf(ctx), columnOf(ctx));
		}
		return new AssignmentNode(target, value, lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitAnnotatedAssignmentStatement(FlaskParser.AnnotatedAssignmentStatementContext ctx) {
		if (ctx.ASSIGN() != null && ctx.expression().size() > 1) {
			Expression target = (Expression) visit(ctx.target());
			Expression value = (Expression) visit(ctx.expression(1));
			return new AssignmentNode(target, value, lineOf(ctx), columnOf(ctx));
		}
		// A bare type annotation with no value (e.g. "name: str") declares no
		// runtime value, so there is nothing to flow-track; it is not an
		// assignment and is dropped from the statement list.
		return null;
	}

	private String augmentedOperator(FlaskParser.AugmentedAssignmentOpContext ctx) {
		if (ctx.ADD_ASSIGN() != null) return "+";
		if (ctx.SUB_ASSIGN() != null) return "-";
		if (ctx.MUL_ASSIGN() != null) return "*";
		if (ctx.DIV_ASSIGN() != null) return "/";
		return "+";
	}

	// ------------------------------------------------------------------
	// Assignment targets: variables, object properties (obj.attr) and
	// indices/keys (obj[expr]), including chains such as a.b[0].c
	// ------------------------------------------------------------------

	@Override
	public ASTNode visitTarget(FlaskParser.TargetContext ctx) {
		Expression result = new IdentifierNode(ctx.IDENTIFIER().getText(), lineOf(ctx), columnOf(ctx));
		for (FlaskParser.Target_trailerContext trailerContext : ctx.target_trailer()) {
			if (trailerContext.DOT() != null) {
				result = new AttributeAccessNode(result, trailerContext.IDENTIFIER().getText(),
						lineOf(trailerContext), columnOf(trailerContext));
			} else if (trailerContext.LBRACK() != null) {
				List<Expression> indices = new ArrayList<>();
				if (trailerContext.subscriptList() != null) {
					for (FlaskParser.SubscriptContext subscriptContext : trailerContext.subscriptList().subscript()) {
						indices.add(buildSubscriptIndex(subscriptContext));
					}
				}
				result = new SubscriptNode(result, indices, lineOf(trailerContext), columnOf(trailerContext));
			}
		}
		return result;
	}

	private Expression buildSubscriptIndex(FlaskParser.SubscriptContext ctx) {
		// Plain index/key (users[0], data["key"]); slices (a[1:2]) are not
		// statically evaluable so we fall back to the first bound if present.
		if (ctx.expression().isEmpty()) {
			return new LiteralNode(null, lineOf(ctx), columnOf(ctx));
		}
		return (Expression) visit(ctx.expression(0));
	}

	// ------------------------------------------------------------------
	// Expressions
	// ------------------------------------------------------------------

	@Override
	public ASTNode visitExpression(FlaskParser.ExpressionContext ctx) {
		if (ctx.or_boolean_expression() != null) {
			return visit(ctx.or_boolean_expression());
		}
		if (ctx.lambdef() != null) {
			return visit(ctx.lambdef());
		}
		if (ctx.yield_expression() != null) {
			// Yield's runtime value cannot be known statically.
			return new LiteralNode(null, lineOf(ctx), columnOf(ctx));
		}
		return new LiteralNode(null, lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitLambdef(FlaskParser.LambdefContext ctx) {
		List<String> parameters = new ArrayList<>();
		if (ctx.lambdaParameters() != null) {
			for (FlaskParser.LambdaParameterContext parameterContext : ctx.lambdaParameters().lambdaParameter()) {
				String prefix = parameterContext.POWER() != null ? "**"
						: parameterContext.MUL() != null ? "*" : "";
				parameters.add(prefix + parameterContext.IDENTIFIER().getText());
			}
		}
		Expression body = ctx.expression() != null ? (Expression) visit(ctx.expression()) : new LiteralNode(null, lineOf(ctx), columnOf(ctx));
		return new LambdaNode(parameters, body, lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitOr_boolean_expression(FlaskParser.Or_boolean_expressionContext ctx) {
		Expression result = (Expression) visit(ctx.and_boolean_expression(0));
		for (int i = 1; i < ctx.and_boolean_expression().size(); i++) {
			Expression right = (Expression) visit(ctx.and_boolean_expression(i));
			result = new BinaryOpNode(result, "or", right, lineOf(ctx), columnOf(ctx));
		}
		return result;
	}

	@Override
	public ASTNode visitAnd_boolean_expression(FlaskParser.And_boolean_expressionContext ctx) {
		Expression result = (Expression) visit(ctx.not_boolean_expression(0));
		for (int i = 1; i < ctx.not_boolean_expression().size(); i++) {
			Expression right = (Expression) visit(ctx.not_boolean_expression(i));
			result = new BinaryOpNode(result, "and", right, lineOf(ctx), columnOf(ctx));
		}
		return result;
	}

	@Override
	public ASTNode visitNot_boolean_expression(FlaskParser.Not_boolean_expressionContext ctx) {
		if (ctx.NOT() != null) {
			return new UnaryOpNode("not", (Expression) visit(ctx.not_boolean_expression()), lineOf(ctx), columnOf(ctx));
		}
		return visit(ctx.comparison_expression());
	}

	@Override
	public ASTNode visitComparison_expression(FlaskParser.Comparison_expressionContext ctx) {
		Expression left = (Expression) visit(ctx.additive_expression(0));
		if (ctx.comp_op().isEmpty()) {
			return left;
		}
		List<String> operators = new ArrayList<>();
		List<Expression> comparators = new ArrayList<>();
		for (int i = 0; i < ctx.comp_op().size(); i++) {
			operators.add(compOperator(ctx.comp_op(i)));
			comparators.add((Expression) visit(ctx.additive_expression(i + 1)));
		}
		return new CompareNode(left, operators, comparators, lineOf(ctx), columnOf(ctx));
	}

	private String compOperator(FlaskParser.Comp_opContext ctx) {
		if (ctx.EQ() != null) return "==";
		if (ctx.NEQ() != null) return "!=";
		if (ctx.LT() != null) return "<";
		if (ctx.GT() != null) return ">";
		if (ctx.LTE() != null) return "<=";
		if (ctx.GTE() != null) return ">=";
		if (ctx.IN() != null) return "in";
		if (ctx.IS() != null) return "is";
		return "==";
	}

	@Override
	public ASTNode visitAdditive_expression(FlaskParser.Additive_expressionContext ctx) {
		Expression result = (Expression) visit(ctx.multiplicative_expression(0));
		// Operators appear left-to-right interleaved with operands; walk the
		// child token stream directly so ADD/SUB order is preserved.
		for (int i = 1; i < ctx.getChildCount(); i += 2) {
			String opText = ctx.getChild(i).getText();
			Expression right = (Expression) visit(ctx.multiplicative_expression((i + 1) / 2));
			result = new BinaryOpNode(result, opText, right, lineOf(ctx), columnOf(ctx));
		}
		return result;
	}

	@Override
	public ASTNode visitMultiplicative_expression(FlaskParser.Multiplicative_expressionContext ctx) {
		Expression result = (Expression) visit(ctx.unary_expression(0));
		for (int i = 1; i < ctx.getChildCount(); i += 2) {
			String opText = ctx.getChild(i).getText();
			Expression right = (Expression) visit(ctx.unary_expression((i + 1) / 2));
			result = new BinaryOpNode(result, opText, right, lineOf(ctx), columnOf(ctx));
		}
		return result;
	}

	@Override
	public ASTNode visitUnary_expression(FlaskParser.Unary_expressionContext ctx) {
		if (ctx.power_expression() != null) {
			return visit(ctx.power_expression());
		}
		Expression operand = (Expression) visit(ctx.unary_expression());
		if (ctx.SUB() != null) {
			return new UnaryOpNode("-", operand, lineOf(ctx), columnOf(ctx));
		}
		if (ctx.ADD() != null) {
			return new UnaryOpNode("+", operand, lineOf(ctx), columnOf(ctx));
		}
		// AWAIT: value is that of the awaited expression itself.
		return operand;
	}

	@Override
	public ASTNode visitPower_expression(FlaskParser.Power_expressionContext ctx) {
		Expression base = (Expression) visit(ctx.atom_expression());
		if (ctx.POWER() != null) {
			Expression exponent = (Expression) visit(ctx.power_expression());
			return new BinaryOpNode(base, "**", exponent, lineOf(ctx), columnOf(ctx));
		}
		return base;
	}

	@Override
	public ASTNode visitAtom_expression(FlaskParser.Atom_expressionContext ctx) {
		Expression result = (Expression) visit(ctx.atom());
		for (FlaskParser.TrailerContext trailerContext : ctx.trailer()) {
			result = applyTrailer(result, trailerContext);
		}
		return result;
	}

	private Expression applyTrailer(Expression base, FlaskParser.TrailerContext ctx) {
		if (ctx.DOT() != null) {
			return new AttributeAccessNode(base, ctx.IDENTIFIER().getText(), lineOf(ctx), columnOf(ctx));
		}
		if (ctx.LPAREN() != null) {
			CallArguments callArguments = buildCallArguments(ctx.arglist());
			return new FunctionCallNode(base, callArguments.args, callArguments.kwargs,
					callArguments.starArgs, callArguments.kwargsSpread, lineOf(ctx), columnOf(ctx));
		}
		if (ctx.LBRACK() != null) {
			Expression index = ctx.expression() != null ? (Expression) visit(ctx.expression()) : new LiteralNode(null, lineOf(ctx), columnOf(ctx));
			return new SubscriptNode(base, List.of(index), lineOf(ctx), columnOf(ctx));
		}
		return base;
	}

	private static final class CallArguments {
		final List<Expression> args = new ArrayList<>();
		final Map<String, Expression> kwargs = new java.util.LinkedHashMap<>();
		final List<Expression> starArgs = new ArrayList<>();
		final List<Expression> kwargsSpread = new ArrayList<>();
	}

	private CallArguments buildCallArguments(FlaskParser.ArglistContext ctx) {
		CallArguments result = new CallArguments();
		if (ctx == null) {
			return result;
		}
		for (FlaskParser.ArgumentContext argumentContext : ctx.argument()) {
			if (argumentContext.comp_for() != null) {
				Expression element = (Expression) visit(argumentContext.expression());
				List<ComprehensionNode.ForClause> clauses = buildComprehensionClauses(argumentContext.comp_for());
				result.args.add(new GeneratorExpressionNode(element, clauses, lineOf(argumentContext), columnOf(argumentContext)));
			} else if (argumentContext.IDENTIFIER() != null && argumentContext.ASSIGN() != null) {
				result.kwargs.put(argumentContext.IDENTIFIER().getText(), (Expression) visit(argumentContext.expression()));
			} else if (argumentContext.POWER() != null) {
				result.kwargsSpread.add((Expression) visit(argumentContext.expression()));
			} else if (argumentContext.MUL() != null) {
				result.starArgs.add((Expression) visit(argumentContext.expression()));
			} else {
				result.args.add((Expression) visit(argumentContext.expression()));
			}
		}
		return result;
	}

	@Override
	public ASTNode visitAtom(FlaskParser.AtomContext ctx) {
		if (ctx.IDENTIFIER() != null) {
			return new IdentifierNode(ctx.IDENTIFIER().getText(), lineOf(ctx), columnOf(ctx));
		}
		if (ctx.NUMBER() != null) {
			return new LiteralNode(parseNumber(ctx.NUMBER().getText()), lineOf(ctx), columnOf(ctx));
		}
		if (ctx.STRING() != null) {
			return new LiteralNode(parseString(ctx.STRING().getText()), lineOf(ctx), columnOf(ctx));
		}
		if (ctx.TRUE() != null) {
			return new LiteralNode(Boolean.TRUE, lineOf(ctx), columnOf(ctx));
		}
		if (ctx.FALSE() != null) {
			return new LiteralNode(Boolean.FALSE, lineOf(ctx), columnOf(ctx));
		}
		if (ctx.NONE() != null) {
			return new LiteralNode(null, lineOf(ctx), columnOf(ctx));
		}
		if (ctx.LPAREN() != null) {
			if (ctx.expression() != null) {
				return visit(ctx.expression());
			}
			return new TupleNode(List.of(), lineOf(ctx), columnOf(ctx));
		}
		if (ctx.LBRACK() != null) {
			if (ctx.listOrComprehension() == null) {
				return new ListNode(List.of(), lineOf(ctx), columnOf(ctx));
			}
			return visit(ctx.listOrComprehension());
		}
		if (ctx.LBRACE() != null) {
			if (ctx.dict_or_set() == null) {
				return new DictNode(List.of(), lineOf(ctx), columnOf(ctx));
			}
			return visit(ctx.dict_or_set());
		}
		return new LiteralNode(null, lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitDict_or_set(FlaskParser.Dict_or_setContext ctx) {
		if (ctx.dict_item() != null && ctx.comp_for() != null) {
			Expression key = (Expression) visit(ctx.dict_item().expression(0));
			Expression value = (Expression) visit(ctx.dict_item().expression(1));
			List<ComprehensionNode.ForClause> clauses = buildComprehensionClauses(ctx.comp_for());
			return new DictComprehensionNode(key, value, clauses, lineOf(ctx), columnOf(ctx));
		}
		if (ctx.expression() != null && ctx.comp_for() != null) {
			Expression element = (Expression) visit(ctx.expression());
			List<ComprehensionNode.ForClause> clauses = buildComprehensionClauses(ctx.comp_for());
			return new SetComprehensionNode(element, clauses, lineOf(ctx), columnOf(ctx));
		}
		if (ctx.dict_items() != null) {
			List<DictNode.DictItem> items = new ArrayList<>();
			for (FlaskParser.Dict_itemContext dictItemContext : ctx.dict_items().dict_item()) {
				Expression key = (Expression) visit(dictItemContext.expression(0));
				Expression value = (Expression) visit(dictItemContext.expression(1));
				items.add(new DictNode.DictItem(key, value));
			}
			return new DictNode(items, lineOf(ctx), columnOf(ctx));
		}
		if (ctx.expression_list() != null) {
			List<Expression> elements = new ArrayList<>();
			for (FlaskParser.ExpressionContext expressionContext : ctx.expression_list().expression()) {
				elements.add((Expression) visit(expressionContext));
			}
			return new SetNode(elements, lineOf(ctx), columnOf(ctx));
		}
		return new DictNode(List.of(), lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitListOrComprehension(FlaskParser.ListOrComprehensionContext ctx) {
		if (ctx.comp_for() != null) {
			Expression element = (Expression) visit(ctx.expression());
			List<ComprehensionNode.ForClause> clauses = buildComprehensionClauses(ctx.comp_for());
			return new ListComprehensionNode(element, clauses, lineOf(ctx), columnOf(ctx));
		}
		List<Expression> elements = new ArrayList<>();
		if (ctx.expression_list() != null) {
			for (FlaskParser.ExpressionContext expressionContext : ctx.expression_list().expression()) {
				elements.add((Expression) visit(expressionContext));
			}
		}
		return new ListNode(elements, lineOf(ctx), columnOf(ctx));
	}

	/**
	 * Walks a (possibly chained) comp_for -> comp_iter -> comp_if/comp_for
	 * sequence and flattens it into an ordered list of ForClause, each
	 * carrying its own target/iterable plus any "if" filters that trail it
	 * before the next "for" (if any).
	 */
	private List<ComprehensionNode.ForClause> buildComprehensionClauses(FlaskParser.Comp_forContext start) {
		List<ComprehensionNode.ForClause> clauses = new ArrayList<>();
		FlaskParser.Comp_forContext current = start;
		while (current != null) {
			Expression target = new IdentifierNode(current.targetList().getText(), lineOf(current.targetList()), columnOf(current.targetList()));
			Expression iterable = (Expression) visit(current.or_boolean_expression());
			List<Expression> conditions = new ArrayList<>();
			FlaskParser.Comp_iterContext iter = current.comp_iter();
			FlaskParser.Comp_forContext nextFor = null;
			while (iter != null) {
				if (iter.comp_if() != null) {
					conditions.add((Expression) visit(iter.comp_if().or_boolean_expression()));
					iter = iter.comp_if().comp_iter();
				} else {
					nextFor = iter.comp_for();
					iter = null;
				}
			}
			clauses.add(new ComprehensionNode.ForClause(target, iterable, conditions));
			current = nextFor;
		}
		return clauses;
	}

	private Object parseNumber(String text) {
		if (text.contains(".") || text.contains("e") || text.contains("E")) {
			return Double.parseDouble(text);
		}
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException tooBig) {
			return Long.parseLong(text);
		}
	}

	private String parseString(String text) {
		String raw = text;
		boolean isRaw = false;
		if (raw.length() > 0 && (raw.charAt(0) == 'f' || raw.charAt(0) == 'F' || raw.charAt(0) == 'r' || raw.charAt(0) == 'R')) {
			isRaw = raw.charAt(0) == 'r' || raw.charAt(0) == 'R';
			raw = raw.substring(1);
		}
		String body;
		if (raw.startsWith("\"\"\"") || raw.startsWith("'''")) {
			body = raw.substring(3, raw.length() - 3);
		} else {
			body = raw.substring(1, raw.length() - 1);
		}
		if (isRaw) {
			return body;
		}
		return unescape(body);
	}

	private String unescape(String body) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < body.length(); i++) {
			char c = body.charAt(i);
			if (c == '\\' && i + 1 < body.length()) {
				char next = body.charAt(i + 1);
				switch (next) {
					case 'n': result.append('\n'); i++; break;
					case 't': result.append('\t'); i++; break;
					case 'r': result.append('\r'); i++; break;
					case '\'': result.append('\''); i++; break;
					case '"': result.append('"'); i++; break;
					case '\\': result.append('\\'); i++; break;
					default: result.append(c);
				}
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}

	@Override
	public ASTNode visitExpression_statement(FlaskParser.Expression_statementContext ctx) {
		return new ExpressionStatementNode((Expression) visit(ctx.expression()), lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitReturnStatement(FlaskParser.ReturnStatementContext ctx) {
		List<Expression> values = new ArrayList<>();
		if (ctx.expression_list() != null) {
			for (FlaskParser.ExpressionContext expressionContext : ctx.expression_list().expression()) {
				values.add((Expression) visit(expressionContext));
			}
		}
		return new ReturnNode(values, lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitBreakStatement(FlaskParser.BreakStatementContext ctx) {
		return new BreakNode(lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitContinueStatement(FlaskParser.ContinueStatementContext ctx) {
		return new ContinueNode(lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitPassStatement(FlaskParser.PassStatementContext ctx) {
		return new PassNode(lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitGlobalStatement(FlaskParser.GlobalStatementContext ctx) {
		return new GlobalNode(ctx.IDENTIFIER().stream().map(identifier -> identifier.getText()).toList(),
				lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitRaiseStatement(FlaskParser.RaiseStatementContext ctx) {
		Expression exception = ctx.expression(0) != null ? (Expression) visit(ctx.expression(0)) : null;
		Expression cause = ctx.expression().size() > 1 ? (Expression) visit(ctx.expression(1)) : null;
		return new RaiseNode(exception, cause, lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitNonlocalStatement(FlaskParser.NonlocalStatementContext ctx) {
		return new NonlocalNode(ctx.IDENTIFIER().stream().map(identifier -> identifier.getText()).toList(),
				lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitAssertStatement(FlaskParser.AssertStatementContext ctx) {
		Expression test = (Expression) visit(ctx.expression(0));
		Expression message = ctx.expression().size() > 1 ? (Expression) visit(ctx.expression(1)) : null;
		return new AssertNode(test, message, lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitDelStatement(FlaskParser.DelStatementContext ctx) {
		List<Expression> targets = new ArrayList<>();
		for (FlaskParser.TargetContext targetContext : ctx.targetList().target()) {
			targets.add((Expression) visit(targetContext));
		}
		return new DelNode(targets, lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitFunctionDef(FlaskParser.FunctionDefContext ctx) {
		List<Parameter> parameters = new ArrayList<>();
		if (ctx.parameters() != null) {
			for (FlaskParser.ParameterContext parameterContext : ctx.parameters().parameter()) {
				Parameter.Kind kind = parameterContext.POWER() != null ? Parameter.Kind.KW_ARGS
						: parameterContext.MUL() != null ? Parameter.Kind.VAR_ARGS
						: Parameter.Kind.NORMAL;
				Expression typeHint = parameterContext.COLON() != null
						? (Expression) visit(parameterContext.expression(0)) : null;
				Expression defaultValue = parameterContext.ASSIGN() != null
						? (Expression) visit(parameterContext.expression(parameterContext.COLON() != null ? 1 : 0)) : null;
				parameters.add(new Parameter(parameterContext.IDENTIFIER().getText(), typeHint, defaultValue, kind));
			}
		}
		return new FunctionDefNode(ctx.IDENTIFIER().getText(), List.of(), parameters, collectStatements(ctx.suite()), null,
				lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitAsyncFunctionDef(FlaskParser.AsyncFunctionDefContext ctx) {
		return visit(ctx.functionDef());
	}

	@Override
	public ASTNode visitDecoratedDef(FlaskParser.DecoratedDefContext ctx) {
		List<Decorator> decorators = new ArrayList<>();
		for (FlaskParser.DecoratorContext decoratorContext : ctx.decorator()) {
			decorators.add(buildDecorator(decoratorContext));
		}
		ASTNode functionNode = ctx.asyncFunctionDef() != null ? visit(ctx.asyncFunctionDef()) : visit(ctx.functionDef());
		if (functionNode instanceof FunctionDefNode functionDefNode) {
			return new FunctionDefNode(functionDefNode.getName(), decorators, functionDefNode.getParameters(),
					functionDefNode.getBody(), functionDefNode.hasReturnType() ? functionDefNode.getReturnType() : null,
					functionDefNode.getLine(), functionDefNode.getColumn());
		}
		return functionNode;
	}

	private Decorator buildDecorator(FlaskParser.DecoratorContext ctx) {
		Expression name = buildDottedNameExpression(ctx.dottedName());
		CallArguments callArguments = buildCallArguments(ctx.arglist());
		return new Decorator(name, callArguments.args, callArguments.kwargs);
	}

	private Expression buildDottedNameExpression(FlaskParser.DottedNameContext ctx) {
		List<org.antlr.v4.runtime.tree.TerminalNode> identifiers = ctx.IDENTIFIER();
		Expression result = new IdentifierNode(identifiers.get(0).getText(), lineOf(ctx), columnOf(ctx));
		for (int i = 1; i < identifiers.size(); i++) {
			result = new AttributeAccessNode(result, identifiers.get(i).getText(), lineOf(ctx), columnOf(ctx));
		}
		return result;
	}

	@Override
	public ASTNode visitClassStatement(FlaskParser.ClassStatementContext ctx) {
		List<Expression> bases = new ArrayList<>();
		if (ctx.expression_list() != null) {
			for (FlaskParser.ExpressionContext expressionContext : ctx.expression_list().expression()) {
				bases.add((Expression) visit(expressionContext));
			}
		}
		return new ClassDefNode(ctx.IDENTIFIER().getText(), List.of(), bases, collectStatements(ctx.suite()),
				lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitIfStatement(FlaskParser.IfStatementContext ctx) {
		List<Statement> thenBody = collectStatements(ctx.suite(0));
		List<IfStatementNode.ElifClause> elifClauses = new ArrayList<>();
		for (int i = 0; i < ctx.ELIF().size(); i++) {
			elifClauses.add(new IfStatementNode.ElifClause((Expression) visit(ctx.expression(i + 1)), collectStatements(ctx.suite(i + 1))));
		}
		List<Statement> elseBody = ctx.ELSE() != null ? collectStatements(ctx.suite(ctx.suite().size() - 1)) : List.of();
		return new IfStatementNode((Expression) visit(ctx.expression(0)), thenBody, elifClauses, elseBody,
				lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitForStatement(FlaskParser.ForStatementContext ctx) {
		List<Statement> body = collectStatements(ctx.suite(0));
		List<Statement> elseBody = ctx.ELSE() != null ? collectStatements(ctx.suite(1)) : List.of();
		return new ForStatementNode(new IdentifierNode(ctx.targetList().getText(), lineOf(ctx.targetList()), columnOf(ctx.targetList())),
				(Expression) visit(ctx.expression()), body, elseBody, lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitWhileStatement(FlaskParser.WhileStatementContext ctx) {
		List<Statement> body = collectStatements(ctx.suite(0));
		List<Statement> elseBody = ctx.ELSE() != null ? collectStatements(ctx.suite(1)) : List.of();
		return new WhileStatementNode((Expression) visit(ctx.expression()), body, elseBody, lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitWithStatement(FlaskParser.WithStatementContext ctx) {
		List<WithItem> items = new ArrayList<>();
		for (FlaskParser.WithItemContext withItemContext : ctx.withItem()) {
			items.add(new WithItem((Expression) visit(withItemContext.expression()),
					withItemContext.targetList() != null
							? new IdentifierNode(withItemContext.targetList().getText(),
									lineOf(withItemContext.targetList()), columnOf(withItemContext.targetList()))
							: null));
		}
		return new WithStatementNode(items, collectStatements(ctx.suite()), lineOf(ctx), columnOf(ctx));
	}

	@Override
	public ASTNode visitTryStatement(FlaskParser.TryStatementContext ctx) {
		// Grammar: TRY COLON suite
		//            ( exceptClause+ (ELSE COLON suite)? (FINALLY COLON suite)?
		//            | FINALLY COLON suite )
		// suite(0) is always the try-body. Each exceptClause carries its own
		// suite internally (not part of ctx.suite()), so the remaining
		// entries in ctx.suite() are, in order, the optional else-suite and
		// then the optional finally-suite - exactly mirroring which of
		// ELSE()/FINALLY() tokens are actually present.
		List<Statement> tryBody = collectStatements(ctx.suite(0));

		List<ExceptClause> exceptClauses = new ArrayList<>();
		for (FlaskParser.ExceptClauseContext exceptClauseContext : ctx.exceptClause()) {
			Expression exceptionType = exceptClauseContext.expression() != null
					? (Expression) visit(exceptClauseContext.expression())
					: null;
			String exceptionName = exceptClauseContext.IDENTIFIER() != null
					? exceptClauseContext.IDENTIFIER().getText()
					: null;
			List<Statement> exceptBody = collectStatements(exceptClauseContext.suite());
			exceptClauses.add(new ExceptClause(exceptionType, exceptionName, exceptBody));
		}

		int nextSuiteIndex = 1;
		List<Statement> elseBody = List.of();
		if (ctx.ELSE() != null) {
			elseBody = collectStatements(ctx.suite(nextSuiteIndex));
			nextSuiteIndex++;
		}
		List<Statement> finallyBody = List.of();
		if (ctx.FINALLY() != null) {
			finallyBody = collectStatements(ctx.suite(nextSuiteIndex));
		}

		return new TryStatementNode(tryBody, exceptClauses, elseBody, finallyBody, lineOf(ctx), columnOf(ctx));
	}

	private List<Statement> collectStatements(FlaskParser.SuiteContext suiteContext) {
		List<Statement> statements = new ArrayList<>();
		if (suiteContext == null) {
			return statements;
		}
		for (FlaskParser.StatementContext statementContext : suiteContext.statement()) {
			ASTNode node = visit(statementContext);
			if (node instanceof Statement statement) {
				statements.add(statement);
			}
		}
		if (suiteContext.simple_statement() != null) {
			ASTNode node = visit(suiteContext.simple_statement());
			if (node instanceof Statement statement) {
				statements.add(statement);
			}
		}
		return statements;
	}

	private String ruleName(ParserRuleContext ctx) {
		String simpleName = ctx.getClass().getSimpleName();
		return simpleName.endsWith("Context") ? simpleName.substring(0, simpleName.length() - 7) : simpleName;
	}
}
