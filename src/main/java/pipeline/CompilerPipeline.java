package pipeline;

import flask.ast.builder.FlaskASTBuilder;
import flask.ast.nodes.statements.ProgramNode;
import generator.CodeGenerator;
import generator.CompilationResult;
import generator.Generator;
import grammar.flask.FlaskLexer;
import grammar.flask.FlaskParser;
import grammar.template.TemplateLexer;
import grammar.template.TemplateParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import semantic.SemanticAnalyzer;
import semantic.error.SemanticError;
import template.TemplateASTBuilder;
import template.ast.TemplateProgramNode;
import java.util.List;

/**
 * Orchestrates the full compiler pipeline:
 *
 * <pre>
 *   Lexer
 *     |
 *   Parser
 *     |
 *   Python AST
 *     |
 *   Jinja2 AST
 *     |
 *   Semantic Analysis
 *     |
 *   Generator
 * </pre>
 *
 * <p>This class is the single place that decision lives: {@link #compile}
 * builds both ASTs (Lexer -&gt; Parser -&gt; the existing {@code
 * FlaskASTBuilder}/{@code TemplateASTBuilder}, entirely unchanged), runs
 * {@link SemanticAnalyzer} over both of them, and — <b>only if semantic
 * analysis found no errors</b> — constructs and runs the existing {@link
 * Generator}. A caller cannot accidentally reach the Generator around this
 * check: if semantic analysis reported anything, {@link Result#getGenerator()}
 * and {@link Result#getGeneratedTemplate()} are simply {@code null} and
 * {@link Result#isGenerated()} is {@code false} — the Generator is never
 * constructed at all in that case, not merely left unused.
 *
 * <p>{@code compile} performs no printing itself — it is a small, pure
 * orchestration step returning a {@link Result} that callers (a runnable
 * demo like {@code Main}, or a test) inspect however they need to. This is
 * what makes "the Generator ran" / "these exact errors were reported, with
 * their line numbers and node names" independently testable without
 * capturing console output — see {@code CompilerPipelineTest}.
 *
 * <p>{@code Main} previously built each AST inline via its own private
 * {@code buildPythonAst}/{@code buildTemplateAst} helpers; that lexer/parser
 * plumbing now lives here instead (as {@link #buildPythonAst} and {@link
 * #buildTemplateAst}, unchanged in behavior, just relocated so both {@code
 * Main} and any test can reuse the exact same pipeline rather than each
 * keeping its own copy) with {@code Main} delegating to {@link #compile}.
 *
 * <p><b>Requirement 5, complete:</b> {@link #compileToHtml} extends this
 * same pipeline one stage further, without changing anything above — it
 * calls {@link #compile} exactly as written, and, only when that reports no
 * semantic errors, additionally runs {@link CodeGenerator} (Jinja AST
 * generation, HTML generation, and CSS generation/injection) to produce a
 * complete Final HTML Document. Every rule {@link #compile} already
 * enforces for the Generator — analysis must finish first, and code
 * generation is skipped entirely rather than run against a broken AST when
 * errors are found — applies identically to this final stage; see
 * {@link CompilationResult} for the combined result it returns.
 */
public final class CompilerPipeline {

    private CompilerPipeline() {
        // Static utility class - never instantiated.
    }

    /**
     * Runs the complete pipeline over Python/Flask source and Jinja2/HTML
     * template source.
     *
     * <p>Integration Requirement #1: the Generator executes only if {@code
     * result.hasSemanticErrors()} is {@code false} afterward. Integration
     * Requirement #2 (print all errors, preserving line numbers and node
     * names, and stop before code generation) is the caller's
     * responsibility using {@link Result#getSemanticErrors()} — each
     * {@link SemanticError} already carries its line, column, and node
     * name (see that class), and stopping "before code generation" is
     * automatic here: this method simply never reaches the {@link
     * Generator} construction line when errors were found.
     */
    public static Result compile(String pythonSource, String templateSource) {
        ProgramNode pythonAst = buildPythonAst(pythonSource);
        TemplateProgramNode templateAst = buildTemplateAst(templateSource);

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
        semanticAnalyzer.analyze(pythonAst).analyze(templateAst);

        if (semanticAnalyzer.hasErrors()) {
            return new Result(pythonAst, templateAst, semanticAnalyzer, null, null);
        }

        Generator generator = new Generator(pythonAst, templateAst, semanticAnalyzer.getSymbolTable());
        TemplateProgramNode generatedTemplate = generator.generate();
        return new Result(pythonAst, templateAst, semanticAnalyzer, generator, generatedTemplate);
    }

    /**
     * Runs the complete pipeline through to a Final HTML Document: {@link
     * #compile} exactly as above (Lexing, Parsing, AST Construction,
     * Semantic Analysis, and the existing {@link Generator}'s AST
     * transform), and then — only when that reports no semantic errors —
     * {@link CodeGenerator#generateFinalDocument()} (Jinja generation, HTML
     * generation, and CSS generation/injection).
     *
     * <p>This is "one compilation pipeline" connecting every phase in a
     * single call: a caller does not need to separately construct a
     * {@link CodeGenerator} or check {@link Result#hasSemanticErrors()}
     * themselves before doing so — this method already does both, the same
     * way {@link #compile} already does for the Generator. Code generation
     * is skipped automatically, not merely left unused, whenever semantic
     * analysis found errors: {@link CompilationResult#getCodeGenerator()}
     * and {@link CompilationResult#getFinalHtmlDocument()} are simply
     * {@code null} in that case, and {@link CompilationResult#isFullyGenerated()}
     * is {@code false}.
     *
     * @param pythonSource   Python/Flask source implementing one or more routes
     * @param templateSource Jinja2/HTML template source
     * @return the combined outcome of every phase; see {@link CompilationResult}
     */
    public static CompilationResult compileToHtml(String pythonSource, String templateSource) {
        Result result = compile(pythonSource, templateSource);

        if (result.hasSemanticErrors()) {
            return new CompilationResult(result, null);
        }

        CodeGenerator codeGenerator = new CodeGenerator(result.getPythonAst(), result.getTemplateAst(), true)
                .generateFinalDocument();
        return new CompilationResult(result, codeGenerator);
    }

    /** Lexer -&gt; Parser -&gt; {@code FlaskASTBuilder} for Python/Flask source. */
    public static ProgramNode buildPythonAst(String source) {
        CharStream input = CharStreams.fromString(source);
        FlaskLexer lexer = new FlaskLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        FlaskParser parser = new FlaskParser(tokens);
        FlaskParser.ProgramContext tree = parser.program();

        FlaskASTBuilder builder = new FlaskASTBuilder();
        return (ProgramNode) builder.build(tree);
    }

    /** Lexer -&gt; Parser -&gt; {@code TemplateASTBuilder} for Jinja2/HTML template source. */
    public static TemplateProgramNode buildTemplateAst(String source) {
        CharStream input = CharStreams.fromString(source);
        TemplateLexer lexer = new TemplateLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TemplateParser parser = new TemplateParser(tokens);
        TemplateParser.HtmlDocumentContext tree = parser.htmlDocument();

        TemplateASTBuilder builder = new TemplateASTBuilder();
        return builder.build(tree);
    }

    /**
     * The outcome of one {@link #compile} run: both ASTs, the completed
     * semantic analysis, and — only when semantic analysis found no
     * errors — the {@link Generator} that ran and the template it produced.
     */
    public static final class Result {

        private final ProgramNode pythonAst;
        private final TemplateProgramNode templateAst;
        private final SemanticAnalyzer semanticAnalyzer;
        private final Generator generator;
        private final TemplateProgramNode generatedTemplate;

        private Result(ProgramNode pythonAst, TemplateProgramNode templateAst, SemanticAnalyzer semanticAnalyzer,
                        Generator generator, TemplateProgramNode generatedTemplate) {
            this.pythonAst = pythonAst;
            this.templateAst = templateAst;
            this.semanticAnalyzer = semanticAnalyzer;
            this.generator = generator;
            this.generatedTemplate = generatedTemplate;
        }

        public ProgramNode getPythonAst() {
            return pythonAst;
        }

        public TemplateProgramNode getTemplateAst() {
            return templateAst;
        }

        /** The completed semantic analysis — scopes, symbols, and every error found. */
        public SemanticAnalyzer getSemanticAnalyzer() {
            return semanticAnalyzer;
        }

        /** Every semantic error found, in discovery order. Empty exactly when {@link #isGenerated()} is {@code true}. */
        public List<SemanticError> getSemanticErrors() {
            return semanticAnalyzer.getErrors();
        }

        public boolean hasSemanticErrors() {
            return semanticAnalyzer.hasErrors();
        }

        /** The Generator that ran, or {@code null} if semantic analysis found errors and it never ran. */
        public Generator getGenerator() {
            return generator;
        }

        /** The Generator's output, or {@code null} if semantic analysis found errors and it never ran. */
        public TemplateProgramNode getGeneratedTemplate() {
            return generatedTemplate;
        }

        /** Whether the Generator ran — always the exact opposite of {@link #hasSemanticErrors()}. */
        public boolean isGenerated() {
            return generatedTemplate != null;
        }
    }
}
