package org.example;

import java.util.List;
import pipeline.CompilerPipeline;
import semantic.error.SemanticError;
import semantic.error.SemanticErrorType;
import template.ast.jinja.JinjaNode;
import template.ast.jinja.LiteralNode;

/**
 * Verifies the full pipeline integration (Lexer -&gt; Parser -&gt; Python AST
 * -&gt; Jinja2 AST -&gt; Semantic Analysis -&gt; Generator) end to end through
 * {@link CompilerPipeline}, using real source text (not hand-built ASTs, as
 * {@link SemanticAnalyzerTest} and {@link SemanticErrorChecksTest} use) so
 * the real Lexer and Parser are exercised too. Same plain-{@code main()}/
 * {@code AssertionError} style as the project's other tests.
 *
 * <p>Covers Integration Requirements #1 and #2 directly:
 * <ul>
 *   <li>{@link #testValidSourceRunsGeneratorAndProducesOutput()} — the
 *       Generator runs, and only, when there are no semantic errors.</li>
 *   <li>{@link #testUndefinedPythonVariableHaltsBeforeGeneration()} /
 *       {@link #testUndefinedJinjaVariableHaltsBeforeGeneration()} /
 *       {@link #testMultipleErrorsAcrossBothLanguagesAreAllCollectedAndGenerationIsSkipped()}
 *       — the Generator is never constructed when semantic analysis finds
 *       an error, in either language, and every error is collected rather
 *       than only the first.</li>
 *   <li>{@link #testSemanticErrorsPreserveLineNumbersAndNodeNames()} —
 *       each reported error carries the real line number the Parser
 *       tracked and the offending node's type.</li>
 * </ul>
 */
public class CompilerPipelineTest {

    public static void main(String[] args) {
        testValidSourceRunsGeneratorAndProducesOutput();
        testUndefinedPythonVariableHaltsBeforeGeneration();
        testUndefinedJinjaVariableHaltsBeforeGeneration();
        testMultipleErrorsAcrossBothLanguagesAreAllCollectedAndGenerationIsSkipped();
        testSemanticErrorsPreserveLineNumbersAndNodeNames();
        System.out.println("Compiler pipeline test passed");
    }

    /** Integration Requirement #1, valid path: no semantic errors -> the Generator runs and produces real output. */
    private static void testValidSourceRunsGeneratorAndProducesOutput() {
        String python = String.join("\n",
            "from flask import Flask, render_template",
            "app = Flask(__name__)",
            "def show_items():",
            "    items = ['Apple', 'Banana']",
            "    return render_template('t.html', items=items)",
            "");
        String template = String.join("\n",
            "{% for item in items %}",
            "{{ item }}",
            "{% endfor %}",
            "");

        CompilerPipeline.Result result = CompilerPipeline.compile(python, template);

        if (result.hasSemanticErrors()) {
            throw new AssertionError("Expected no semantic errors for valid source, got: " + result.getSemanticErrors());
        }
        if (!result.isGenerated() || result.getGenerator() == null || result.getGeneratedTemplate() == null) {
            throw new AssertionError("Expected the Generator to have run for valid source");
        }

        // Prove the Generator's actual output, not just that it "ran":
        // both list values should have flowed through into the template.
        int matchedLiterals = 0;
        for (JinjaNode node : result.getGeneratedTemplate().getJinjaElements()) {
            if (node instanceof LiteralNode literal
                    && ("Apple".equals(literal.getStringValue()) || "Banana".equals(literal.getStringValue()))) {
                matchedLiterals++;
            }
        }
        if (matchedLiterals != 2) {
            throw new AssertionError("Expected both 'Apple' and 'Banana' in the generated template, found " + matchedLiterals);
        }
    }

    /** Integration Requirements #1-#2: an undefined Python variable must halt the pipeline before the Generator runs. */
    private static void testUndefinedPythonVariableHaltsBeforeGeneration() {
        String python = String.join("\n",
            "def f():",
            "    return undefined_thing",
            "");
        String template = "<p>no jinja here</p>\n";

        CompilerPipeline.Result result = CompilerPipeline.compile(python, template);

        if (!result.hasSemanticErrors()) {
            throw new AssertionError("Expected a semantic error for an undefined Python variable");
        }
        assertGeneratorDidNotRun(result);
    }

    /** Integration Requirements #1-#2: an undefined Jinja2 variable must halt the pipeline before the Generator runs. */
    private static void testUndefinedJinjaVariableHaltsBeforeGeneration() {
        String python = "x = 1\n";
        String template = "{{ undefined_template_variable }}\n";

        CompilerPipeline.Result result = CompilerPipeline.compile(python, template);

        if (!result.hasSemanticErrors()) {
            throw new AssertionError("Expected a semantic error for an undefined template variable");
        }
        assertGeneratorDidNotRun(result);
    }

    /** "Do NOT stop after the first error", proven across the whole pipeline: Python + Jinja2 errors together. */
    private static void testMultipleErrorsAcrossBothLanguagesAreAllCollectedAndGenerationIsSkipped() {
        String python = String.join("\n",
            "def f():",
            "    pass",
            "",
            "def f():",
            "    pass",
            "");
        String template = String.join("\n",
            "{{ undefined_one }}",
            "{% if undefined_two %}x{% endif %}",
            "");

        CompilerPipeline.Result result = CompilerPipeline.compile(python, template);

        List<SemanticError> errors = result.getSemanticErrors();
        if (errors.size() < 3) {
            throw new AssertionError(
                "Expected at least 3 errors (1 duplicate Python function + 2 undefined Jinja2 variables), got "
                    + errors.size() + ": " + errors);
        }
        requireErrorOfType(errors, SemanticErrorType.DUPLICATE_DEFINITION);
        requireErrorOfType(errors, SemanticErrorType.UNDEFINED_JINJA_VARIABLE);
        assertGeneratorDidNotRun(result);
    }

    /** Integration Requirement #2: every reported error preserves its real line number and node type. */
    private static void testSemanticErrorsPreserveLineNumbersAndNodeNames() {
        String python = String.join("\n", "", "", "y = mystery", "");
        String template = "<p>ok</p>\n";

        CompilerPipeline.Result result = CompilerPipeline.compile(python, template);
        List<SemanticError> errors = result.getSemanticErrors();
        if (errors.size() != 1) {
            throw new AssertionError("Expected exactly 1 error, got " + errors.size() + ": " + errors);
        }

        SemanticError error = errors.get(0);
        if (error.getType() != SemanticErrorType.UNDEFINED_VARIABLE) {
            throw new AssertionError("Expected UNDEFINED_VARIABLE, got " + error.getType());
        }
        if (error.getLine() != 3) {
            throw new AssertionError("Expected the real parsed line number (3) to be preserved, got " + error.getLine());
        }
        if (!"IdentifierNode".equals(error.getNodeName())) {
            throw new AssertionError("Expected the node name 'IdentifierNode' to be preserved, got " + error.getNodeName());
        }
        if (error.getMessage() == null || error.getMessage().isEmpty()) {
            throw new AssertionError("Expected a descriptive message");
        }
        assertGeneratorDidNotRun(result);
    }

    private static void assertGeneratorDidNotRun(CompilerPipeline.Result result) {
        if (result.isGenerated() || result.getGenerator() != null || result.getGeneratedTemplate() != null) {
            throw new AssertionError("Expected the Generator to NOT run when semantic errors exist");
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
