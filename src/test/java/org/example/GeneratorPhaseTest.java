package org.example;

import flask.ast.nodes.Expression;
import flask.ast.nodes.expressions.access.AttributeAccessNode;
import flask.ast.nodes.expressions.access.FunctionCallNode;
import flask.ast.nodes.expressions.access.SubscriptNode;
import flask.ast.nodes.expressions.atoms.DictNode;
import flask.ast.nodes.expressions.atoms.IdentifierNode;
import flask.ast.nodes.expressions.atoms.ListNode;
import flask.ast.nodes.expressions.atoms.LiteralNode;
import flask.ast.nodes.statements.ProgramNode;
import flask.ast.nodes.statements.simple.AssignmentNode;
import flask.ast.nodes.statements.simple.ReturnNode;
import flask.ast.nodes.statements.compound.FunctionDefNode;
import generator.Generator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import template.ast.TemplateProgramNode;
import template.ast.jinja.JinjaForNode;
import template.ast.jinja.JinjaIfNode;
import template.ast.jinja.JinjaNode;
import template.ast.jinja.JinjaVariableNode;

public class GeneratorPhaseTest {

    public static void main(String[] args) {
        runSimpleGeneratorTest();
        runBindingResolutionTest();
        runFunctionScopedRenderTest();
        runNestedAssignmentTest();
        runKwargsSpreadTest();
        runIfEliminationTest();
        runForUnrollTest();
        System.out.println("Generator phase test passed");
    }

    private static void runSimpleGeneratorTest() {
        ProgramNode pythonAst = buildPythonAst();
        TemplateProgramNode templateAst = buildTemplateAst();

        Generator generator = new Generator(pythonAst, templateAst, null);
        TemplateProgramNode transformed = generator.generate();

        if (!"Ali".equals(generator.getContext().get("name"))) {
            throw new AssertionError("Expected context name to be Ali");
        }
        if (!Integer.valueOf(20).equals(generator.getContext().get("age"))) {
            throw new AssertionError("Expected context age to be 20");
        }

        List<JinjaNode> jinjaElements = transformed.getJinjaElements();
        if (jinjaElements.isEmpty()) {
            throw new AssertionError("Expected transformed template to contain Jinja elements");
        }
        if (!(jinjaElements.get(0) instanceof template.ast.jinja.LiteralNode)) {
            throw new AssertionError("Expected Jinja variable to be replaced by LiteralNode");
        }
    }

    private static void runBindingResolutionTest() {
        ProgramNode pythonAst = buildBindingPythonAst();
        TemplateProgramNode templateAst = buildBindingTemplateAst();

        Generator generator = new Generator(pythonAst, templateAst, null);
        TemplateProgramNode transformed = generator.generate();

        List<JinjaNode> jinjaElements = transformed.getJinjaElements();
        if (jinjaElements.size() != 4) {
            throw new AssertionError("Expected 4 transformed Jinja elements");
        }

        assertLiteralValue(jinjaElements.get(0), "Ali");
        assertLiteralValue(jinjaElements.get(1), "Ali");
        assertLiteralValue(jinjaElements.get(2), "first");
        assertLiteralValue(jinjaElements.get(3), "Demo App");
    }

    private static ProgramNode buildPythonAst() {
        List<Expression> renderArgs = new ArrayList<>();
        renderArgs.add(new flask.ast.nodes.expressions.atoms.LiteralNode("index.html"));

        LinkedHashMap<String, Expression> kwargs = new LinkedHashMap<>();
        kwargs.put("name", new IdentifierNode("name"));
        kwargs.put("age", new IdentifierNode("age"));

        List<Expression> returnValues = new ArrayList<>();
        returnValues.add(new FunctionCallNode(new IdentifierNode("render_template"), renderArgs, kwargs));

        List<flask.ast.nodes.Statement> statements = new ArrayList<>();
        statements.add(new AssignmentNode(new IdentifierNode("name"), new flask.ast.nodes.expressions.atoms.LiteralNode("Ali")));
        statements.add(new AssignmentNode(new IdentifierNode("age"), new flask.ast.nodes.expressions.atoms.LiteralNode(20)));
        statements.add(new ReturnNode(returnValues));

        return new ProgramNode(statements);
    }

    private static ProgramNode buildBindingPythonAst() {
        List<Expression> renderArgs = new ArrayList<>();
        renderArgs.add(new LiteralNode("index.html"));

        LinkedHashMap<String, Expression> kwargs = new LinkedHashMap<>();
        kwargs.put("name", new IdentifierNode("name"));
        kwargs.put("user", new IdentifierNode("user"));
        kwargs.put("items", new IdentifierNode("items"));
        kwargs.put("config", new IdentifierNode("config"));

        List<Expression> returnValues = new ArrayList<>();
        returnValues.add(new FunctionCallNode(new IdentifierNode("render_template"), renderArgs, kwargs));

        List<flask.ast.nodes.Statement> statements = new ArrayList<>();
        statements.add(new AssignmentNode(new IdentifierNode("name"), new LiteralNode("Ali")));
        statements.add(new AssignmentNode(new IdentifierNode("user"), new DictNode(new ArrayList<>(Arrays.asList(new DictNode.DictItem(new LiteralNode("name"), new LiteralNode("Ali")))))));
        statements.add(new AssignmentNode(new IdentifierNode("items"), new ListNode(new ArrayList<>(Arrays.asList(new LiteralNode("first"), new LiteralNode("second"))))));
        statements.add(new AssignmentNode(new IdentifierNode("config"), new DictNode(new ArrayList<>(Arrays.asList(new DictNode.DictItem(new LiteralNode("title"), new LiteralNode("Demo App")))))));
        statements.add(new ReturnNode(returnValues));

        return new ProgramNode(statements);
    }

    private static TemplateProgramNode buildTemplateAst() {
        TemplateProgramNode program = new TemplateProgramNode();
        program.addJinjaElement(new JinjaVariableNode("name"));
        return program;
    }

    private static TemplateProgramNode buildBindingTemplateAst() {
        TemplateProgramNode program = new TemplateProgramNode();
        program.addJinjaElement(new JinjaVariableNode("name"));
        program.addJinjaElement(new JinjaVariableNode("user.name"));
        program.addJinjaElement(new JinjaVariableNode("items[0]"));
        program.addJinjaElement(new JinjaVariableNode("config.title"));
        return program;
    }

    /** render_template() nested inside a Flask view function must still be found. */
    private static void runFunctionScopedRenderTest() {
        List<Expression> renderArgs = new ArrayList<>();
        renderArgs.add(new LiteralNode("index.html"));
        LinkedHashMap<String, Expression> kwargs = new LinkedHashMap<>();
        kwargs.put("name", new IdentifierNode("name"));

        List<flask.ast.nodes.Statement> functionBody = new ArrayList<>();
        functionBody.add(new AssignmentNode(new IdentifierNode("name"), new LiteralNode("Ali")));
        List<Expression> returnValues = new ArrayList<>();
        returnValues.add(new FunctionCallNode(new IdentifierNode("render_template"), renderArgs, kwargs));
        functionBody.add(new ReturnNode(returnValues));

        FunctionDefNode viewFunction = new FunctionDefNode("index", List.of(), List.of(), functionBody, null);
        ProgramNode pythonAst = new ProgramNode(List.of(viewFunction));

        TemplateProgramNode templateAst = new TemplateProgramNode();
        templateAst.addJinjaElement(new JinjaVariableNode("name"));

        Generator generator = new Generator(pythonAst, templateAst, null);
        TemplateProgramNode transformed = generator.generate();

        if (!"Ali".equals(generator.getContext().get("name"))) {
            throw new AssertionError("Expected render_template() inside a function body to be found");
        }
        assertLiteralValue(transformed.getJinjaElements().get(0), "Ali");
    }

    /** Dict-index and object-property assignment targets must mutate the right container. */
    private static void runNestedAssignmentTest() {
        List<flask.ast.nodes.Statement> statements = new ArrayList<>();
        statements.add(new AssignmentNode(new IdentifierNode("user"), new DictNode(List.of())));
        statements.add(new AssignmentNode(
                new SubscriptNode(new IdentifierNode("user"), List.of(new LiteralNode("name"))),
                new LiteralNode("Ali")));
        statements.add(new AssignmentNode(new IdentifierNode("config"), new DictNode(List.of())));
        statements.add(new AssignmentNode(
                new AttributeAccessNode(new IdentifierNode("config"), "title"),
                new LiteralNode("Demo App")));

        List<Expression> renderArgs = new ArrayList<>();
        renderArgs.add(new LiteralNode("index.html"));
        LinkedHashMap<String, Expression> kwargs = new LinkedHashMap<>();
        kwargs.put("user", new IdentifierNode("user"));
        kwargs.put("config", new IdentifierNode("config"));
        List<Expression> returnValues = new ArrayList<>();
        returnValues.add(new FunctionCallNode(new IdentifierNode("render_template"), renderArgs, kwargs));
        statements.add(new ReturnNode(returnValues));

        ProgramNode pythonAst = new ProgramNode(statements);
        TemplateProgramNode templateAst = new TemplateProgramNode();
        templateAst.addJinjaElement(new JinjaVariableNode("user.name"));
        templateAst.addJinjaElement(new JinjaVariableNode("config.title"));

        Generator generator = new Generator(pythonAst, templateAst, null);
        TemplateProgramNode transformed = generator.generate();

        assertLiteralValue(transformed.getJinjaElements().get(0), "Ali");
        assertLiteralValue(transformed.getJinjaElements().get(1), "Demo App");
    }

    /** render_template("t.html", **context) should merge the spread dict's entries. */
    private static void runKwargsSpreadTest() {
        List<flask.ast.nodes.Statement> statements = new ArrayList<>();
        List<DictNode.DictItem> items = new ArrayList<>();
        items.add(new DictNode.DictItem(new LiteralNode("greeting"), new LiteralNode("Hi")));
        items.add(new DictNode.DictItem(new LiteralNode("tone"), new LiteralNode("formal")));
        statements.add(new AssignmentNode(new IdentifierNode("context"), new DictNode(items)));

        List<Expression> renderArgs = new ArrayList<>();
        renderArgs.add(new LiteralNode("index.html"));
        FunctionCallNode call = new FunctionCallNode(new IdentifierNode("render_template"), renderArgs,
                new LinkedHashMap<>(), List.of(), List.of(new IdentifierNode("context")));
        statements.add(new ReturnNode(List.of(call)));

        ProgramNode pythonAst = new ProgramNode(statements);
        TemplateProgramNode templateAst = new TemplateProgramNode();
        templateAst.addJinjaElement(new JinjaVariableNode("greeting"));
        templateAst.addJinjaElement(new JinjaVariableNode("tone"));

        Generator generator = new Generator(pythonAst, templateAst, null);
        TemplateProgramNode transformed = generator.generate();

        assertLiteralValue(transformed.getJinjaElements().get(0), "Hi");
        assertLiteralValue(transformed.getJinjaElements().get(1), "formal");
    }

    /** A {% if %} whose condition is fully known should collapse to just the taken branch. */
    private static void runIfEliminationTest() {
        List<flask.ast.nodes.Statement> statements = new ArrayList<>();
        statements.add(new AssignmentNode(new IdentifierNode("flag"), new LiteralNode(Boolean.TRUE)));
        statements.add(new AssignmentNode(new IdentifierNode("name"), new LiteralNode("Ali")));
        ProgramNode pythonAst = new ProgramNode(statements);

        TemplateProgramNode templateAst = new TemplateProgramNode();
        templateAst.addJinjaElement(new JinjaIfNode("flag", List.of(new JinjaVariableNode("name"))));

        Generator generator = new Generator(pythonAst, templateAst, null);
        TemplateProgramNode transformed = generator.generate();

        if (transformed.getJinjaElements().size() != 1) {
            throw new AssertionError("Expected the if-statement to collapse to a single node");
        }
        assertLiteralValue(transformed.getJinjaElements().get(0), "Ali");
    }

    /** A {% for %} over a statically-known list should unroll into one node per element. */
    private static void runForUnrollTest() {
        List<flask.ast.nodes.Statement> statements = new ArrayList<>();
        List<Expression> elements = List.of(new LiteralNode("a"), new LiteralNode("b"), new LiteralNode("c"));
        statements.add(new AssignmentNode(new IdentifierNode("items"), new ListNode(elements)));
        ProgramNode pythonAst = new ProgramNode(statements);

        TemplateProgramNode templateAst = new TemplateProgramNode();
        templateAst.addJinjaElement(new JinjaForNode("item", "items", List.of(new JinjaVariableNode("item"))));

        Generator generator = new Generator(pythonAst, templateAst, null);
        TemplateProgramNode transformed = generator.generate();

        if (transformed.getJinjaElements().size() != 3) {
            throw new AssertionError("Expected the for-loop to unroll into 3 nodes, got " + transformed.getJinjaElements().size());
        }
        assertLiteralValue(transformed.getJinjaElements().get(0), "a");
        assertLiteralValue(transformed.getJinjaElements().get(1), "b");
        assertLiteralValue(transformed.getJinjaElements().get(2), "c");
    }

    private static void assertLiteralValue(JinjaNode node, Object expected) {
        if (!(node instanceof template.ast.jinja.LiteralNode literalNode)) {
            throw new AssertionError("Expected LiteralNode but got " + node.getClass().getSimpleName());
        }
        if (!expected.equals(literalNode.getValue())) {
            throw new AssertionError("Expected literal value " + expected + " but got " + literalNode.getValue());
        }
    }
}
