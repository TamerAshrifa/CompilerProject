package org.example;

import flask.ast.nodes.Statement;
import flask.ast.nodes.expressions.access.FunctionCallNode;
import flask.ast.nodes.expressions.atoms.IdentifierNode;
import flask.ast.nodes.expressions.atoms.LiteralNode;
import flask.ast.nodes.helpers.Parameter;
import flask.ast.nodes.statements.ProgramNode;
import flask.ast.nodes.statements.compound.ForStatementNode;
import flask.ast.nodes.statements.compound.FunctionDefNode;
import flask.ast.nodes.statements.compound.WhileStatementNode;
import flask.ast.nodes.statements.imports.FromImportNode;
import flask.ast.nodes.statements.simple.AssignmentNode;
import flask.ast.nodes.statements.simple.BreakNode;
import flask.ast.nodes.statements.simple.ContinueNode;
import flask.ast.nodes.statements.simple.ReturnNode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import semantic.SemanticAnalyzer;
import semantic.error.SemanticError;
import semantic.error.SemanticErrorType;
import template.ast.TemplateProgramNode;
import template.ast.jinja.JinjaAttributeAccessNode;
import template.ast.jinja.JinjaCallNode;
import template.ast.jinja.JinjaExpressionNode;
import template.ast.jinja.JinjaForNode;
import template.ast.jinja.JinjaIdentifierNode;
import template.ast.jinja.JinjaIfNode;
import template.ast.jinja.JinjaMacroNode;
import template.ast.jinja.JinjaVariableNode;

/**
 * One (or a small handful of) hand-built AST test per semantic check
 * required by the current instructions — 7 Python, 7 Jinja2 — following
 * the same plain-{@code main()}/{@code AssertionError} style as {@link
 * GeneratorPhaseTest} and {@link SemanticAnalyzerTest}. Where useful, a
 * check's test is paired with a "this is NOT flagged" counterpart proving
 * the check does not false-positive on the equivalent valid code.
 */
public class SemanticErrorChecksTest {

    public static void main(String[] args) {
        testUndefinedVariable();
        testDefinedVariableIsNotFlagged();
        testDuplicateVariableDeclaration();
        testReassignmentInANestedScopeIsNotDuplicate();
        testFunctionCallTooFewArguments();
        testFunctionCallTooManyArguments();
        testFunctionCallCorrectArgumentCountIsNotFlagged();
        testDefaultParameterMakesArgumentOptional();
        testReturnOutsideFunction();
        testReturnInsideFunctionIsNotFlagged();
        testBreakOutsideLoop();
        testContinueOutsideLoop();
        testBreakInsideLoopIsNotFlagged();
        testBreakInsideNestedFunctionCannotEscapeToOuterLoop();
        testDuplicateFunctionDefinition();
        testDuplicateFunctionDefinitionValidCounterpart();
        testDuplicateParameterNames();
        testDuplicateParameterNamesValidCounterpart();
        testContinueInsideLoopIsNotFlagged();
        testMultiplePythonErrorsAreAllCollected();

        testUndefinedTemplateVariableInOutput();
        testUndefinedVariableInsideIf();
        testUndefinedVariableInsideIfValidCounterpart();
        testUndefinedVariableInsideFor();
        testExternallySuppliedContextVariableIsNotFlagged();
        testInvalidAttributeAccessOnMacroFlatChain();
        testInvalidAttributeAccessOnMacroStructuredTree();
        testAttributeAccessOnLoopVariableIsNotFlagged();
        testDuplicateLoopVariable();
        testSiblingForLoopsReusingLoopVariableNameIsNotFlagged();
        testUndefinedMacroCall();
        testDefinedMacroCallIsNotFlagged();
        testMultipleJinjaErrorsAreAllCollected();

        System.out.println("Semantic error checks test passed");
    }

    // ==================== Python Semantic Errors ====================

    /** #1: y = x  (x is never defined) */
    private static void testUndefinedVariable() {
        List<Statement> statements = List.of(
            new AssignmentNode(new IdentifierNode("y"), new IdentifierNode("x")));
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(statements));
        expectExactlyOneError(analyzer, SemanticErrorType.UNDEFINED_VARIABLE);
    }

    /** #1 counterpart: x = 1; y = x */
    private static void testDefinedVariableIsNotFlagged() {
        List<Statement> statements = List.of(
            new AssignmentNode(new IdentifierNode("x"), new LiteralNode(1)),
            new AssignmentNode(new IdentifierNode("y"), new IdentifierNode("x")));
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(statements));
        expectNoErrors(analyzer);
    }

    /** #2: x = 1; x = 2  (both in the same, module-level scope) */
    private static void testDuplicateVariableDeclaration() {
        List<Statement> statements = List.of(
            new AssignmentNode(new IdentifierNode("x"), new LiteralNode(1)),
            new AssignmentNode(new IdentifierNode("x"), new LiteralNode(2)));
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(statements));
        expectExactlyOneError(analyzer, SemanticErrorType.DUPLICATE_DEFINITION);
    }

    /** #2 boundary: x = 1; while x: x = 2  -- the inner "x" is a fresh binding in the Loop's own scope, not a duplicate. */
    private static void testReassignmentInANestedScopeIsNotDuplicate() {
        List<Statement> whileBody = List.of(new AssignmentNode(new IdentifierNode("x"), new LiteralNode(2)));
        List<Statement> statements = List.of(
            new AssignmentNode(new IdentifierNode("x"), new LiteralNode(1)),
            new WhileStatementNode(new IdentifierNode("x"), whileBody, List.of(), 0, 0));
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(statements));
        expectNoErrors(analyzer);
    }

    /** #3: def f(a, b): pass ; f(1)  -- too few arguments */
    private static void testFunctionCallTooFewArguments() {
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(programWithCall("f", List.of("a", "b"), List.of(new LiteralNode(1)))));
        expectExactlyOneError(analyzer, SemanticErrorType.ARGUMENT_MISMATCH);
    }

    /** #3: def f(a, b): pass ; f(1, 2, 3)  -- too many arguments */
    private static void testFunctionCallTooManyArguments() {
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(programWithCall("f", List.of("a", "b"),
            List.of(new LiteralNode(1), new LiteralNode(2), new LiteralNode(3)))));
        expectExactlyOneError(analyzer, SemanticErrorType.ARGUMENT_MISMATCH);
    }

    /** #3 counterpart: def f(a, b): pass ; f(1, 2) */
    private static void testFunctionCallCorrectArgumentCountIsNotFlagged() {
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(programWithCall("f", List.of("a", "b"),
            List.of(new LiteralNode(1), new LiteralNode(2)))));
        expectNoErrors(analyzer);
    }

    /** #3 nuance: def f(a, b=2): pass ; f(1)  -- "b" has a default, so one argument is enough. */
    private static void testDefaultParameterMakesArgumentOptional() {
        List<Parameter> params = List.of(
            new Parameter("a", null, null),
            new Parameter("b", null, new LiteralNode(2)));
        FunctionDefNode functionDef = new FunctionDefNode("f", List.of(), params, List.of(), null);
        List<Statement> statements = List.of(functionDef,
            new AssignmentNode(new IdentifierNode("result"),
                new FunctionCallNode(new IdentifierNode("f"), List.of(new LiteralNode(1)), new LinkedHashMap<>())));
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(statements));
        expectNoErrors(analyzer);
    }

    /** #4: a bare `return` at module level. */
    private static void testReturnOutsideFunction() {
        List<Statement> statements = List.of(new ReturnNode(List.of()));
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(statements));
        expectExactlyOneError(analyzer, SemanticErrorType.INVALID_RETURN);
    }

    /** #4 counterpart: def f(): return 1 */
    private static void testReturnInsideFunctionIsNotFlagged() {
        FunctionDefNode functionDef = new FunctionDefNode("f", List.of(), List.of(),
            List.of(new ReturnNode(List.of(new LiteralNode(1)))), null);
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(List.of(functionDef)));
        expectNoErrors(analyzer);
    }

    /** #5: a bare `break` at module level. */
    private static void testBreakOutsideLoop() {
        List<Statement> statements = List.of(new BreakNode(0, 0));
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(statements));
        expectExactlyOneError(analyzer, SemanticErrorType.INVALID_BREAK_OR_CONTINUE);
    }

    /** #5: a bare `continue` at module level. */
    private static void testContinueOutsideLoop() {
        List<Statement> statements = List.of(new ContinueNode(0, 0));
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(statements));
        expectExactlyOneError(analyzer, SemanticErrorType.INVALID_BREAK_OR_CONTINUE);
    }

    /** #5 counterpart: while True: break */
    private static void testBreakInsideLoopIsNotFlagged() {
        List<Statement> whileBody = List.of(new BreakNode(0, 0));
        List<Statement> statements = List.of(
            new WhileStatementNode(new LiteralNode(Boolean.TRUE), whileBody, List.of(), 0, 0));
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(statements));
        expectNoErrors(analyzer);
    }

    /**
     * #5 boundary: for i in range(3): def f(): break
     * "break" is lexically nested inside an outer loop, but it is INSIDE
     * ITS OWN function body with no loop of its own — it cannot reach out
     * and break the enclosing function's loop, so this must still be flagged.
     */
    private static void testBreakInsideNestedFunctionCannotEscapeToOuterLoop() {
        FunctionDefNode innerFunction = new FunctionDefNode("f", List.of(), List.of(),
            List.of(new BreakNode(0, 0)), null);
        List<Statement> forBody = List.of(innerFunction);
        FunctionCallNode rangeCall = new FunctionCallNode(new IdentifierNode("range"),
            List.of(new LiteralNode(3)), new LinkedHashMap<>());
        List<Statement> statements = List.of(
            new ForStatementNode(new IdentifierNode("i"), rangeCall, forBody, List.of(), 0, 0));
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(statements));
        expectExactlyOneError(analyzer, SemanticErrorType.INVALID_BREAK_OR_CONTINUE);
    }

    /** #6: def f(): pass ; def f(): pass */
    private static void testDuplicateFunctionDefinition() {
        FunctionDefNode first = new FunctionDefNode("f", List.of(), List.of(), List.of(), null);
        FunctionDefNode second = new FunctionDefNode("f", List.of(), List.of(), List.of(), null);
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(List.of(first, second)));
        expectExactlyOneError(analyzer, SemanticErrorType.DUPLICATE_DEFINITION);
    }

    /** #6 counterpart: def f(): pass ; def g(): pass  -- two distinct names, not a duplicate. */
    private static void testDuplicateFunctionDefinitionValidCounterpart() {
        FunctionDefNode first = new FunctionDefNode("f", List.of(), List.of(), List.of(), null);
        FunctionDefNode second = new FunctionDefNode("g", List.of(), List.of(), List.of(), null);
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(List.of(first, second)));
        expectNoErrors(analyzer);
    }

    /** #7: def f(a, a): pass */
    private static void testDuplicateParameterNames() {
        List<Parameter> params = List.of(new Parameter("a", null, null), new Parameter("a", null, null));
        FunctionDefNode functionDef = new FunctionDefNode("f", List.of(), params, List.of(), null);
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(List.of(functionDef)));
        expectExactlyOneError(analyzer, SemanticErrorType.DUPLICATE_DEFINITION);
    }

    /** #7 counterpart: def f(a, b): pass  -- two distinct parameter names, not a duplicate. */
    private static void testDuplicateParameterNamesValidCounterpart() {
        List<Parameter> params = List.of(new Parameter("a", null, null), new Parameter("b", null, null));
        FunctionDefNode functionDef = new FunctionDefNode("f", List.of(), params, List.of(), null);
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(List.of(functionDef)));
        expectNoErrors(analyzer);
    }

    /** #5 counterpart: while True: continue */
    private static void testContinueInsideLoopIsNotFlagged() {
        List<Statement> whileBody = List.of(new ContinueNode(0, 0));
        List<Statement> statements = List.of(
            new WhileStatementNode(new LiteralNode(Boolean.TRUE), whileBody, List.of(), 0, 0));
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(statements));
        expectNoErrors(analyzer);
    }

    /**
     * "Do NOT stop after the first error": one small program with three
     * unrelated violations (#1, #4, #5) must report all three, not just
     * the first one encountered.
     */
    private static void testMultiplePythonErrorsAreAllCollected() {
        List<Statement> statements = List.of(
            new AssignmentNode(new IdentifierNode("y"), new IdentifierNode("undefined_name")), // #1
            new ReturnNode(List.of()),                                                          // #4
            new BreakNode(0, 0));                                                                // #5
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(statements));

        List<SemanticError> errors = analyzer.getErrors();
        if (errors.size() != 3) {
            throw new AssertionError("Expected all 3 errors to be collected, got " + errors.size() + ": " + errors);
        }
        requireErrorOfType(errors, SemanticErrorType.UNDEFINED_VARIABLE);
        requireErrorOfType(errors, SemanticErrorType.INVALID_RETURN);
        requireErrorOfType(errors, SemanticErrorType.INVALID_BREAK_OR_CONTINUE);
    }

    // ==================== Jinja2 Semantic Errors ====================

    /** #1 / #2: {{ undefined_name }} with no definition anywhere and no Python context. */
    private static void testUndefinedTemplateVariableInOutput() {
        TemplateProgramNode templateAst = new TemplateProgramNode();
        templateAst.addJinjaElement(new JinjaVariableNode("undefined_name"));
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(templateAst);
        expectExactlyOneError(analyzer, SemanticErrorType.UNDEFINED_JINJA_VARIABLE);
    }

    /** #3: {% if undefined_flag %}...{% endif %} */
    private static void testUndefinedVariableInsideIf() {
        TemplateProgramNode templateAst = new TemplateProgramNode();
        templateAst.addJinjaElement(new JinjaIfNode("undefined_flag", List.of()));
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(templateAst);
        expectExactlyOneError(analyzer, SemanticErrorType.UNDEFINED_JINJA_VARIABLE);
    }

    /** #3 counterpart: {% macro show(flag) %}{% if flag %}yes{% endif %}{% endmacro %} -- "flag" is a defined macro parameter. */
    private static void testUndefinedVariableInsideIfValidCounterpart() {
        JinjaIfNode ifNode = new JinjaIfNode("flag", List.of());
        TemplateProgramNode templateAst = new TemplateProgramNode();
        templateAst.addJinjaElement(new JinjaMacroNode("show", List.of("flag"), List.of(ifNode), 0, 0));
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(templateAst);
        expectNoErrors(analyzer);
    }

    /** #4: {% for x in undefined_list %}...{% endfor %} */
    private static void testUndefinedVariableInsideFor() {
        TemplateProgramNode templateAst = new TemplateProgramNode();
        templateAst.addJinjaElement(new JinjaForNode("x", "undefined_list", List.of()));
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(templateAst);
        expectExactlyOneError(analyzer, SemanticErrorType.UNDEFINED_JINJA_VARIABLE);
    }

    /** #1-#4 counterpart: a name never bound in the template, but passed via render_template(**context), is not flagged. */
    private static void testExternallySuppliedContextVariableIsNotFlagged() {
        List<Statement> pythonStatements = List.of(
            new FromImportNode("flask", List.of("render_template"), 0, 0),
            new AssignmentNode(new IdentifierNode("result"),
                new FunctionCallNode(new IdentifierNode("render_template"), List.of(new LiteralNode("t.html")),
                    Map.of("items", new flask.ast.nodes.expressions.atoms.ListNode(List.of())))));

        TemplateProgramNode templateAst = new TemplateProgramNode();
        templateAst.addJinjaElement(new JinjaForNode("x", "items", List.of(new JinjaVariableNode("x"))));

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(pythonStatements)).analyze(templateAst);
        expectNoErrors(analyzer);
    }

    /** #5: a macro referenced as `greet.foo`, as a single flattened JinjaVariableNode chain. */
    private static void testInvalidAttributeAccessOnMacroFlatChain() {
        TemplateProgramNode templateAst = new TemplateProgramNode();
        templateAst.addJinjaElement(new JinjaMacroNode("greet", List.of(), List.of(), 0, 0));
        templateAst.addJinjaElement(new JinjaVariableNode("greet.foo"));
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(templateAst);
        expectExactlyOneError(analyzer, SemanticErrorType.INVALID_ATTRIBUTE_ACCESS);
    }

    /** #5: the same case via the structured expression tree (JinjaAttributeAccessNode), as a real parse with an operator would produce. */
    private static void testInvalidAttributeAccessOnMacroStructuredTree() {
        TemplateProgramNode templateAst = new TemplateProgramNode();
        templateAst.addJinjaElement(new JinjaMacroNode("greet", List.of(), List.of(), 0, 0));
        JinjaAttributeAccessNode access = new JinjaAttributeAccessNode(
            new JinjaIdentifierNode("greet", 0, 0), "foo", 0, 0);
        templateAst.addJinjaElement(new JinjaExpressionNode("greet.foo", access, 0, 0));
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(templateAst);
        expectExactlyOneError(analyzer, SemanticErrorType.INVALID_ATTRIBUTE_ACCESS);
    }

    /** #5 counterpart: {% for item in items %}{{ item.name }}{% endfor %} -- a loop variable, not a macro, so this is valid. */
    private static void testAttributeAccessOnLoopVariableIsNotFlagged() {
        JinjaVariableNode itemName = new JinjaVariableNode("item.name");
        TemplateProgramNode templateAst = new TemplateProgramNode();
        templateAst.addJinjaElement(new JinjaForNode("item", "items", List.of(itemName)));

        List<Statement> pythonStatements = List.of(
            new FromImportNode("flask", List.of("render_template"), 0, 0),
            new AssignmentNode(new IdentifierNode("result"),
                new FunctionCallNode(new IdentifierNode("render_template"), List.of(new LiteralNode("t.html")),
                    Map.of("items", new flask.ast.nodes.expressions.atoms.ListNode(List.of())))));

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(pythonStatements)).analyze(templateAst);
        expectNoErrors(analyzer);
    }

    /** #6: nested {% for %} loops reusing the same loop variable name. */
    private static void testDuplicateLoopVariable() {
        JinjaForNode innerFor = new JinjaForNode("item", "nested", List.of());
        JinjaForNode outerFor = new JinjaForNode("item", "items", List.of(innerFor));

        List<Statement> pythonStatements = List.of(
            new FromImportNode("flask", List.of("render_template"), 0, 0),
            new AssignmentNode(new IdentifierNode("result"),
                new FunctionCallNode(new IdentifierNode("render_template"), List.of(new LiteralNode("t.html")),
                    Map.of("items", new flask.ast.nodes.expressions.atoms.ListNode(List.of()),
                           "nested", new flask.ast.nodes.expressions.atoms.ListNode(List.of())))));

        TemplateProgramNode templateAst = new TemplateProgramNode();
        templateAst.addJinjaElement(outerFor);

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(pythonStatements)).analyze(templateAst);
        expectExactlyOneError(analyzer, SemanticErrorType.DUPLICATE_DEFINITION);
    }

    /**
     * #6 counterpart: two SEPARATE, sibling (not nested) {% for %} loops
     * both reusing "item" is fine — the first loop's scope is fully
     * popped before the second one's is pushed, so there is no active
     * outer "item" left to shadow by the time the second loop is checked.
     */
    private static void testSiblingForLoopsReusingLoopVariableNameIsNotFlagged() {
        JinjaForNode firstFor = new JinjaForNode("item", "items", List.of(new JinjaVariableNode("item")));
        JinjaForNode secondFor = new JinjaForNode("item", "others", List.of(new JinjaVariableNode("item")));

        List<Statement> pythonStatements = List.of(
            new FromImportNode("flask", List.of("render_template"), 0, 0),
            new AssignmentNode(new IdentifierNode("result"),
                new FunctionCallNode(new IdentifierNode("render_template"), List.of(new LiteralNode("t.html")),
                    Map.of("items", new flask.ast.nodes.expressions.atoms.ListNode(List.of()),
                           "others", new flask.ast.nodes.expressions.atoms.ListNode(List.of())))));

        TemplateProgramNode templateAst = new TemplateProgramNode();
        templateAst.addJinjaElement(firstFor);
        templateAst.addJinjaElement(secondFor);

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(new ProgramNode(pythonStatements)).analyze(templateAst);
        expectNoErrors(analyzer);
    }

    /** #7: {{ undefined_macro() }} with no matching {% macro %} anywhere. */
    private static void testUndefinedMacroCall() {
        JinjaCallNode call = new JinjaCallNode(new JinjaIdentifierNode("undefined_macro", 0, 0), List.of(), 0, 0);
        TemplateProgramNode templateAst = new TemplateProgramNode();
        templateAst.addJinjaElement(new JinjaExpressionNode("undefined_macro()", call, 0, 0));
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(templateAst);
        expectExactlyOneError(analyzer, SemanticErrorType.UNDEFINED_JINJA_MACRO);
    }

    /** #7 counterpart: {% macro helper() %}{% endmacro %}{{ helper() }} */
    private static void testDefinedMacroCallIsNotFlagged() {
        JinjaCallNode call = new JinjaCallNode(new JinjaIdentifierNode("helper", 0, 0), List.of(), 0, 0);
        TemplateProgramNode templateAst = new TemplateProgramNode();
        templateAst.addJinjaElement(new JinjaMacroNode("helper", List.of(), List.of(), 0, 0));
        templateAst.addJinjaElement(new JinjaExpressionNode("helper()", call, 0, 0));
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(templateAst);
        expectNoErrors(analyzer);
    }

    /** "Do NOT stop after the first error", Jinja2 side: three unrelated violations in one template must all be collected. */
    private static void testMultipleJinjaErrorsAreAllCollected() {
        TemplateProgramNode templateAst = new TemplateProgramNode();
        templateAst.addJinjaElement(new JinjaVariableNode("undefined_one"));               // #1/#2
        templateAst.addJinjaElement(new JinjaIfNode("undefined_two", List.of()));           // #3
        templateAst.addJinjaElement(new JinjaForNode("x", "undefined_three", List.of()));   // #4

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(templateAst);

        List<SemanticError> errors = analyzer.getErrors();
        if (errors.size() != 3) {
            throw new AssertionError("Expected all 3 errors to be collected, got " + errors.size() + ": " + errors);
        }
        for (SemanticError error : errors) {
            if (error.getType() != SemanticErrorType.UNDEFINED_JINJA_VARIABLE) {
                throw new AssertionError("Expected every error to be UNDEFINED_JINJA_VARIABLE, got: " + errors);
            }
        }
    }

    // ==================== Helpers ====================

    /** def <name>(<params...>): pass \n f(<args...>) */
    private static List<Statement> programWithCall(String functionName, List<String> paramNames, List<flask.ast.nodes.Expression> args) {
        List<Parameter> params = new ArrayList<>();
        for (String paramName : paramNames) {
            params.add(new Parameter(paramName, null, null));
        }
        FunctionDefNode functionDef = new FunctionDefNode(functionName, List.of(), params, List.of(), null);
        FunctionCallNode call = new FunctionCallNode(new IdentifierNode(functionName), args, new LinkedHashMap<>());
        return List.of(functionDef, new AssignmentNode(new IdentifierNode("result"), call));
    }

    private static SemanticError expectExactlyOneError(SemanticAnalyzer analyzer, SemanticErrorType expectedType) {
        List<SemanticError> errors = analyzer.getErrors();
        if (errors.size() != 1) {
            throw new AssertionError("Expected exactly 1 error, got " + errors.size() + ": " + errors);
        }
        if (errors.get(0).getType() != expectedType) {
            throw new AssertionError("Expected error type " + expectedType + " but got " + errors.get(0).getType());
        }
        if (errors.get(0).getLine() < 0 || errors.get(0).getMessage() == null || errors.get(0).getMessage().isEmpty()
                || errors.get(0).getNodeName() == null || errors.get(0).getNodeName().isEmpty()) {
            throw new AssertionError("Expected a well-formed error (message, line, node type), got: " + errors.get(0));
        }
        return errors.get(0);
    }

    private static void expectNoErrors(SemanticAnalyzer analyzer) {
        if (!analyzer.getErrors().isEmpty()) {
            throw new AssertionError("Expected no errors, got: " + analyzer.getErrors());
        }
    }

    private static void requireErrorOfType(List<SemanticError> errors, SemanticErrorType type) {
        for (SemanticError error : errors) {
            if (error.getType() == type) {
                return;
            }
        }
        throw new AssertionError("Expected an error of type " + type + " among: " + errors);
    }
}
