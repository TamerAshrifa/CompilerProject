package org.example;

import flask.ast.nodes.Expression;
import flask.ast.nodes.Statement;
import flask.ast.nodes.expressions.access.FunctionCallNode;
import flask.ast.nodes.expressions.atoms.IdentifierNode;
import flask.ast.nodes.expressions.atoms.ListNode;
import flask.ast.nodes.expressions.atoms.LiteralNode;
import flask.ast.nodes.expressions.atoms.TupleNode;
import flask.ast.nodes.helpers.Parameter;
import flask.ast.nodes.statements.ProgramNode;
import flask.ast.nodes.statements.compound.ClassDefNode;
import flask.ast.nodes.statements.compound.ForStatementNode;
import flask.ast.nodes.statements.compound.FunctionDefNode;
import flask.ast.nodes.statements.compound.WhileStatementNode;
import flask.ast.nodes.statements.imports.FromImportNode;
import flask.ast.nodes.statements.simple.AssignmentNode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import semantic.SemanticAnalyzer;
import semantic.scope.Scope;
import semantic.scope.ScopeType;
import semantic.scope.SymbolTable;
import semantic.symbol.Symbol;
import semantic.symbol.SymbolType;
import template.ast.TemplateProgramNode;
import template.ast.jinja.JinjaBlockNode;
import template.ast.jinja.JinjaForNode;
import template.ast.jinja.JinjaIfNode;
import template.ast.jinja.JinjaMacroNode;
import template.ast.jinja.JinjaVariableNode;

/**
 * Exercises the semantic analysis infrastructure (SemanticAnalyzer,
 * SymbolTable, Scope, Symbol, SemanticError) against hand-built ASTs, the
 * same way {@link GeneratorPhaseTest} exercises the Generator.
 */
public class SemanticAnalyzerTest {

    public static void main(String[] args) {
        runFlaskScopeTest();
        runTupleDestructuringTest();
        runJinjaScopeTest();
        runSharedSymbolTableTest();
        runWhileAndClassDefSmokeTest();
        runJinjaBlockAndIfSmokeTest();
        System.out.println("Semantic analyzer test passed");
    }

    /**
     * from flask import Flask
     * app = Flask(__name__)
     * def show_items(prefix):
     *     items = ['Apple', 'Banana']
     *     for item in items:
     *         x = item
     */
    private static void runFlaskScopeTest() {
        List<Statement> loopBody = new ArrayList<>();
        loopBody.add(new AssignmentNode(new IdentifierNode("x"), new IdentifierNode("item")));

        List<Statement> functionBody = new ArrayList<>();
        functionBody.add(new AssignmentNode(new IdentifierNode("items"),
            new ListNode(List.of(new LiteralNode("Apple"), new LiteralNode("Banana")))));
        functionBody.add(new ForStatementNode(new IdentifierNode("item"), new IdentifierNode("items"),
            loopBody, List.of(), 0, 0));

        List<Parameter> params = List.of(new Parameter("prefix", null, null));
        FunctionDefNode functionDef = new FunctionDefNode("show_items", List.of(), params, functionBody, null);

        List<Statement> topLevel = new ArrayList<>();
        topLevel.add(new FromImportNode("flask", List.of("Flask"), 0, 0));
        topLevel.add(new AssignmentNode(new IdentifierNode("app"),
            new FunctionCallNode(new IdentifierNode("Flask"), List.of(new IdentifierNode("__name__")),
                new LinkedHashMap<>())));
        topLevel.add(functionDef);

        ProgramNode pythonAst = new ProgramNode(topLevel);

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(pythonAst);

        if (analyzer.hasErrors() || !analyzer.getErrors().isEmpty()) {
            throw new AssertionError("Expected no errors to be reported yet, got: " + analyzer.getErrors());
        }

        SymbolTable table = analyzer.getSymbolTable();
        List<Scope> scopes = table.getAllScopes();
        if (scopes.size() != 3) {
            throw new AssertionError("Expected 3 scopes (Global, Function, Loop), got " + scopes.size());
        }

        Scope global = scopes.get(0);
        Scope function = scopes.get(1);
        Scope loop = scopes.get(2);

        assertScope(global, ScopeType.GLOBAL, 0, null);
        assertScope(function, ScopeType.FUNCTION, 1, global);
        assertScope(loop, ScopeType.LOOP, 2, function);

        assertSymbol(global.resolveLocally("app"), SymbolType.VARIABLE);
        assertSymbol(global.resolveLocally("show_items"), SymbolType.FUNCTION);

        assertSymbol(function.resolveLocally("prefix"), SymbolType.PARAMETER);
        assertSymbol(function.resolveLocally("items"), SymbolType.VARIABLE);

        assertSymbol(loop.resolveLocally("item"), SymbolType.LOOP_VARIABLE);
        assertSymbol(loop.resolveLocally("x"), SymbolType.VARIABLE);

        // resolve() must walk all the way up the parent chain: "app" is
        // only defined in Global, three levels above Loop.
        Symbol resolvedFromLoop = loop.resolve("app");
        if (resolvedFromLoop == null || resolvedFromLoop.getType() != SymbolType.VARIABLE) {
            throw new AssertionError("Expected 'app' to resolve up the scope chain from the Loop scope");
        }
        if (loop.resolve("does_not_exist") != null) {
            throw new AssertionError("Expected an undefined name to resolve to null");
        }
    }

    /**
     * pairs = []
     * for a, b in pairs:
     *     total = a
     */
    private static void runTupleDestructuringTest() {
        List<Statement> loopBody = new ArrayList<>();
        loopBody.add(new AssignmentNode(new IdentifierNode("total"), new IdentifierNode("a")));

        Expression tupleTarget = new TupleNode(List.of(new IdentifierNode("a"), new IdentifierNode("b")), 0, 0);
        List<Statement> statements = new ArrayList<>();
        statements.add(new AssignmentNode(new IdentifierNode("pairs"), new ListNode(List.of())));
        statements.add(new ForStatementNode(tupleTarget, new IdentifierNode("pairs"), loopBody, List.of(), 0, 0));

        ProgramNode pythonAst = new ProgramNode(statements);

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(pythonAst);

        if (!analyzer.getErrors().isEmpty()) {
            throw new AssertionError("Expected no errors, got: " + analyzer.getErrors());
        }

        Scope loop = analyzer.getSymbolTable().getAllScopes().get(1);
        assertSymbol(loop.resolveLocally("a"), SymbolType.LOOP_VARIABLE);
        assertSymbol(loop.resolveLocally("b"), SymbolType.LOOP_VARIABLE);
        assertSymbol(loop.resolveLocally("total"), SymbolType.VARIABLE);
    }

    /**
     * render_template('t.html', items=[])
     * {% for item in items %}{{ item }}{% endfor %}
     * {% macro greet(name, title) %}{{ name }}{% endmacro %}
     */
    private static void runJinjaScopeTest() {
        List<Statement> pythonStatements = new ArrayList<>();
        pythonStatements.add(new FromImportNode("flask", List.of("render_template"), 0, 0));
        pythonStatements.add(new AssignmentNode(new IdentifierNode("result"),
            new FunctionCallNode(new IdentifierNode("render_template"), List.of(new LiteralNode("t.html")),
                Map.of("items", new ListNode(List.of())))));
        ProgramNode pythonAst = new ProgramNode(pythonStatements);

        TemplateProgramNode templateAst = new TemplateProgramNode();
        templateAst.addJinjaElement(new JinjaForNode("item", "items", List.of(new JinjaVariableNode("item"))));
        templateAst.addJinjaElement(new JinjaMacroNode("greet", List.of("name", "title"),
            List.of(new JinjaVariableNode("name")), 0, 0));

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(pythonAst).analyze(templateAst);

        if (!analyzer.getErrors().isEmpty()) {
            throw new AssertionError("Expected no errors to be reported yet, got: " + analyzer.getErrors());
        }

        List<Scope> scopes = analyzer.getSymbolTable().getAllScopes();
        // Global (from the Python side) + template root + for-loop + macro.
        if (scopes.size() != 4) {
            throw new AssertionError("Expected 4 scopes (Global, template root, for-loop, macro), got " + scopes.size());
        }

        Scope root = scopes.get(1);
        Scope forScope = scopes.get(2);
        Scope macroScope = scopes.get(3);

        assertScope(root, ScopeType.JINJA, 0, null);
        assertScope(forScope, ScopeType.JINJA, 1, root);
        assertScope(macroScope, ScopeType.JINJA, 1, root);

        // The loop variable belongs to the for-loop's own scope, not the root.
        if (root.isDefinedLocally("item")) {
            throw new AssertionError("Loop variable 'item' should not leak into the template root scope");
        }
        assertSymbol(forScope.resolveLocally("item"), SymbolType.LOOP_VARIABLE);

        // The macro's own name belongs to the ENCLOSING (root) scope.
        assertSymbol(root.resolveLocally("greet"), SymbolType.FUNCTION);
        assertSymbol(macroScope.resolveLocally("name"), SymbolType.PARAMETER);
        assertSymbol(macroScope.resolveLocally("title"), SymbolType.PARAMETER);
    }

    /** One SemanticAnalyzer, fed both ASTs, must keep two independent root scopes. */
    private static void runSharedSymbolTableTest() {
        ProgramNode pythonAst = new ProgramNode(List.of(
            new FromImportNode("flask", List.of("render_template"), 0, 0),
            new AssignmentNode(new IdentifierNode("name"), new LiteralNode("Ali")),
            new AssignmentNode(new IdentifierNode("result"),
                new FunctionCallNode(new IdentifierNode("render_template"), List.of(new LiteralNode("t.html")),
                    Map.of("name", new IdentifierNode("name"))))));

        TemplateProgramNode templateAst = new TemplateProgramNode();
        templateAst.addJinjaElement(new JinjaVariableNode("name"));

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(pythonAst).analyze(templateAst);

        if (!analyzer.getErrors().isEmpty()) {
            throw new AssertionError("Expected no errors, got: " + analyzer.getErrors());
        }

        List<Scope> roots = new ArrayList<>();
        for (Scope scope : analyzer.getSymbolTable().getAllScopes()) {
            if (scope.isRoot()) {
                roots.add(scope);
            }
        }
        if (roots.size() != 2) {
            throw new AssertionError("Expected two independent root scopes (Global and Jinja), got " + roots.size());
        }
        boolean hasGlobalRoot = roots.get(0).getType() == ScopeType.GLOBAL || roots.get(1).getType() == ScopeType.GLOBAL;
        boolean hasJinjaRoot = roots.get(0).getType() == ScopeType.JINJA || roots.get(1).getType() == ScopeType.JINJA;
        if (!hasGlobalRoot || !hasJinjaRoot) {
            throw new AssertionError("Expected one Global root and one Jinja root, got " + roots);
        }
    }

    /**
     * flag = True
     * while flag:
     *     class Helper:
     *         value = 1
     *     flag = False
     *
     * Verifies While pushes a Loop scope, and that ClassDefNode -
     * intentionally left un-overridden, since Class Scope is not one of
     * the four required scope kinds - pushes no scope of its own: "value"
     * (assigned inside the class body) lands directly in the Loop scope,
     * right alongside "flag".
     */
    private static void runWhileAndClassDefSmokeTest() {
        List<Statement> classBody = new ArrayList<>();
        classBody.add(new AssignmentNode(new IdentifierNode("value"), new LiteralNode(1)));
        ClassDefNode classDef = new ClassDefNode("Helper", List.of(), List.of(), classBody, 0, 0);

        List<Statement> whileBody = new ArrayList<>();
        whileBody.add(classDef);
        whileBody.add(new AssignmentNode(new IdentifierNode("flag"), new LiteralNode(Boolean.FALSE)));

        List<Statement> statements = new ArrayList<>();
        statements.add(new AssignmentNode(new IdentifierNode("flag"), new LiteralNode(Boolean.TRUE)));
        statements.add(new WhileStatementNode(new IdentifierNode("flag"), whileBody, List.of(), 0, 0));

        ProgramNode pythonAst = new ProgramNode(statements);

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(pythonAst);

        if (!analyzer.getErrors().isEmpty()) {
            throw new AssertionError("Expected no errors, got: " + analyzer.getErrors());
        }

        List<Scope> scopes = analyzer.getSymbolTable().getAllScopes();
        if (scopes.size() != 2) {
            throw new AssertionError(
                "Expected 2 scopes (Global, Loop) - ClassDefNode must not push its own scope, got " + scopes.size());
        }

        Scope global = scopes.get(0);
        Scope loop = scopes.get(1);
        assertScope(global, ScopeType.GLOBAL, 0, null);
        assertScope(loop, ScopeType.LOOP, 1, global);

        assertSymbol(loop.resolveLocally("value"), SymbolType.VARIABLE);
        assertSymbol(loop.resolveLocally("flag"), SymbolType.VARIABLE);
    }

    /**
     * render_template('t.html', items=[], flag=True, x=1)
     * {% block content %}{% for item in items %}{{ item }}{% endfor %}{% endblock %}
     * {% if flag %}{{ x }}{% endif %}
     *
     * Verifies JinjaBlockNode and JinjaIfNode - both intentionally left
     * un-overridden - push no scope of their own and do not crash the
     * traversal: the nested for-loop's scope parent is the template ROOT
     * directly, not an intermediate block scope.
     */
    private static void runJinjaBlockAndIfSmokeTest() {
        Map<String, Expression> context = new LinkedHashMap<>();
        context.put("items", new ListNode(List.of()));
        context.put("flag", new LiteralNode(Boolean.TRUE));
        context.put("x", new LiteralNode(1));
        List<Statement> pythonStatements = new ArrayList<>();
        pythonStatements.add(new FromImportNode("flask", List.of("render_template"), 0, 0));
        pythonStatements.add(new AssignmentNode(new IdentifierNode("result"),
            new FunctionCallNode(new IdentifierNode("render_template"), List.of(new LiteralNode("t.html")), context)));
        ProgramNode pythonAst = new ProgramNode(pythonStatements);

        JinjaForNode forNode = new JinjaForNode("item", "items", List.of(new JinjaVariableNode("item")));
        JinjaBlockNode blockNode = new JinjaBlockNode("content", List.of(forNode), 0, 0);
        JinjaIfNode ifNode = new JinjaIfNode("flag", List.of(new JinjaVariableNode("x")));

        TemplateProgramNode templateAst = new TemplateProgramNode();
        templateAst.addJinjaElement(blockNode);
        templateAst.addJinjaElement(ifNode);

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(pythonAst).analyze(templateAst);

        if (!analyzer.getErrors().isEmpty()) {
            throw new AssertionError("Expected no errors, got: " + analyzer.getErrors());
        }

        List<Scope> scopes = analyzer.getSymbolTable().getAllScopes();
        // Global (from the Python side) + template root + for-loop.
        if (scopes.size() != 3) {
            throw new AssertionError(
                "Expected 3 scopes (Global, root, for-loop) - block/if must not push their own scopes, got " + scopes.size());
        }

        Scope root = scopes.get(1);
        Scope forScope = scopes.get(2);
        assertScope(root, ScopeType.JINJA, 0, null);
        assertScope(forScope, ScopeType.JINJA, 1, root);
        assertSymbol(forScope.resolveLocally("item"), SymbolType.LOOP_VARIABLE);
    }

    private static void assertScope(Scope scope, ScopeType expectedType, int expectedDepth, Scope expectedParent) {
        if (scope.getType() != expectedType) {
            throw new AssertionError("Expected scope type " + expectedType + " but got " + scope.getType());
        }
        if (scope.getDepth() != expectedDepth) {
            throw new AssertionError("Expected scope depth " + expectedDepth + " but got " + scope.getDepth());
        }
        if (scope.getParent() != expectedParent) {
            throw new AssertionError("Scope " + scope + " did not have the expected parent");
        }
    }

    private static void assertSymbol(Symbol symbol, SymbolType expectedType) {
        if (symbol == null) {
            throw new AssertionError("Expected a symbol to be defined but found none");
        }
        if (symbol.getType() != expectedType) {
            throw new AssertionError("Expected symbol type " + expectedType + " but got " + symbol.getType());
        }
    }
}
