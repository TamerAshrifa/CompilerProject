package generator;

import flask.ast.nodes.statements.ProgramNode;
import pipeline.CompilerPipeline;
import semantic.SemanticAnalyzer;
import semantic.error.SemanticError;
import template.ast.TemplateProgramNode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * The outcome of one {@link CompilerPipeline#compileToHtml} run: the
 * existing, completely unmodified {@link CompilerPipeline.Result} (Lexing,
 * Parsing, AST Construction, Semantic Analysis, and the existing
 * {@code Generator}'s AST transform) together with the {@link CodeGenerator}
 * that carries out this requirement's remaining stage — Jinja/HTML/CSS
 * generation and the merged Final HTML Document — or {@code null} for that
 * second part when semantic analysis found errors and, per this
 * requirement's own rule, code generation never ran at all.
 *
 * <p>This class adds no generation logic of its own: every field it exposes
 * either comes directly from the existing {@link CompilerPipeline.Result} or
 * from a {@link CodeGenerator} produced exactly as
 * {@code CodeGeneratorTest}/{@code FullPipelineIntegrationTest} already
 * verify it independently. It exists purely to give
 * {@link CompilerPipeline#compileToHtml} one coherent object to return,
 * covering every one of this project's five requirements in a single place:
 *
 * <ul>
 *   <li>Lexer &amp; Parser, Python/Jinja AST — {@link #getPythonAst()} / {@link #getTemplateAst()}</li>
 *   <li>OOP Node hierarchy — the AST objects above, built entirely by the existing, unmodified builders</li>
 *   <li>Semantic Analysis — {@link #getSemanticAnalyzer()} / {@link #hasSemanticErrors()}</li>
 *   <li>Code Generation — {@link #getCodeGenerator()} / {@link #getFinalHtmlDocument()}</li>
 * </ul>
 */
public final class CompilationResult {

    private final CompilerPipeline.Result compileResult;
    private final CodeGenerator codeGenerator;

    /**
     * @param compileResult the outcome of {@link CompilerPipeline#compile}, never {@code null}
     * @param codeGenerator the {@link CodeGenerator} that ran {@link CodeGenerator#generateFinalDocument()},
     *                       or {@code null} if {@code compileResult.hasSemanticErrors()} was {@code true}
     *                       and code generation correctly never started
     */
    public CompilationResult(CompilerPipeline.Result compileResult, CodeGenerator codeGenerator) {
        if (compileResult == null) {
            throw new IllegalArgumentException("compileResult must not be null");
        }
        if (codeGenerator == null && !compileResult.hasSemanticErrors()) {
            throw new IllegalArgumentException(
                    "codeGenerator must not be null when compileResult has no semantic errors "
                            + "(code generation must run whenever semantic analysis succeeded)");
        }
        if (codeGenerator != null && compileResult.hasSemanticErrors()) {
            throw new IllegalArgumentException(
                    "codeGenerator must be null when compileResult has semantic errors "
                            + "(code generation must not run when semantic analysis failed)");
        }
        this.compileResult = compileResult;
        this.codeGenerator = codeGenerator;
    }

    /** Phases 1–4's outcome: both ASTs, semantic analysis, and the existing {@code Generator}'s transformed template. */
    public CompilerPipeline.Result getCompileResult() {
        return compileResult;
    }

    public ProgramNode getPythonAst() {
        return compileResult.getPythonAst();
    }

    public TemplateProgramNode getTemplateAst() {
        return compileResult.getTemplateAst();
    }

    public SemanticAnalyzer getSemanticAnalyzer() {
        return compileResult.getSemanticAnalyzer();
    }

    public List<SemanticError> getSemanticErrors() {
        return compileResult.getSemanticErrors();
    }

    public boolean hasSemanticErrors() {
        return compileResult.hasSemanticErrors();
    }

    /**
     * Phase 5's outcome, or {@code null} if semantic analysis found errors and
     * code generation correctly never ran — always the exact opposite of
     * {@link #hasSemanticErrors()}, exactly mirroring how
     * {@link CompilerPipeline.Result#getGenerator()} already relates to that
     * same flag for the earlier AST-transform stage.
     */
    public CodeGenerator getCodeGenerator() {
        return codeGenerator;
    }

    /** Whether code generation ran — always the exact opposite of {@link #hasSemanticErrors()}. */
    public boolean isFullyGenerated() {
        return codeGenerator != null;
    }

    /**
     * The complete Final HTML Document — HTML structure, injected CSS, and
     * resolved Jinja output merged into one piece of text by
     * {@link FinalDocumentGenerator} — or {@code null} if semantic analysis
     * found errors and code generation never ran.
     */
    public String getFinalHtmlDocument() {
        return codeGenerator != null ? codeGenerator.getFinalHtmlDocument() : null;
    }

    /** Convenience passthrough to {@code getCodeGenerator().getGeneratedPythonSource()}, or {@code null} if not generated. */
    public String getGeneratedPythonSource() {
        return codeGenerator != null ? codeGenerator.getGeneratedPythonSource() : null;
    }

    /** Convenience passthrough to {@code getCodeGenerator().getGeneratedJinjaSource()}, or {@code null} if not generated. */
    public String getGeneratedJinjaSource() {
        return codeGenerator != null ? codeGenerator.getGeneratedJinjaSource() : null;
    }

    /** Convenience passthrough to {@code getCodeGenerator().getGeneratedHtmlSource()}, or {@code null} if not generated. */
    public String getGeneratedHtmlSource() {
        return codeGenerator != null ? codeGenerator.getGeneratedHtmlSource() : null;
    }

    /**
     * Writes {@link #getFinalHtmlDocument()} to {@code path} as UTF-8 text,
     * creating any missing parent directories first, satisfying this
     * requirement's "Final HTML File" pipeline stage literally rather than
     * only as an in-memory string.
     *
     * @return {@code path}, for chaining
     * @throws IllegalStateException if code generation did not run (semantic analysis found errors)
     * @throws IOException if the file could not be written
     */
    public Path writeFinalHtmlToFile(Path path) throws IOException {
        if (!isFullyGenerated()) {
            throw new IllegalStateException(
                    "Cannot write the Final HTML File: code generation did not run because semantic "
                            + "analysis found errors. See getSemanticErrors() for what was reported.");
        }
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        Files.write(path, getFinalHtmlDocument().getBytes(StandardCharsets.UTF_8));
        return path;
    }

    /** Convenience overload of {@link #writeFinalHtmlToFile(Path)} taking a plain path string. */
    public Path writeFinalHtmlToFile(String path) throws IOException {
        return writeFinalHtmlToFile(Paths.get(path));
    }

    /** A short, human-readable report covering every phase, from lexing through the Final HTML Document. */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("CompilationResult Summary (Requirement 5, full pipeline)\n");
        sb.append("==========================================================\n");
        sb.append("Python AST: ").append(compileResult.getPythonAst() != null ? "built" : "(none)").append('\n');
        sb.append("Template AST: ").append(compileResult.getTemplateAst() != null ? "built" : "(none)").append('\n');
        sb.append("Semantic errors: ").append(compileResult.getSemanticErrors().size()).append('\n');
        sb.append("Code generation ran: ").append(isFullyGenerated()).append('\n');
        if (isFullyGenerated()) {
            sb.append('\n').append(codeGenerator.getSummary());
        } else {
            sb.append("Final HTML Document: (not produced - semantic errors must be fixed first)\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getSummary();
    }
}
