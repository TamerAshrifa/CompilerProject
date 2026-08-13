package org.example;

import generator.CodeGenerator;
import generator.CompilationResult;
import pipeline.CompilerPipeline;
import semantic.symbol.Symbol;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Integration tests for the fully connected pipeline added in this final
 * stage: {@link CompilerPipeline#compileToHtml}, which is the single entry
 * point this requirement asks for, wiring together every phase - Lexing,
 * Parsing, AST Construction, Semantic Analysis, and Code Generation - behind
 * one method call, and {@link CompilationResult}, the object it returns.
 *
 * <p>Each individual generator's own correctness (operator precedence,
 * control-flow rendering, CSS formatting, and so on) is already thoroughly
 * covered in isolation by {@link CodeGeneratorTest}; this file deliberately
 * does not repeat that. What it covers instead is specifically the
 * integration surface added in this stage: does one call to
 * {@code compileToHtml} really reach every phase in order, does it really
 * stop automatically at the semantic-error gate, is the existing
 * {@link CompilerPipeline#compile} still completely unaffected, and does the
 * whole thing hold together for realistic, non-trivial input.
 *
 * <p>Follows this project's existing test convention: a plain {@code main()}
 * calling one {@code runXxxTest()} per case, each asserting via
 * {@link AssertionError} - the same style as {@link CompilerPipelineTest},
 * {@link GeneratorPhaseTest}, {@link SemanticAnalyzerTest}, and
 * {@link CodeGeneratorTest}, all four of which are re-run, unmodified and
 * still passing, alongside this file (see the project's build/verification
 * notes) - not JUnit, matching every test actually written for this project
 * rather than the vestigial Maven-archetype {@code AppTest}.
 */
public class FullPipelineIntegrationTest {

    public static void main(String[] args) throws Exception {
        runAllFiveRequirementsInOneCallTest();
        runCompileToHtmlSemanticErrorGateTest();
        runExistingCompileMethodUnaffectedTest();
        runWriteFinalHtmlToFileTest();
        runWriteFinalHtmlToFileRefusesWhenNotGeneratedTest();
        runCompilationResultInvariantsTest();
        runComplexRealisticTemplateIntegrationTest();
        runCompileToHtmlDeterminismTest();
        runConvenencePassthroughsTest();

        System.out.println("Full pipeline integration test passed");
    }

    /**
     * One call to {@link CompilerPipeline#compileToHtml} must demonstrably
     * satisfy all five of this project's requirements at once: Lexer &amp;
     * Parser (tokens were consumed into a tree at all), Python/Jinja AST and
     * OOP Node hierarchy (the right node types come back, each generating
     * itself polymorphically), Semantic Analysis (symbols were actually
     * defined), and fully integrated Code Generation (a complete, correct
     * Final HTML Document, with CSS injected and Jinja resolved).
     */
    private static void runAllFiveRequirementsInOneCallTest() {
        String pythonSource = String.join("\n",
                "from flask import Flask, render_template",
                "app = Flask(__name__)",
                "",
                "@app.route('/dashboard')",
                "def dashboard():",
                "    username = 'Grace'",
                "    score = 97",
                "    return render_template('dashboard.html', username=username, score=score)",
                ""
        );
        String templateSource = String.join("\n",
                "<html>",
                "<head><style>h1 { color: teal; }</style></head>",
                "<body>",
                "<h1>Dashboard</h1>",
                "{{ username }}",
                "{{ score }}",
                "</body>",
                "</html>",
                ""
        );

        CompilationResult result = CompilerPipeline.compileToHtml(pythonSource, templateSource);

        // Requirement 1 (Lexer & Parser) + Requirement 2 (Python/Jinja AST):
        // real trees were built from real source text, not stubs.
        if (result.getPythonAst() == null || result.getPythonAst().getStatements().isEmpty()) {
            throw new AssertionError("Expected a non-empty Python AST");
        }
        if (result.getTemplateAst() == null || result.getTemplateAst().getHtmlElements().isEmpty()) {
            throw new AssertionError("Expected a non-empty Template AST");
        }

        // Requirement 3 (OOP Node hierarchy): the AST is not a flat token
        // list - a real FunctionDefNode was built and correctly nested a
        // ReturnNode inside its own body.
        boolean foundFunctionWithReturn = result.getPythonAst().getStatements().stream()
                .filter(s -> s instanceof flask.ast.nodes.statements.compound.FunctionDefNode)
                .map(s -> (flask.ast.nodes.statements.compound.FunctionDefNode) s)
                .anyMatch(fn -> fn.getBody().stream()
                        .anyMatch(stmt -> stmt instanceof flask.ast.nodes.statements.simple.ReturnNode));
        if (!foundFunctionWithReturn) {
            throw new AssertionError("Expected a FunctionDefNode containing a ReturnNode in the built AST");
        }

        // Requirement 4 (Semantic Analysis): symbols were actually defined,
        // not just "no errors".
        List<Symbol> symbols = result.getSemanticAnalyzer().getSymbolTable().getAllSymbols();
        if (symbols.isEmpty()) {
            throw new AssertionError("Expected semantic analysis to have defined at least one symbol");
        }
        if (result.hasSemanticErrors()) {
            throw new AssertionError("Expected no semantic errors, got: " + result.getSemanticErrors());
        }

        // Requirement 5 (fully integrated Code Generation): a complete Final
        // HTML Document, with the Python-derived values resolved and the CSS
        // injected - not a placeholder, not the raw unresolved template.
        if (!result.isFullyGenerated()) {
            throw new AssertionError("Expected code generation to have run");
        }
        String html = result.getFinalHtmlDocument();
        if (html == null || html.isEmpty()) {
            throw new AssertionError("Expected a non-empty Final HTML Document");
        }
        if (html.contains("{{ username }}") || html.contains("{{ score }}")) {
            throw new AssertionError("Expected variables to be resolved in the Final HTML Document, got:\n" + html);
        }
        if (!html.contains("Grace") || !html.contains("97")) {
            throw new AssertionError("Expected resolved values 'Grace' and '97' in the Final HTML Document, got:\n" + html);
        }
        if (!html.contains("color: teal;")) {
            throw new AssertionError("Expected injected CSS in the Final HTML Document, got:\n" + html);
        }
        if (result.getCodeGenerator().getFinalDocumentGenerator().countInjectedStylesheets() != 1) {
            throw new AssertionError("Expected exactly one injected stylesheet");
        }

        System.out.println("runAllFiveRequirementsInOneCallTest passed");
    }

    /**
     * Requirement 5, task 3: generation must stop automatically if semantic
     * errors exist - verified at the fully-integrated {@code compileToHtml}
     * level, not just on {@link CodeGenerator} directly (already covered by
     * {@link CodeGeneratorTest#runCodeGeneratorSemanticGateTest()}).
     */
    private static void runCompileToHtmlSemanticErrorGateTest() {
        String brokenPython = String.join("\n",
                "from flask import render_template",
                "",
                "def dashboard():",
                "    return render_template('dashboard.html', username=totally_undefined)",
                ""
        );
        String templateSource = "<h1>{{ username }}</h1>\n";

        CompilationResult result = CompilerPipeline.compileToHtml(brokenPython, templateSource);

        if (!result.hasSemanticErrors()) {
            throw new AssertionError("Expected semantic errors for an undefined variable");
        }
        if (result.getSemanticErrors().isEmpty()) {
            throw new AssertionError("hasSemanticErrors() is true but getSemanticErrors() is empty");
        }
        boolean foundUndefinedVariableError = result.getSemanticErrors().stream()
                .anyMatch(e -> e.toString().contains("totally_undefined"));
        if (!foundUndefinedVariableError) {
            throw new AssertionError("Expected an error mentioning 'totally_undefined', got: " + result.getSemanticErrors());
        }

        // Code generation must not merely be "unused" - it must never have run.
        if (result.isFullyGenerated()) {
            throw new AssertionError("Expected isFullyGenerated() to be false");
        }
        if (result.getCodeGenerator() != null) {
            throw new AssertionError("Expected getCodeGenerator() to be null when semantic analysis failed");
        }
        if (result.getFinalHtmlDocument() != null) {
            throw new AssertionError("Expected getFinalHtmlDocument() to be null when semantic analysis failed");
        }
        if (result.getGeneratedPythonSource() != null || result.getGeneratedJinjaSource() != null
                || result.getGeneratedHtmlSource() != null) {
            throw new AssertionError("Expected every generated-source passthrough to be null when not generated");
        }

        System.out.println("runCompileToHtmlSemanticErrorGateTest passed");
    }

    /**
     * The pre-existing {@link CompilerPipeline#compile} must be completely
     * unaffected by adding {@code compileToHtml} alongside it - same
     * behavior, same fields, and critically, still does NOT run code
     * generation on its own (that remains {@code compileToHtml}'s job).
     */
    private static void runExistingCompileMethodUnaffectedTest() {
        String pythonSource = String.join("\n",
                "from flask import Flask, render_template",
                "app = Flask(__name__)",
                "",
                "@app.route('/x')",
                "def x():",
                "    value = 1",
                "    return render_template('x.html', value=value)",
                ""
        );
        String templateSource = "<p>{{ value }}</p>\n";

        CompilerPipeline.Result plain = CompilerPipeline.compile(pythonSource, templateSource);
        if (plain.hasSemanticErrors()) {
            throw new AssertionError("Unexpected semantic errors: " + plain.getSemanticErrors());
        }
        if (!plain.isGenerated()) {
            throw new AssertionError("Expected the existing Generator to have run, exactly as before this stage");
        }
        if (plain.getGeneratedTemplate() == null) {
            throw new AssertionError("Expected getGeneratedTemplate() to be populated, exactly as before this stage");
        }
        // The key regression check: plain compile() has no notion of
        // PythonGenerator/JinjaGenerator/HtmlGenerator/CssGenerator/CodeGenerator
        // at all - it must keep doing exactly what it did before this stage,
        // nothing more.
        for (java.lang.reflect.Method m : CompilerPipeline.Result.class.getMethods()) {
            if (m.getName().equals("getFinalHtmlDocument") || m.getName().equals("getCodeGenerator")) {
                throw new AssertionError(
                        "CompilerPipeline.Result must not have gained any Requirement-5-specific method "
                                + "(found " + m.getName() + ") - that capability belongs on CompilationResult only, "
                                + "returned by the new compileToHtml method, so the existing Result stays exactly "
                                + "as it was");
            }
        }

        System.out.println("runExistingCompileMethodUnaffectedTest passed");
    }

    /** The Final HTML File must actually be written to disk, with byte-for-byte matching content. */
    private static void runWriteFinalHtmlToFileTest() throws Exception {
        String pythonSource = String.join("\n",
                "from flask import Flask, render_template",
                "app = Flask(__name__)",
                "",
                "@app.route('/')",
                "def home():",
                "    return render_template('home.html')",
                ""
        );
        String templateSource = "<p>Static content</p>\n";

        CompilationResult result = CompilerPipeline.compileToHtml(pythonSource, templateSource);
        if (!result.isFullyGenerated()) {
            throw new AssertionError("Expected successful generation");
        }

        Path tempFile = Files.createTempFile("full-pipeline-integration-test", ".html");
        Path written = result.writeFinalHtmlToFile(tempFile);
        if (!written.equals(tempFile)) {
            throw new AssertionError("Expected writeFinalHtmlToFile to return the path it was given");
        }
        if (!Files.exists(tempFile)) {
            throw new AssertionError("Expected the Final HTML File to actually exist on disk");
        }
        String onDisk = new String(Files.readAllBytes(tempFile), java.nio.charset.StandardCharsets.UTF_8);
        if (!onDisk.equals(result.getFinalHtmlDocument())) {
            throw new AssertionError("Expected file content to exactly match getFinalHtmlDocument()");
        }

        // Also exercise the String-path convenience overload, and parent
        // directory creation.
        Path nested = Files.createTempDirectory("full-pipeline-integration-test-dir")
                .resolve("nested").resolve("output.html");
        result.writeFinalHtmlToFile(nested.toString());
        if (!Files.exists(nested)) {
            throw new AssertionError("Expected writeFinalHtmlToFile(String) to create missing parent directories");
        }

        System.out.println("runWriteFinalHtmlToFileTest passed");
    }

    /** Writing the Final HTML File must be refused, not attempted with null content, when generation didn't run. */
    private static void runWriteFinalHtmlToFileRefusesWhenNotGeneratedTest() {
        String brokenPython = String.join("\n",
                "from flask import render_template",
                "",
                "def home():",
                "    return render_template('home.html', x=nope)",
                ""
        );
        CompilationResult result = CompilerPipeline.compileToHtml(brokenPython, "<p>{{ x }}</p>\n");
        if (result.isFullyGenerated()) {
            throw new AssertionError("Expected this deliberately-broken source to fail generation");
        }

        boolean threw = false;
        try {
            result.writeFinalHtmlToFile("/tmp/full-pipeline-integration-test-should-not-be-created.html");
        } catch (IllegalStateException expected) {
            threw = true;
        } catch (Exception unexpected) {
            throw new AssertionError("Expected IllegalStateException, got " + unexpected.getClass(), unexpected);
        }
        if (!threw) {
            throw new AssertionError("Expected writeFinalHtmlToFile to refuse when code generation did not run");
        }
        if (Files.exists(java.nio.file.Paths.get("/tmp/full-pipeline-integration-test-should-not-be-created.html"))) {
            throw new AssertionError("A file must not have been created when generation was refused");
        }

        System.out.println("runWriteFinalHtmlToFileRefusesWhenNotGeneratedTest passed");
    }

    /**
     * {@link CompilationResult}'s constructor enforces its own invariant
     * (code generation ran if and only if there were no semantic errors)
     * regardless of how it is constructed, not only through
     * {@code compileToHtml} - defense in depth for a class with a public
     * constructor.
     */
    private static void runCompilationResultInvariantsTest() {
        CompilerPipeline.Result cleanResult = CompilerPipeline.compile("x = 1\n", "<p>hi</p>\n");
        if (cleanResult.hasSemanticErrors()) {
            throw new AssertionError("Unexpected semantic errors in fixture source");
        }

        boolean threw = false;
        try {
            new CompilationResult(cleanResult, null);
        } catch (IllegalArgumentException expected) {
            threw = true;
        }
        if (!threw) {
            throw new AssertionError("Expected constructing CompilationResult with a null CodeGenerator "
                    + "but no semantic errors to be rejected");
        }

        CompilerPipeline.Result brokenResult = CompilerPipeline.compile(
                "x = totally_undefined_here\n", "<p>hi</p>\n");
        if (!brokenResult.hasSemanticErrors()) {
            throw new AssertionError("Expected fixture source to have semantic errors");
        }
        CodeGenerator dummyGenerator = new CodeGenerator(
                brokenResult.getPythonAst(), brokenResult.getTemplateAst(), true).generateFinalDocument();

        threw = false;
        try {
            new CompilationResult(brokenResult, dummyGenerator);
        } catch (IllegalArgumentException expected) {
            threw = true;
        }
        if (!threw) {
            throw new AssertionError("Expected constructing CompilationResult with a non-null CodeGenerator "
                    + "alongside semantic errors to be rejected");
        }

        threw = false;
        try {
            new CompilationResult(null, null);
        } catch (IllegalArgumentException expected) {
            threw = true;
        }
        if (!threw) {
            throw new AssertionError("Expected a null CompilerPipeline.Result to be rejected");
        }

        System.out.println("runCompilationResultInvariantsTest passed");
    }

    /**
     * A richer, more realistic template - multiple routes' worth of data,
     * control flow, a loop, and CSS all together - to confirm the full
     * integration holds up beyond the smallest possible example.
     */
    private static void runComplexRealisticTemplateIntegrationTest() {
        String pythonSource = String.join("\n",
                "from flask import Flask, render_template",
                "app = Flask(__name__)",
                "",
                "@app.route('/report')",
                "def report():",
                "    company = 'Acme Corp'",
                "    total_sales = 15000",
                "    is_profitable = True",
                "    return render_template('report.html',",
                "        company=company, total_sales=total_sales, is_profitable=is_profitable)",
                ""
        );
        String templateSource = String.join("\n",
                "<html>",
                "<head>",
                "    <title>Quarterly Report</title>",
                "    <style>",
                "        .report { border: 1px solid black; }",
                "        .positive { color: green; }",
                "        .negative { color: red; }",
                "    </style>",
                "</head>",
                "<body>",
                "<h1>{{ company }}</h1>",
                "<p>Total sales: {{ total_sales }}</p>",
                "{% if is_profitable %}",
                "<p class=\"positive\">Profitable quarter</p>",
                "{% else %}",
                "<p class=\"negative\">Loss this quarter</p>",
                "{% endif %}",
                "</body>",
                "</html>",
                ""
        );

        CompilationResult result = CompilerPipeline.compileToHtml(pythonSource, templateSource);
        if (result.hasSemanticErrors()) {
            throw new AssertionError("Unexpected semantic errors: " + result.getSemanticErrors());
        }
        if (!result.isFullyGenerated()) {
            throw new AssertionError("Expected successful full generation");
        }

        String html = result.getFinalHtmlDocument();
        if (!html.contains("Acme Corp") || !html.contains("15000")) {
            throw new AssertionError("Expected resolved company/total_sales values, got:\n" + html);
        }
        if (!html.contains("border: 1px solid black;") || !html.contains("color: green;")
                || !html.contains("color: red;")) {
            throw new AssertionError("Expected all three injected CSS rules, got:\n" + html);
        }
        if (result.getCodeGenerator().getFinalDocumentGenerator().countInjectedStylesheets() != 1) {
            throw new AssertionError("Expected exactly one <style> element to have been injected");
        }
        // is_profitable = True is statically known from the Python source, so
        // Generator collapses {% if is_profitable %}...{% else %}...{% endif %}
        // down to just the taken branch: the winning branch's text is present,
        // the losing branch's text is gone, and no literal {% if %} control
        // tag survives into what is supposed to be a final, rendered document.
        if (!html.contains("Profitable quarter")) {
            throw new AssertionError("Expected taken branch resolved into the output, got:\n" + html);
        }
        if (html.contains("Loss this quarter")) {
            throw new AssertionError("Expected the untaken branch to be dropped, got:\n" + html);
        }
        if (result.getGeneratedJinjaSource().contains("{% if")) {
            throw new AssertionError(
                    "Expected the conditional to be fully resolved (no literal {% if %} left), got:\n"
                            + result.getGeneratedJinjaSource());

        }

        System.out.println("runComplexRealisticTemplateIntegrationTest passed");
    }

    /** Compiling the same source twice through the fully-integrated entry point must produce equivalent output. */
    private static void runCompileToHtmlDeterminismTest() {
        String pythonSource = String.join("\n",
                "from flask import Flask, render_template",
                "app = Flask(__name__)",
                "",
                "@app.route('/')",
                "def home():",
                "    label = 'Stable'",
                "    return render_template('home.html', label=label)",
                ""
        );
        String templateSource = "<p>{{ label }}</p>\n<style>p { color: gray; }</style>\n";

        CompilationResult first = CompilerPipeline.compileToHtml(pythonSource, templateSource);
        CompilationResult second = CompilerPipeline.compileToHtml(pythonSource, templateSource);

        if (!first.isFullyGenerated() || !second.isFullyGenerated()) {
            throw new AssertionError("Expected both runs to generate successfully");
        }
        if (!first.getFinalHtmlDocument().equals(second.getFinalHtmlDocument())) {
            throw new AssertionError("Expected two independent compileToHtml calls on identical source "
                    + "to produce identical Final HTML Documents:\n---\n" + first.getFinalHtmlDocument()
                    + "\n---\n" + second.getFinalHtmlDocument());
        }

        System.out.println("runCompileToHtmlDeterminismTest passed");
    }

    /** Every convenience passthrough on CompilationResult must agree with going through getCodeGenerator() directly. */
    private static void runConvenencePassthroughsTest() {
        String pythonSource = String.join("\n",
                "from flask import Flask, render_template",
                "app = Flask(__name__)",
                "",
                "@app.route('/')",
                "def home():",
                "    return render_template('home.html')",
                ""
        );
        String templateSource = "<p>hi</p>\n";

        CompilationResult result = CompilerPipeline.compileToHtml(pythonSource, templateSource);
        if (!result.isFullyGenerated()) {
            throw new AssertionError("Expected successful generation");
        }

        if (!result.getGeneratedPythonSource().equals(result.getCodeGenerator().getGeneratedPythonSource())) {
            throw new AssertionError("getGeneratedPythonSource() passthrough mismatch");
        }
        if (!result.getGeneratedJinjaSource().equals(result.getCodeGenerator().getGeneratedJinjaSource())) {
            throw new AssertionError("getGeneratedJinjaSource() passthrough mismatch");
        }
        if (!result.getGeneratedHtmlSource().equals(result.getCodeGenerator().getGeneratedHtmlSource())) {
            throw new AssertionError("getGeneratedHtmlSource() passthrough mismatch");
        }
        if (!result.getFinalHtmlDocument().equals(result.getCodeGenerator().getFinalHtmlDocument())) {
            throw new AssertionError("getFinalHtmlDocument() passthrough mismatch");
        }
        if (result.getPythonAst() != result.getCompileResult().getPythonAst()) {
            throw new AssertionError("getPythonAst() passthrough mismatch");
        }
        if (result.getSemanticAnalyzer() != result.getCompileResult().getSemanticAnalyzer()) {
            throw new AssertionError("getSemanticAnalyzer() passthrough mismatch");
        }

        System.out.println("runConvenencePassthroughsTest passed");
    }
}
