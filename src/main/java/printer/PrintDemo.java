package printer;

import flask.ast.nodes.Expression;
import flask.ast.nodes.Statement;
import flask.ast.nodes.expressions.access.AttributeAccessNode;
import flask.ast.nodes.expressions.atoms.IdentifierNode;
import flask.ast.nodes.expressions.atoms.LiteralNode;
import flask.ast.nodes.expressions.operations.BinaryOpNode;
import flask.ast.nodes.expressions.operations.CompareNode;
import flask.ast.nodes.helpers.Decorator;
import flask.ast.nodes.helpers.Parameter;
import flask.ast.nodes.statements.ProgramNode;
import flask.ast.nodes.statements.compound.FunctionDefNode;
import flask.ast.nodes.statements.compound.IfStatementNode;
import flask.ast.nodes.statements.imports.FromImportNode;
import flask.ast.nodes.statements.simple.AssignmentNode;
import flask.ast.nodes.statements.simple.ReturnNode;

import template.ast.jinja.JinjaBlockNode;
import template.ast.jinja.JinjaAttributeAccessNode;
import template.ast.jinja.JinjaElseNode;
import template.ast.jinja.JinjaExpressionNode;
import template.ast.jinja.JinjaExtendsNode;
import template.ast.jinja.JinjaFilterNode;
import template.ast.jinja.JinjaForNode;
import template.ast.jinja.JinjaIdentifierNode;
import template.ast.jinja.JinjaIfNode;
import template.ast.jinja.JinjaNode;
import template.ast.jinja.JinjaProgramNode;
import template.ast.jinja.JinjaVariableNode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Example usage of the structured AST tree-printing system added to this
 * project: builds one small but representative tree for each of the two AST
 * hierarchies covered by this task - the Python AST ({@code flask.ast.nodes.*})
 * and the Jinja2 AST ({@code template.ast.jinja.*}) - and prints each with a
 * single {@code root.print("")} call.
 *
 * <p>Every node below is built through its real, existing constructor -
 * nothing here is a mock or a special test-only type - so the trees are
 * exactly what {@code FlaskASTBuilder}/{@code TemplateASTBuilder} would hand
 * back after parsing source that looks like the snippets in the comments.
 * The interesting part is simply the last line of each section:
 * {@code root.print("")}. That one polymorphic call is enough to render the
 * whole tree, however many node types it contains, because every concrete
 * node knows how to print itself.
 *
 * <p>Run directly ({@code java printer.PrintDemo}) to see the output.
 */
public final class PrintDemo {

    private PrintDemo() {
        // Demo entry point - not instantiated.
    }

    public static void main(String[] args) {
        System.out.println("========================================================");
        System.out.println(" PYTHON AST  -  flask.ast.nodes.*");
        System.out.println("========================================================");
        System.out.println(pythonSource());
        System.out.println("--------------------------------------------------------");
        buildPythonAst().print("");

        System.out.println();
        System.out.println("========================================================");
        System.out.println(" JINJA2 AST  -  template.ast.jinja.*");
        System.out.println("========================================================");
        System.out.println(jinjaSource());
        System.out.println("--------------------------------------------------------");
        buildJinjaAst().print("");
    }

    private static String pythonSource() {
        return "from flask import Flask, render_template          # line 1\n"
             + "\n"
             + "@app.route(\"/greet/<name>\")                       # line 3\n"
             + "def greet(name, greeting=\"Hello\"):                 # line 4\n"
             + "    if name == \"admin\":                            # line 5\n"
             + "        message = greeting + \", boss!\"             # line 6\n"
             + "    else:                                          # line 7\n"
             + "        message = greeting + \", \" + name           # line 8\n"
             + "    return message                                 # line 9";
    }

    /**
     * Hand-builds the AST that {@code FlaskASTBuilder} would produce for
     * {@link #pythonSource()}, using the real node constructors with their
     * real source line numbers, exercising - directly or through a nested
     * field - the majority of the concrete node types in this hierarchy:
     * {@code ProgramNode}, {@code FromImportNode}, {@code FunctionDefNode}
     * (with a {@code Decorator} and two {@code Parameter}s, one defaulted),
     * {@code IfStatementNode} (with an else body), {@code AssignmentNode},
     * {@code ReturnNode}, {@code CompareNode}, {@code BinaryOpNode},
     * {@code AttributeAccessNode}, {@code IdentifierNode}, and
     * {@code LiteralNode}.
     */
    private static ProgramNode buildPythonAst() {
        Statement fromImport = new FromImportNode("flask", List.of("Flask", "render_template"), 1, 1);

        Expression decoratorName = new AttributeAccessNode(new IdentifierNode("app", 3, 2), "route", 3, 2);
        Decorator routeDecorator = new Decorator(
                decoratorName,
                List.of(new LiteralNode("/greet/<name>", 3, 12)),
                new LinkedHashMap<>());

        Parameter nameParam = new Parameter("name", null, null);
        Parameter greetingParam = new Parameter("greeting", null, new LiteralNode("Hello", 4, 24));

        Expression condition = new CompareNode(
                new IdentifierNode("name", 5, 8),
                List.of("=="),
                List.of(new LiteralNode("admin", 5, 16)),
                5, 8);

        Expression thenValue = new BinaryOpNode(
                new IdentifierNode("greeting", 6, 19),
                "+",
                new LiteralNode(", boss!", 6, 30),
                6, 19);
        Statement thenAssign = new AssignmentNode(new IdentifierNode("message", 6, 9), thenValue, 6, 9);

        Expression elseValue = new BinaryOpNode(
                new BinaryOpNode(new IdentifierNode("greeting", 8, 19), "+", new LiteralNode(", ", 8, 30), 8, 19),
                "+",
                new IdentifierNode("name", 8, 38),
                8, 19);
        Statement elseAssign = new AssignmentNode(new IdentifierNode("message", 8, 9), elseValue, 8, 9);

        Statement ifStatement = new IfStatementNode(
                condition,
                List.of(thenAssign),
                List.of(),
                List.of(elseAssign),
                5, 5);

        Statement returnStatement = new ReturnNode(List.of(new IdentifierNode("message", 9, 12)), 9, 5);

        Statement greetFunction = new FunctionDefNode(
                "greet",
                List.of(routeDecorator),
                List.of(nameParam, greetingParam),
                List.of(ifStatement, returnStatement),
                null,
                4, 1);

        return new ProgramNode(List.of(fromImport, greetFunction), 1, 1);
    }

    private static String jinjaSource() {
        return "{% extends \"base.html\" %}                         {# line 1 #}\n"
             + "{% block content %}                                {# line 2 #}\n"
             + "  {% if user %}                                    {# line 3 #}\n"
             + "    {{ user.name|upper }}                          {# line 4 #}\n"
             + "  {% else %}                                       {# line 5 #}\n"
             + "    {{ login_prompt }}                             {# line 6 #}\n"
             + "  {% endif %}\n"
             + "  {% for item in items %}                          {# line 8 #}\n"
             + "    {{ item }}\n"
             + "  {% endfor %}\n"
             + "{% endblock %}";
    }

    /**
     * Hand-builds the AST that {@code TemplateASTBuilder} would produce for
     * the Jinja2 constructs in {@link #jinjaSource()} (this hierarchy is, by
     * design, independent of the surrounding HTML - see
     * {@link JinjaNode}'s own class Javadoc), exercising
     * {@code JinjaProgramNode}, {@code JinjaExtendsNode},
     * {@code JinjaBlockNode}, {@code JinjaIfNode} with a
     * {@code JinjaElseNode}, {@code JinjaExpressionNode} with a structured
     * {@code JinjaAttributeAccessNode} root and a {@code JinjaFilterNode},
     * {@code JinjaVariableNode}, {@code JinjaForNode}, and
     * {@code JinjaIdentifierNode}.
     */
    private static JinjaProgramNode buildJinjaAst() {
        JinjaNode extendsNode = new JinjaExtendsNode("base.html", new ArrayList<>(), 1, 1);

        JinjaNode userNameExpression = new JinjaExpressionNode(
                "user.name|upper",
                new JinjaAttributeAccessNode(new JinjaIdentifierNode("user", 4, 8), "name", 4, 8),
                List.of(new JinjaFilterNode("upper", 4, 18)),
                4, 8);

        JinjaElseNode elseBranch = new JinjaElseNode(
                List.of(new JinjaVariableNode("login_prompt", 6, 8)), 5, 3);

        JinjaNode ifUser = new JinjaIfNode(
                "user",
                new JinjaIdentifierNode("user", 3, 8),
                List.of(userNameExpression),
                new ArrayList<>(),
                elseBranch,
                3, 3);

        JinjaNode forItems = new JinjaForNode(
                "item",
                "items",
                new JinjaIdentifierNode("items", 8, 17),
                List.of(new JinjaVariableNode("item", 9, 8)),
                new ArrayList<>(),
                8, 3);

        JinjaNode contentBlock = new JinjaBlockNode("content", List.of(ifUser, forItems), 2, 1);

        return new JinjaProgramNode(List.of(extendsNode, contentBlock), 1, 1);
    }
}
