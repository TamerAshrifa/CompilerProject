package pipeline;

import flask.ast.builder.FlaskASTBuilder;
import flask.ast.nodes.statements.ProgramNode;
import generator.CodeGenerator;
import generator.CompilationResult;
import generator.DataFlowPrinter;
import generator.Generator;
import grammar.flask.FlaskLexer;
import grammar.flask.FlaskParser;
import grammar.template.TemplateLexer;
import grammar.template.TemplateParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import semantic.DebugOutput;
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
 *
 * <p><b>Debug mode.</b> Both {@link #compile} and {@link #compileToHtml}
 * above are, and remain, print-free - that design does not change. Each
 * also has a {@code (..., boolean debugMode)} overload that runs the exact
 * same pipeline (it delegates to the print-free method above it for every
 * bit of actual work) and, only when {@code debugMode} is {@code true},
 * additionally prints a full debug report to {@code System.out} - the
 * Python AST, the Jinja2 AST, the Symbol Table, any Semantic Errors, the
 * Python-to-Jinja2 Data Flow, and, for {@link #compileToHtml}, the
 * generated output - before returning the identical result its
 * {@code debugMode=false} form would have. See {@code semantic.DebugOutput}
 * (the AST/Symbol Table/Errors sections) and {@code generator.DataFlowPrinter}
 * (the Data Flow section), which this class simply calls in sequence with a
 * banner around each - no new printing logic of its own, and no change to
 * how compilation itself works either way. See {@code pipeline.FullPipelineDemo}
 * for a runnable example.
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
     * Same as {@link #compile(String, String)} - it delegates to that exact
     * method for the entire pipeline, so a given source pair produces the
     * identical {@link Result} either way - except that when {@code
     * debugMode} is {@code true}, it also prints, in order: the Python AST,
     * the Jinja2 AST, the Symbol Table, any Semantic Errors, and the
     * Python-to-Jinja2 Data Flow. These print even when semantic analysis
     * found errors (that is often the most useful time to see them); only
     * {@link #compileToHtml(String, String, boolean)}'s further "generated
     * output" section is conditional on there being no errors, since code
     * generation itself does not run otherwise.
     *
     * <p>When {@code debugMode} is {@code false}, this does exactly what
     * calling {@link #compile(String, String)} directly does - the
     * conditional print block below is simply skipped, with no other
     * difference in behavior or return value.
     */
    public static Result compile(String pythonSource, String templateSource, boolean debugMode) {
        Result result = compile(pythonSource, templateSource);
        if (debugMode) {
            printDebugReport(result);
        }
        return result;
    }

    /** The AST/Symbol Table/Errors/Data Flow portion of debug mode, shared by both {@code compile} and {@code compileToHtml}. */
    private static void printDebugReport(Result result) {
        DebugOutput.printFullDebugOutput(
                result.getPythonAst(),
                result.getTemplateAst(),
                result.getSemanticAnalyzer().getSymbolTable(),
                result.getSemanticErrors());
        System.out.println();
        printBanner("DATA FLOW");
        DataFlowPrinter.printDataFlow(result.getPythonAst());
        System.out.println();
    }

    private static void printBanner(String title) {
        String rule = "=".repeat(60);
        System.out.println(rule);
        System.out.println(" " + title);
        System.out.println(rule);
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

    /**
     * Same as {@link #compileToHtml(String, String)} - producing the
     * identical {@link CompilationResult} either way - except that when
     * {@code debugMode} is {@code true}, it also prints the same debug
     * report {@link #compile(String, String, boolean)} does (Python AST,
     * Jinja2 AST, Symbol Table, Semantic Errors, Data Flow - all printed
     * <em>before</em> code generation runs, per this method's own "before
     * code generation" rule), followed by one more section once code
     * generation actually runs: the resulting Final HTML Document. When
     * semantic analysis found errors, code generation is skipped exactly as
     * {@link #compileToHtml(String, String)} already does, and debug mode
     * prints a short note explaining why that final section has nothing to
     * show instead of silently omitting it.
     *
     * <p>When {@code debugMode} is {@code false}, this does exactly what
     * calling {@link #compileToHtml(String, String)} directly does.
     */
    public static CompilationResult compileToHtml(String pythonSource, String templateSource, boolean debugMode) {
        Result result = compile(pythonSource, templateSource, debugMode);

        if (result.hasSemanticErrors()) {
            CompilationResult failed = new CompilationResult(result, null);
            if (debugMode) {
                printBanner("GENERATED OUTPUT");
                System.out.println("(skipped - semantic analysis found errors; see SEMANTIC ERRORS above)");
            }
            return failed;
        }

        CodeGenerator codeGenerator = new CodeGenerator(result.getPythonAst(), result.getTemplateAst(), true)
                .generateFinalDocument();
        CompilationResult generated = new CompilationResult(result, codeGenerator);
        if (debugMode) {
            printBanner("GENERATED OUTPUT");
            System.out.println(generated.getFinalHtmlDocument());
        }
        return generated;
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
