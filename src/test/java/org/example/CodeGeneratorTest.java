package org.example;

import flask.ast.nodes.Expression;
import flask.ast.nodes.expressions.atoms.IdentifierNode;
import flask.ast.nodes.expressions.atoms.LiteralNode;
import flask.ast.nodes.expressions.atoms.TupleNode;
import flask.ast.nodes.statements.ProgramNode;
import generator.CodeGenerator;
import generator.Context;
import generator.CssGenerator;
import generator.FinalDocumentGenerator;
import generator.GenerationSupport;
import generator.HtmlGenerator;
import generator.JinjaGenerator;
import generator.PythonGenerator;
import generator.SourceMapping;
import pipeline.CompilerPipeline;
import template.ast.TemplateProgramNode;
import template.ast.css.CssStylesheetNode;
import template.ast.html.HtmlNode;
import template.ast.html.StyleElementNode;

import java.util.List;

/**
 * Exercises the Requirement 5 code generation architecture built on top of
 * the existing, unmodified Lexer/Parser/AST/Semantic Analysis phases.
 *
 * Like {@link GeneratorPhaseTest} and {@link SemanticAnalyzerTest}, most
 * cases here parse real source through the existing, untouched
 * {@link CompilerPipeline#buildPythonAst} / {@link CompilerPipeline#buildTemplateAst}
 * so generation is verified against exactly what the real parser produces,
 * not just against hand-built trees. A few cases build nodes by hand where
 * that is the only way to reach them (e.g. {@link TupleNode}, which the
 * current grammar's {@code atom} rule cannot itself produce — see
 * {@link #runPythonTupleTest()}).
 */
public class CodeGeneratorTest {

    public static void main(String[] args) {
        runPythonLiteralsAndAtomsTest();
        runPythonOperatorPrecedenceTest();
        runPythonCompoundStatementsTest();
        runPythonComprehensionsAndLambdaTest();
        runPythonTupleTest();
        runPythonRoundTripTest();

        runJinjaVariableAndFilterTest();
        runJinjaExpressionTreePrecedenceTest();
        runJinjaControlFlowTest();
        runJinjaMacroExtendsIncludeCommentTest();
        runJinjaRoundTripTest();

        runCssGeneratorTest();

        runHtmlElementAndAttributeTest();
        runHtmlVoidElementTest();
        runHtmlWhitespaceFilteringTest();
        runHtmlStyleDelegationTest();

        runCodeGeneratorSemanticGateTest();
        runCodeGeneratorPlainGenerateTest();
        runCodeGeneratorResolvedContextPipelineTest();

        runFinalDocumentGeneratorTopLevelMergeTest();
        runFinalDocumentGeneratorCssInjectionTest();
        runFinalDocumentGeneratorMultipleStylesheetsTest();
        runFinalDocumentGeneratorOnlyOneTreeTest();
        runFinalDocumentGeneratorNestedScopeLimitationTest();
        runCodeGeneratorFinalDocumentSemanticGateTest();
        runCodeGeneratorFinalDocumentFullWorkflowTest();

        runSourceMappingAndIndentTest();

        System.out.println("Code generator (Requirement 5) test passed");
    }

    // ------------------------------------------------------------------
    // PythonGenerator
    // ------------------------------------------------------------------

    private static void runPythonLiteralsAndAtomsTest() {
        ProgramNode ast = CompilerPipeline.buildPythonAst(String.join("\n",
                "s = 'it\\'s'",
                "n = 42",
                "pi = 3.5",
                "t = True",
                "f = False",
                "nothing = None",
                "name = identifier",
                "items = [1, 2, 3]",
                "empty_list = []",
                "mapping = {'a': 1, 'b': 2}",
                "people = {'x', 'y'}",
                ""
        ));
        String generated = new PythonGenerator().generate(ast);
        assertContains("string literal with escaped quote", generated, "s = 'it\\'s'");
        assertContains("int literal", generated, "n = 42");
        assertContains("float literal", generated, "pi = 3.5");
        assertContains("True literal", generated, "t = True");
        assertContains("False literal", generated, "f = False");
        assertContains("None literal", generated, "nothing = None");
        assertContains("identifier", generated, "name = identifier");
        assertContains("list literal", generated, "items = [1, 2, 3]");
        assertContains("empty list", generated, "empty_list = []");
        assertContains("dict literal", generated, "mapping = {'a': 1, 'b': 2}");
        assertContains("set literal", generated, "people = {'x', 'y'}");
        System.out.println("runPythonLiteralsAndAtomsTest passed");
    }

    private static void runPythonOperatorPrecedenceTest() {
        ProgramNode ast = CompilerPipeline.buildPythonAst(String.join("\n",
                "a = 1 + 2 * 3",
                "b = (1 + 2) * 3",
                "c = 2 ** 3 ** 2",
                "d = -(x + y)",
                "e = not (a and b)",
                "f = a < b < c",
                ""
        ));
        String generated = new PythonGenerator().generate(ast);
        // Multiplication binds tighter than addition, so no parens needed here.
        assertContains("no redundant parens", generated, "a = 1 + 2 * 3");
        // But grouped addition inside multiplication must keep its parens.
        assertContains("necessary parens preserved", generated, "b = (1 + 2) * 3");
        // ** is right-associative, so chained ** never needs parens.
        assertContains("right-associative power", generated, "c = 2 ** 3 ** 2");
        // Unary minus of a lower-precedence binary op needs parens.
        assertContains("unary minus wraps binary op", generated, "d = -(x + y)");
        assertContains("not wraps and", generated, "e = not (a and b)");
        assertContains("chained compare", generated, "f = a < b < c");

        // The generated text must itself be valid, re-parseable Python.
        ProgramNode reparsed = CompilerPipeline.buildPythonAst(generated);
        assertEquals("round-trip statement count", ast.getStatements().size(), reparsed.getStatements().size());

        // ** is right-associative, so a *left*-grouped power tree needs the
        // opposite parenthesization from a right-grouped one; the grammar
        // itself can only ever build the (already-covered) right-grouped
        // shape, so this is checked directly against a hand-built tree.
        Expression left = (Expression) new flask.ast.nodes.expressions.atoms.IdentifierNode("a", 0, 0);
        Expression mid = (Expression) new flask.ast.nodes.expressions.atoms.IdentifierNode("b", 0, 0);
        Expression right = (Expression) new flask.ast.nodes.expressions.atoms.IdentifierNode("c", 0, 0);
        Expression leftGroupedPower = new flask.ast.nodes.expressions.operations.BinaryOpNode(left, "**", mid, 0, 0);
        Expression outerPower = new flask.ast.nodes.expressions.operations.BinaryOpNode(leftGroupedPower, "**", right, 0, 0);
        String powerGenerated = outerPower.accept(new PythonGenerator());
        assertEquals("left-grouped power keeps its parens", "(a ** b) ** c", powerGenerated);

        System.out.println("runPythonOperatorPrecedenceTest passed");
    }

    private static void runPythonCompoundStatementsTest() {
        String source = String.join("\n",
                "@app.route('/x')",
                "def handler(a, b=1, *args, **kwargs):",
                "    if a > 0:",
                "        return a",
                "    elif b > 0:",
                "        return b",
                "    else:",
                "        return 0",
                "",
                "class Widget(Base):",
                "    def method(self):",
                "        for x in items:",
                "            if x:",
                "                break",
                "        else:",
                "            pass",
                "        while True:",
                "            continue",
                "        try:",
                "            risky()",
                "        except ValueError as e:",
                "            handle(e)",
                "        finally:",
                "            cleanup()",
                "        with open('f') as fh:",
                "            fh.read()",
                "        assert self.ok, 'must be ok'",
                "        del self.temp",
                "        global counter",
                "        return None",
                ""
        );
        ProgramNode ast = CompilerPipeline.buildPythonAst(source);
        String generated = new PythonGenerator().generate(ast);

        assertContains("decorator", generated, "@app.route('/x')");
        assertContains("def with defaults/varargs/kwargs", generated, "def handler(a, b=1, *args, **kwargs):");
        assertContains("if", generated, "if a > 0:");
        assertContains("elif", generated, "elif b > 0:");
        assertContains("else", generated, "else:");
        assertContains("class with base", generated, "class Widget(Base):");
        assertContains("for/else", generated, "for x in items:");
        assertContains("while", generated, "while True:");
        assertContains("except as", generated, "except ValueError as e:");
        assertContains("finally", generated, "finally:");
        assertContains("with as", generated, "with open('f') as fh:");
        assertContains("assert with message", generated, "assert self.ok, 'must be ok'");
        assertContains("del", generated, "del self.temp");
        assertContains("global", generated, "global counter");

        // Indentation must be well-formed: re-parsing must succeed and
        // reproduce the same top-level shape.
        ProgramNode reparsed = CompilerPipeline.buildPythonAst(generated);
        assertEquals("round-trip statement count", ast.getStatements().size(), reparsed.getStatements().size());
        System.out.println("runPythonCompoundStatementsTest passed");
    }

    private static void runPythonComprehensionsAndLambdaTest() {
        ProgramNode ast = CompilerPipeline.buildPythonAst(String.join("\n",
                "squares = [x * x for x in range(10) if x % 2 == 0]",
                "lookup = {k: v for k, v in pairs}",
                "unique = {x for x in items}",
                "total = sum(x * x for x in range(5))",
                "fn = lambda x, y: x + y",
                "thunk = lambda: 42",
                ""
        ));
        String generated = new PythonGenerator().generate(ast);
        assertContains("list comprehension with filter", generated,
                "squares = [x * x for x in range(10) if x % 2 == 0]");
        // "for k, v in pairs" is a tuple-unpacking comprehension target; the
        // existing FlaskASTBuilder stores such multi-name targets as a single
        // IdentifierNode built from ANTLR's getText() (which joins tokens with
        // no separating whitespace), so "k, v" faithfully regenerates as
        // "k,v" — a pre-existing tokenization characteristic (see
        // PythonGenerator's class Javadoc), not something introduced here.
        assertContains("dict comprehension", generated, "lookup = {k: v for k,v in pairs}");
        assertContains("set comprehension", generated, "unique = {x for x in items}");
        // Generator expression as sole call argument must NOT be double-wrapped.
        assertContains("generator expression, no double parens", generated,
                "total = sum(x * x for x in range(5))");
        assertContains("lambda with params", generated, "fn = lambda x, y: x + y");
        assertContains("lambda with no params", generated, "thunk = lambda: 42");

        ProgramNode reparsed = CompilerPipeline.buildPythonAst(generated);
        assertEquals("round-trip statement count", ast.getStatements().size(), reparsed.getStatements().size());
        System.out.println("runPythonComprehensionsAndLambdaTest passed");
    }

    /**
     * TupleNode cannot currently be produced by parsing any source through
     * this grammar (its {@code atom} rule is {@code LPAREN expression RPAREN}
     * with a mandatory, singular expression — no comma, no empty case), so
     * this is verified directly against a hand-built node instead, the same
     * way {@code GeneratorPhaseTest} builds nodes by hand where useful.
     */
    private static void runPythonTupleTest() {
        PythonGenerator generator = new PythonGenerator();

        String empty = new TupleNode(List.of(), 0, 0).accept(generator);
        assertEquals("empty tuple", "()", empty);

        String single = new TupleNode(List.of((Expression) new LiteralNode(1, 0, 0)), 0, 0).accept(generator);
        assertEquals("single-element tuple keeps trailing comma", "(1,)", single);

        String multi = new TupleNode(List.of(
                (Expression) new IdentifierNode("a", 0, 0),
                (Expression) new IdentifierNode("b", 0, 0)), 0, 0).accept(generator);
        assertEquals("multi-element tuple", "(a, b)", multi);

        System.out.println("runPythonTupleTest passed");
    }

    /** A larger, representative program: everything should round-trip and be deterministic. */
    private static void runPythonRoundTripTest() {
        String source = String.join("\n",
                "import os",
                "from flask import Flask, render_template",
                "",
                "app = Flask(__name__)",
                "",
                "@app.route('/users/<int:user_id>')",
                "def get_user(user_id, verbose=False):",
                "    if user_id > 0 and verbose:",
                "        message = 'positive'",
                "    else:",
                "        message = 'other'",
                "    return render_template('user.html', user_id=user_id, message=message)",
                ""
        );
        ProgramNode ast = CompilerPipeline.buildPythonAst(source);
        PythonGenerator generator = new PythonGenerator();
        String generated = generator.generate(ast);

        ProgramNode reparsed = CompilerPipeline.buildPythonAst(generated);
        assertEquals("round-trip statement count", ast.getStatements().size(), reparsed.getStatements().size());

        String generatedAgain = new PythonGenerator().generate(ast);
        assertEquals("deterministic generation", generated, generatedAgain);

        if (generator.getSourceMap().isEmpty()) {
            throw new AssertionError("Expected a non-empty source map after generation");
        }
        System.out.println("runPythonRoundTripTest passed");
    }

    // ------------------------------------------------------------------
    // JinjaGenerator
    // ------------------------------------------------------------------

    private static void runJinjaVariableAndFilterTest() {
        TemplateProgramNode ast = CompilerPipeline.buildTemplateAst(String.join("\n",
                "{{ name }}",
                "{{ user.profile.name }}",
                "{{ name|upper }}",
                "{{ price|round(2) }}",
                ""
        ));
        String generated = new JinjaGenerator().generate(ast);
        assertContains("plain variable", generated, "{{ name }}");
        assertContains("dotted chain", generated, "{{ user.profile.name }}");
        assertContains("filter, no args", generated, "{{ name|upper }}");
        assertContains("filter with args", generated, "{{ price|round(2) }}");
        System.out.println("runJinjaVariableAndFilterTest passed");
    }

    private static void runJinjaExpressionTreePrecedenceTest() {
        TemplateProgramNode ast = CompilerPipeline.buildTemplateAst(String.join("\n",
                "{{ a + b * c }}",
                "{{ (a + b) * c }}",
                "{{ items[0].name }}",
                "{{ loop.cycle(a, b) }}",
                "{{ (a + b)|round }}",
                "{{ not (a and b) }}",
                "{{ a + b < c }}",
                "{{ (a < b) < c }}",
                ""
        ));
        String generated = new JinjaGenerator().generate(ast);
        assertContains("no redundant parens", generated, "{{ a + b * c }}");
        assertContains("necessary parens preserved", generated, "{{ (a + b) * c }}");
        assertContains("subscript then attribute", generated, "{{ items[0].name }}");
        assertContains("call with args", generated, "{{ loop.cycle(a, b) }}");
        assertContains("filter over parenthesized binary op", generated, "{{ (a + b)|round }}");
        assertContains("not wraps and (lower precedence)", generated, "{{ not (a and b) }}");
        assertContains("additive under compare needs no parens", generated, "{{ a + b < c }}");
        assertContains("nested compare keeps its parens", generated, "{{ (a < b) < c }}");
        System.out.println("runJinjaExpressionTreePrecedenceTest passed");
    }

    private static void runJinjaControlFlowTest() {
        TemplateProgramNode ast = CompilerPipeline.buildTemplateAst(String.join("\n",
                "{% if user.age >= 18 %}",
                "{{ 'adult' }}",
                "{% elif user.age > 0 %}",
                "{{ 'minor' }}",
                "{% else %}",
                "{{ 'unknown' }}",
                "{% endif %}",
                "{% for item in items %}",
                "{{ item.name }}",
                "{% else %}",
                "no items",
                "{% endfor %}",
                ""
        ));
        String generated = new JinjaGenerator().generate(ast);
        assertContains("if with tree condition", generated, "{% if user.age >= 18 %}");
        assertContains("elif with tree condition", generated, "{% elif user.age > 0 %}");
        assertContains("else", generated, "{% else %}");
        assertContains("endif", generated, "{% endif %}");
        assertContains("for with tree iterable", generated, "{% for item in items %}");
        assertContains("endfor", generated, "{% endfor %}");

        TemplateProgramNode reparsed = CompilerPipeline.buildTemplateAst(generated);
        if (reparsed.getJinjaElements().isEmpty()) {
            throw new AssertionError("Expected generated control flow to re-parse into Jinja elements");
        }
        System.out.println("runJinjaControlFlowTest passed");
    }

    private static void runJinjaMacroExtendsIncludeCommentTest() {
        TemplateProgramNode ast = CompilerPipeline.buildTemplateAst(String.join("\n",
                "{% extends \"base.html\" %}",
                "{% include \"partial.html\" %}",
                "{% macro greet(name) %}",
                "{{ name }}",
                "{% endmacro %}",
                "{% block content %}",
                "hi",
                "{% endblock %}",
                "{# a comment #}",
                ""
        ));
        String generated = new JinjaGenerator().generate(ast);
        assertContains("extends", generated, "{% extends \"base.html\" %}");
        assertContains("include", generated, "{% include \"partial.html\" %}");
        assertContains("macro with parameter", generated, "{% macro greet(name) %}");
        assertContains("endmacro", generated, "{% endmacro %}");
        assertContains("block", generated, "{% block content %}");
        assertContains("endblock", generated, "{% endblock %}");
        assertContains("comment", generated, "{# a comment #}");
        System.out.println("runJinjaMacroExtendsIncludeCommentTest passed");
    }

    private static void runJinjaRoundTripTest() {
        String source = String.join("\n",
                "{% for item in items %}",
                "{{ item.name|upper }}",
                "{% endfor %}",
                ""
        );
        TemplateProgramNode ast = CompilerPipeline.buildTemplateAst(source);
        JinjaGenerator generator = new JinjaGenerator();
        String generated = generator.generate(ast);

        TemplateProgramNode reparsed = CompilerPipeline.buildTemplateAst(generated);
        assertEquals("round-trip top-level jinja element count",
                ast.getJinjaElements().size(), reparsed.getJinjaElements().size());

        String generatedAgain = new JinjaGenerator().generate(ast);
        assertEquals("deterministic generation", generated, generatedAgain);

        if (generator.getSourceMap().isEmpty()) {
            throw new AssertionError("Expected a non-empty source map after generation");
        }
        System.out.println("runJinjaRoundTripTest passed");
    }

    // ------------------------------------------------------------------
    // CssGenerator
    // ------------------------------------------------------------------

    private static void runCssGeneratorTest() {
        TemplateProgramNode ast = CompilerPipeline.buildTemplateAst(String.join("\n",
                "<style>",
                "  body { color: red; margin: 0 auto; }",
                "  .card, .card-highlight { padding: 10px !important; }",
                "  @media screen { .card { width: 100%; } }",
                "</style>",
                ""
        ));
        CssStylesheetNode stylesheet = findStyleStylesheet(ast);
        String generated = new CssGenerator().generate(stylesheet);

        assertContains("declaration", generated, "color: red;");
        assertContains("multi-selector rule", generated, ".card, .card-highlight {");
        assertContains("important", generated, "padding: 10px !important;");
        assertContains("at-rule with nested rule", generated, "@media screen {");
        System.out.println("runCssGeneratorTest passed");
    }

    // ------------------------------------------------------------------
    // HtmlGenerator
    // ------------------------------------------------------------------

    private static void runHtmlElementAndAttributeTest() {
        TemplateProgramNode ast = CompilerPipeline.buildTemplateAst(String.join("\n",
                "<div class=\"container\" data-id=\"42\" hidden>",
                "  <p>Hello</p>",
                "</div>",
                ""
        ));
        String generated = new HtmlGenerator().generate(ast);
        assertContains("attribute with value", generated, "class=\"container\"");
        assertContains("another attribute with value", generated, "data-id=\"42\"");
        assertContains("boolean attribute, no value", generated, "hidden");
        assertContains("single-text-child stays inline", generated, "<p>Hello</p>");

        TemplateProgramNode reparsed = CompilerPipeline.buildTemplateAst(generated);
        assertEquals("round-trip top-level html element count",
                ast.getHtmlElements().size(), reparsed.getHtmlElements().size());
        System.out.println("runHtmlElementAndAttributeTest passed");
    }

    private static void runHtmlVoidElementTest() {
        TemplateProgramNode ast = CompilerPipeline.buildTemplateAst(String.join("\n",
                "<div>",
                "  <img src=\"a.png\" />",
                "  <br />",
                "  <input type=\"text\" />",
                "</div>",
                ""
        ));
        String generated = new HtmlGenerator().generate(ast);
        assertContains("img self-closes, no closing tag", generated, "<img src=\"a.png\" />");
        if (generated.contains("</img>")) {
            throw new AssertionError("Void element must never get a closing tag: " + generated);
        }
        assertContains("br self-closes", generated, "<br />");
        assertContains("input self-closes", generated, "<input type=\"text\" />");
        System.out.println("runHtmlVoidElementTest passed");
    }

    private static void runHtmlWhitespaceFilteringTest() {
        TemplateProgramNode ast = CompilerPipeline.buildTemplateAst(String.join("\n",
                "<ul>",
                "",
                "    <li>a</li>",
                "",
                "    <li>b</li>",
                "",
                "</ul>",
                ""
        ));
        String generated = new HtmlGenerator().generate(ast);
        // Whitespace-only text between sibling tags should not produce blank lines.
        if (generated.contains("\n\n")) {
            throw new AssertionError("Expected whitespace-only text nodes to be filtered out, got:\n" + generated);
        }
        assertContains("first item", generated, "<li>a</li>");
        assertContains("second item", generated, "<li>b</li>");
        System.out.println("runHtmlWhitespaceFilteringTest passed");
    }

    private static void runHtmlStyleDelegationTest() {
        TemplateProgramNode ast = CompilerPipeline.buildTemplateAst(String.join("\n",
                "<head>",
                "  <style>",
                "    body { color: blue; }",
                "  </style>",
                "</head>",
                ""
        ));
        HtmlGenerator htmlGenerator = new HtmlGenerator();
        String generated = htmlGenerator.generate(ast);
        assertContains("style tag present", generated, "<style>");
        assertContains("delegated css content", generated, "color: blue;");
        assertContains("style tag closed", generated, "</style>");
        if (htmlGenerator.getCssGenerator().getSourceMap().isEmpty()) {
            throw new AssertionError("Expected the internally-delegated CssGenerator to have recorded a source map too");
        }
        System.out.println("runHtmlStyleDelegationTest passed");
    }

    // ------------------------------------------------------------------
    // CodeGenerator (orchestrator)
    // ------------------------------------------------------------------

    private static void runCodeGeneratorSemanticGateTest() {
        ProgramNode pythonAst = CompilerPipeline.buildPythonAst("x = 1\n");
        TemplateProgramNode templateAst = CompilerPipeline.buildTemplateAst("<p>hi</p>\n");

        boolean threw = false;
        try {
            new CodeGenerator(pythonAst, templateAst, false).generate();
        } catch (IllegalStateException expected) {
            threw = true;
        }
        if (!threw) {
            throw new AssertionError("generate() must throw IllegalStateException when semanticAnalysisSucceeded=false");
        }

        threw = false;
        try {
            new CodeGenerator(pythonAst, templateAst, false).generateWithResolvedContext();
        } catch (IllegalStateException expected) {
            threw = true;
        }
        if (!threw) {
            throw new AssertionError(
                    "generateWithResolvedContext() must throw IllegalStateException when semanticAnalysisSucceeded=false");
        }

        // Must NOT throw, and must produce output, once the flag is true.
        CodeGenerator ok = new CodeGenerator(pythonAst, templateAst, true).generate();
        if (!ok.isGenerated()) {
            throw new AssertionError("Expected isGenerated() to be true after generate() with semanticAnalysisSucceeded=true");
        }
        System.out.println("runCodeGeneratorSemanticGateTest passed");
    }

    private static void runCodeGeneratorPlainGenerateTest() {
        ProgramNode pythonAst = CompilerPipeline.buildPythonAst(String.join("\n",
                "x = 1",
                "def f():",
                "    return x",
                ""
        ));
        TemplateProgramNode templateAst = CompilerPipeline.buildTemplateAst(String.join("\n",
                "<p>{{ x }}</p>",
                ""
        ));

        CodeGenerator generator = new CodeGenerator(pythonAst, templateAst, true).generate();
        assertContains("python generated", generator.getGeneratedPythonSource(), "def f():");
        assertContains("jinja generated, unresolved", generator.getGeneratedJinjaSource(), "{{ x }}");
        if (generator.getIntermediateContext() != null) {
            throw new AssertionError("Plain generate() must not populate an intermediate context");
        }
        System.out.println("runCodeGeneratorPlainGenerateTest passed");
    }

    private static void runCodeGeneratorResolvedContextPipelineTest() {
        ProgramNode pythonAst = CompilerPipeline.buildPythonAst(String.join("\n",
                "from flask import Flask, render_template",
                "app = Flask(__name__)",
                "",
                "@app.route('/')",
                "def home():",
                "    title = 'Welcome'",
                "    count = 3",
                "    return render_template('index.html', title=title, count=count)",
                ""
        ));
        TemplateProgramNode templateAst = CompilerPipeline.buildTemplateAst(String.join("\n",
                "<h1>{{ title }}</h1>",
                "<p>Count: {{ count }}</p>",
                ""
        ));

        CodeGenerator generator = new CodeGenerator(pythonAst, templateAst, true).generateWithResolvedContext();

        Context context = generator.getIntermediateContext();
        if (context == null) {
            throw new AssertionError("Expected an intermediate Context to be populated");
        }
        assertEquals("resolved title", "Welcome", context.get("title"));
        assertEquals("resolved count", 3, context.get("count"));

        String jinja = generator.getGeneratedJinjaSource();
        if (jinja.contains("{{ title }}") || jinja.contains("{{ count }}")) {
            throw new AssertionError("Expected variables to be resolved away, got:\n" + jinja);
        }
        assertContains("resolved title value present", jinja, "Welcome");
        assertContains("resolved count value present", jinja, "3");
        System.out.println("runCodeGeneratorResolvedContextPipelineTest passed");
    }

    // ------------------------------------------------------------------
    // FinalDocumentGenerator (Prompt 2: merging HTML + Jinja into one document)
    // ------------------------------------------------------------------

    /** Sibling-level (no wrapping element) top-level content must interleave in correct source order. */
    private static void runFinalDocumentGeneratorTopLevelMergeTest() {
        TemplateProgramNode ast = CompilerPipeline.buildTemplateAst(String.join("\n",
                "<p>Intro</p>",
                "{{ name }}",
                "<p>Outro</p>",
                ""
        ));
        String merged = new FinalDocumentGenerator().generate(ast);

        int introIdx = merged.indexOf("Intro");
        int nameIdx = merged.indexOf("{{ name }}");
        int outroIdx = merged.indexOf("Outro");
        if (introIdx < 0 || nameIdx < 0 || outroIdx < 0) {
            throw new AssertionError("Expected all three top-level pieces present, got:\n" + merged);
        }
        if (!(introIdx < nameIdx && nameIdx < outroIdx)) {
            throw new AssertionError("Expected source order Intro < {{ name }} < Outro, got:\n" + merged);
        }
        System.out.println("runFinalDocumentGeneratorTopLevelMergeTest passed");
    }

    /** A <style> tag anywhere in the HTML tree must have its CSS generated (not copied as text) and counted. */
    private static void runFinalDocumentGeneratorCssInjectionTest() {
        TemplateProgramNode ast = CompilerPipeline.buildTemplateAst(String.join("\n",
                "<head>",
                "  <style>",
                "    body { color: red; margin: 0; }",
                "  </style>",
                "</head>",
                ""
        ));
        FinalDocumentGenerator generator = new FinalDocumentGenerator();
        String merged = generator.generate(ast);

        assertContains("style tag present", merged, "<style>");
        assertContains("declaration generated from the CSS AST, not copy-pasted", merged, "color: red;");
        assertContains("second declaration also generated", merged, "margin: 0;");
        assertEquals("exactly one stylesheet injected", 1, generator.countInjectedStylesheets());
        if (generator.getCombinedSourceMap().isEmpty()) {
            throw new AssertionError("Expected a non-empty combined source map after generate()");
        }
        System.out.println("runFinalDocumentGeneratorCssInjectionTest passed");
    }

    /** Multiple <style> tags must each be counted and each contribute their own generated CSS. */
    private static void runFinalDocumentGeneratorMultipleStylesheetsTest() {
        TemplateProgramNode ast = CompilerPipeline.buildTemplateAst(String.join("\n",
                "<head>",
                "  <style>body { color: red; }</style>",
                "</head>",
                "<footer>",
                "  <style>footer { color: blue; }</style>",
                "</footer>",
                ""
        ));
        FinalDocumentGenerator generator = new FinalDocumentGenerator();
        String merged = generator.generate(ast);

        assertContains("first stylesheet", merged, "color: red;");
        assertContains("second stylesheet", merged, "color: blue;");
        assertEquals("two stylesheets injected", 2, generator.countInjectedStylesheets());
        System.out.println("runFinalDocumentGeneratorMultipleStylesheetsTest passed");
    }

    /** A tree with only HTML, or only Jinja, top-level content must not crash and must degrade gracefully. */
    private static void runFinalDocumentGeneratorOnlyOneTreeTest() {
        TemplateProgramNode htmlOnly = CompilerPipeline.buildTemplateAst("<p>just html</p>\n");
        String htmlMerged = new FinalDocumentGenerator().generate(htmlOnly);
        assertContains("html-only merge", htmlMerged, "<p>just html</p>");

        TemplateProgramNode jinjaOnly = CompilerPipeline.buildTemplateAst("{{ value }}\n");
        String jinjaMerged = new FinalDocumentGenerator().generate(jinjaOnly);
        assertContains("jinja-only merge", jinjaMerged, "{{ value }}");

        TemplateProgramNode empty = CompilerPipeline.buildTemplateAst("");
        String emptyMerged = new FinalDocumentGenerator().generate(empty);
        assertEquals("empty template merges to empty string", "", emptyMerged);
        System.out.println("runFinalDocumentGeneratorOnlyOneTreeTest passed");
    }

    /**
     * A Jinja construct hoisted out from several levels deep inside a
     * wrapping HTML element (the realistic case — a real page's content
     * normally sits inside {@code <html><body>...}) is correctly placed back
     * at its original nesting depth, not after the whole top-level element
     * it was found under. This used to be an honest, documented limitation:
     * nothing in the existing AST recorded where an element's content ends,
     * so re-nesting a hoisted construct looked like it would require either
     * guessing at an unrecorded span or duplicating {@link HtmlGenerator}'s
     * own tag-rendering logic elsewhere. Neither turned out to be necessary:
     * {@link template.ast.html.JinjaHostNode} records <em>where</em> (not how
     * long) a construct was found — a placeholder left right there among the
     * surrounding {@link template.ast.html.HtmlNode} children, letting
     * {@link HtmlGenerator#visitJinjaHostNode} inline whatever it resolves to
     * at exactly that position — so the HTML/Jinja independence {@link
     * template.TemplateASTBuilder} relies on for semantic analysis is kept
     * intact while generation still recombines the two correctly.
     */
    private static void runFinalDocumentGeneratorNestedScopeLimitationTest() {
        TemplateProgramNode ast = CompilerPipeline.buildTemplateAst(String.join("\n",
                "<html>",
                "<body>",
                "<h1>Header</h1>",
                "{{ title }}",
                "</body>",
                "</html>",
                ""
        ));
        String merged = new FinalDocumentGenerator().generate(ast);

        assertContains("outer structure preserved", merged, "<h1>Header</h1>");
        assertContains("hoisted content still present, not dropped", merged, "{{ title }}");
        int headerIdx = merged.indexOf("<h1>Header</h1>");
        int titleIdx = merged.indexOf("{{ title }}");
        int bodyEnd = merged.indexOf("</body>");
        if (!(headerIdx >= 0 && bodyEnd >= 0 && headerIdx < titleIdx && titleIdx < bodyEnd)) {
            throw new AssertionError(
                    "Expected the hoisted content re-nested between <h1>Header</h1> and </body>, got:\n" + merged);
        }
        System.out.println("runFinalDocumentGeneratorNestedScopeLimitationTest passed");
    }

    // ------------------------------------------------------------------
    // CodeGenerator.generateFinalDocument() (Prompt 2)
    // ------------------------------------------------------------------

    private static void runCodeGeneratorFinalDocumentSemanticGateTest() {
        ProgramNode pythonAst = CompilerPipeline.buildPythonAst("x = 1\n");
        TemplateProgramNode templateAst = CompilerPipeline.buildTemplateAst("<p>{{ x }}</p>\n");

        boolean threw = false;
        try {
            new CodeGenerator(pythonAst, templateAst, false).generateFinalDocument();
        } catch (IllegalStateException expected) {
            threw = true;
        }
        if (!threw) {
            throw new AssertionError(
                    "generateFinalDocument() must throw IllegalStateException when semanticAnalysisSucceeded=false");
        }
        System.out.println("runCodeGeneratorFinalDocumentSemanticGateTest passed");
    }

    /**
     * The complete pipeline this task describes, end to end: real Python
     * source with a {@code render_template(...)} call, real template source,
     * through {@link CompilerPipeline#compile}, semantic-analysis-gated,
     * variables extracted from the Python AST, transferred into the Jinja AST
     * (via the existing, untouched {@code Generator}), generated, and merged
     * into one Final HTML Document with CSS injected.
     */
    private static void runCodeGeneratorFinalDocumentFullWorkflowTest() {
        String pythonSource = String.join("\n",
                "from flask import Flask, render_template",
                "app = Flask(__name__)",
                "",
                "@app.route('/')",
                "def home():",
                "    title = 'Welcome'",
                "    count = 3",
                "    return render_template('index.html', title=title, count=count)",
                ""
        );
        String templateSource = String.join("\n",
                "<p>Header</p>",
                "{{ title }}",
                "<p>Items: {{ count }}</p>",
                "<style>body { color: navy; }</style>",
                ""
        );

        CompilerPipeline.Result result = CompilerPipeline.compile(pythonSource, templateSource);
        if (result.hasSemanticErrors()) {
            throw new AssertionError("Unexpected semantic errors: " + result.getSemanticErrors());
        }

        CodeGenerator generator = new CodeGenerator(result.getPythonAst(), result.getTemplateAst(), true)
                .generateFinalDocument();

        String doc = generator.getFinalHtmlDocument();
        if (doc == null) {
            throw new AssertionError("Expected a non-null final HTML document");
        }
        if (doc.contains("{{ title }}") || doc.contains("{{ count }}")) {
            throw new AssertionError("Expected variables to be resolved away in the final document, got:\n" + doc);
        }
        assertContains("resolved title in final document", doc, "Welcome");
        assertContains("resolved count in final document", doc, "3");
        assertContains("surrounding html preserved", doc, "<p>Header</p>");
        assertContains("css injected via CssGenerator, not copied", doc, "color: navy;");
        assertEquals("exactly one stylesheet injected",
                1, generator.getFinalDocumentGenerator().countInjectedStylesheets());

        // Variables genuinely came from the Python AST, through the
        // "Intermediate Generation Data", not fabricated by this test.
        assertEquals("title traced from Python AST", "Welcome", generator.getIntermediateContext().get("title"));
        assertEquals("count traced from Python AST", 3, generator.getIntermediateContext().get("count"));

        System.out.println("runCodeGeneratorFinalDocumentFullWorkflowTest passed");
    }

    // ------------------------------------------------------------------
    // GenerationSupport / SourceMapping
    // ------------------------------------------------------------------

    private static void runSourceMappingAndIndentTest() {
        GenerationSupport support = new GenerationSupport("  ");
        assertEquals("initial indent", "", support.indent());
        assertEquals("initial indent level", 0, support.getIndentLevel());
        support.increaseIndent();
        assertEquals("one level", "  ", support.indent());
        assertEquals("indent level after one increase", 1, support.getIndentLevel());
        support.increaseIndent();
        assertEquals("two levels", "    ", support.indent());
        support.decreaseIndent();
        assertEquals("back to one level", "  ", support.indent());
        support.decreaseIndent();
        support.decreaseIndent(); // must not go negative
        assertEquals("floor at zero", "", support.indent());
        assertEquals("indent level floors at zero, not negative", 0, support.getIndentLevel());

        support.mark("NodeA", 1, 2);
        support.mark("NodeB", 3, 4);
        List<SourceMapping> map = support.getSourceMap();
        assertEquals("source map size", 2, map.size());
        assertEquals("first entry node name", "NodeA", map.get(0).getNodeName());
        assertEquals("first entry sequence", 0, map.get(0).getSequence());
        assertEquals("second entry node name", "NodeB", map.get(1).getNodeName());
        assertEquals("second entry sequence", 1, map.get(1).getSequence());
        assertEquals("second entry line", 3, map.get(1).getSourceLine());

        support.reset();
        if (!support.getSourceMap().isEmpty()) {
            throw new AssertionError("Expected reset() to clear the source map");
        }
        assertEquals("reset also clears indent", "", support.indent());

        System.out.println("runSourceMappingAndIndentTest passed");
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private static CssStylesheetNode findStyleStylesheet(TemplateProgramNode ast) {
        for (HtmlNode node : ast.getHtmlElements()) {
            CssStylesheetNode found = findStyleStylesheet(node);
            if (found != null) {
                return found;
            }
        }
        throw new AssertionError("No <style> element found in parsed template");
    }

    private static CssStylesheetNode findStyleStylesheet(HtmlNode node) {
        if (node instanceof StyleElementNode) {
            return ((StyleElementNode) node).getStylesheet();
        }
        if (node instanceof template.ast.html.HtmlElementNode) {
            for (HtmlNode child : ((template.ast.html.HtmlElementNode) node).getChildren()) {
                CssStylesheetNode found = findStyleStylesheet(child);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static void assertContains(String label, String haystack, String needle) {
        if (haystack == null || !haystack.contains(needle)) {
            throw new AssertionError(label + ": expected generated text to contain [" + needle + "] but got:\n" + haystack);
        }
    }

    private static void assertEquals(String label, Object expected, Object actual) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(label + ": expected [" + expected + "] but got [" + actual + "]");
        }
    }
}
