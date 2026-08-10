package webapp;

import generator.FinalDocumentGenerator;
import pipeline.CompilerPipeline;
import semantic.error.SemanticError;
import template.ast.TemplateProgramNode;

import java.util.List;

/**
 * Requirement 6 (Product List / Add Product): compiles one page's Python
 * source + template source into a Final HTML Document, reusing the existing
 * pipeline end to end rather than introducing any new rendering logic.
 *
 * <h2>Why this class exists instead of calling {@link CompilerPipeline#compileToHtml}</h2>
 * {@code compileToHtml} produces its Final HTML Document by running
 * {@code CodeGenerator}, which resolves {@code {{ variable }}} references
 * through {@code TemplateTransformer}. {@code TemplateTransformer}'s own
 * Javadoc describes exactly what it does: substitute a resolvable {@code
 * JinjaVariableNode} with a {@code LiteralNode}. Its {@code transformJinjaFor}
 * transforms a {@code {% for %}} loop's body once and hands back a single
 * (still-a-loop) node — it does not iterate the bound collection, so a page
 * whose data is a list (this requirement's "display all products") does not
 * come out resolved through that path.
 *
 * <p>{@link CompilerPipeline#compile} already runs the original {@code
 * Generator} first (see {@code Result#getGeneratedTemplate()}), and that
 * class's {@code transformFor} does unroll a {@code {% for %}} into one copy
 * of its body per element once the iterable resolves to a known list -
 * exactly what a product listing needs, and already exercised by
 * {@code Main}'s "Generator: passing the Python array into the Jinja2 tree"
 * demo. What {@code compile()} alone does not do is recombine the resulting
 * HTML tree and Jinja tree back into one document - that recombination is
 * {@link FinalDocumentGenerator}, used exactly as {@code CodeGenerator}
 * already uses it internally.
 *
 * <p>This class therefore composes two pieces that already exist and are
 * each already independently exercised elsewhere - {@code
 * CompilerPipeline.compile}'s {@code Generator} stage, then {@code
 * FinalDocumentGenerator} - rather than adding a third way to turn a
 * template into text. No class in {@code generator}, {@code pipeline},
 * {@code semantic}, or {@code template} is modified to make this work.
 */
public final class PageCompiler {

    private PageCompiler() {
        // Static utility class - never instantiated.
    }

    /**
     * Runs Lexing, Parsing, Semantic Analysis, and the existing {@link
     * generator.Generator} (via {@link CompilerPipeline#compile}), then -
     * only when semantic analysis found no errors - merges the resulting,
     * already-resolved template AST into one Final HTML Document via {@link
     * FinalDocumentGenerator}, precisely mirroring how {@link
     * CompilerPipeline#compileToHtml} gates its own final step on the same
     * condition.
     */
    public static RenderedPage compile(String pythonSource, String templateSource) {
        CompilerPipeline.Result result = CompilerPipeline.compile(pythonSource, templateSource);

        if (result.hasSemanticErrors()) {
            return new RenderedPage(result, null);
        }

        TemplateProgramNode resolvedTemplate = result.getGeneratedTemplate();
        String finalHtml = new FinalDocumentGenerator().generate(resolvedTemplate);
        return new RenderedPage(result, finalHtml);
    }

    /**
     * The outcome of one {@link #compile} run: the underlying pipeline
     * {@link CompilerPipeline.Result} (semantic analysis, the Generator that
     * ran, and its resolved template) plus the merged Final HTML Document
     * text, or {@code null} for the latter exactly when semantic analysis
     * reported errors.
     */
    public static final class RenderedPage {

        private final CompilerPipeline.Result compilationResult;
        private final String finalHtml;

        private RenderedPage(CompilerPipeline.Result compilationResult, String finalHtml) {
            this.compilationResult = compilationResult;
            this.finalHtml = finalHtml;
        }

        /** The underlying pipeline result: both ASTs, semantic analysis, the Generator, and its output. */
        public CompilerPipeline.Result getCompilationResult() {
            return compilationResult;
        }

        public boolean hasErrors() {
            return compilationResult.hasSemanticErrors();
        }

        /** Every semantic error found, in discovery order. Empty exactly when {@link #hasErrors()} is {@code false}. */
        public List<SemanticError> getErrors() {
            return compilationResult.getSemanticErrors();
        }

        /** The merged Final HTML Document, or {@code null} if semantic analysis found errors. */
        public String getFinalHtml() {
            return finalHtml;
        }
    }
}
