package generator;

import flask.ast.nodes.statements.ProgramNode;
import template.ast.TemplateProgramNode;
import template.ast.jinja.JinjaNode;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Orchestrates Requirement 5 code generation: given a Python AST and/or a
 * Template AST, drives {@link PythonGenerator}, {@link JinjaGenerator} and
 * {@link HtmlGenerator} (which in turn drives {@link CssGenerator} for any
 * embedded {@code <style>} content) to produce source text, and mechanically
 * enforces that this only happens once semantic analysis has succeeded.
 *
 * <p>This class deliberately holds the three (four, counting
 * {@code CssGenerator}) concrete generators <em>through composition</em>
 * rather than being a common superclass they extend. {@link PythonGenerator}
 * implements {@link flask.ast.visitor.ASTVisitor} directly, while
 * {@link JinjaGenerator}/{@link HtmlGenerator}/{@link CssGenerator} each
 * extend {@link template.visitor.TemplateBaseVisitor} to properly reuse the
 * project's existing default Template-visitor behavior — two different
 * concrete superclasses that Java's single inheritance rules out combining
 * under one shared base. Composition here gives every concrete generator the
 * most appropriate existing base to build on (rule: "reuse the current
 * Visitor pattern if one already exists") while this class still offers one
 * place to drive and query them together.
 *
 * <h2>Independence from semantic analysis</h2>
 * This class — and everything else in this package — has no compile-time
 * dependency on the {@code semantic} package; it never imports
 * {@code SemanticAnalyzer}, {@code SymbolTable}, or anything else from it.
 * The rule "generator starts only if semantic analysis succeeds" is instead
 * enforced mechanically through the {@code semanticAnalysisSucceeded}
 * constructor flag: {@link #generate()} and
 * {@link #generateWithResolvedContext()} both throw
 * {@link IllegalStateException} immediately if it is {@code false}, rather
 * than silently generating from a possibly-broken AST. The caller — a future
 * pipeline stage, not this one (this task explicitly stops short of wiring
 * this into {@code CompilerPipeline}) — is expected to construct this class
 * only after confirming {@code !semanticAnalyzer.hasErrors()}, exactly
 * mirroring how {@code CompilerPipeline} already gates the existing
 * {@link Generator} on the same condition.
 *
 * <h2>Two ways to generate</h2>
 * <ul>
 *   <li>{@link #generate()} — straightforward serialization. Each AST is
 *       printed as-is; nothing is evaluated or substituted. Use this when the
 *       Template AST passed in has already been through whatever
 *       transformation (if any) the caller wants, or when no such
 *       transformation is needed.</li>
 *   <li>{@link #generateWithResolvedContext()} — additionally realizes the
 *       "Python AST &rarr; Intermediate Generation Data &rarr; Jinja AST
 *       &rarr; HTML" pipeline this requirement describes, end to end, by
 *       reusing the existing, already-correct {@link Generator} (Python
 *       AST &rarr; {@link Context}, the intermediate generation data,
 *       &rarr; a new, fully resolved Template AST: resolvable
 *       {@code {{ variable }}} references replaced by concrete values,
 *       {@code {% for %}} loops unrolled once per element of a resolved
 *       list, and {@code {% if %}} collapsed to its taken branch when the
 *       condition is statically known) before handing the result to
 *       {@link JinjaGenerator} / {@link HtmlGenerator}. This is the same
 *       {@link Generator} {@link webapp.PageCompiler} already drives for
 *       Requirement 6's product pages, so both pathways now share one
 *       real resolution mechanism instead of two differently-capable
 *       ones.</li>
 *   <li>{@link #generateFinalDocument()} — everything
 *       {@link #generateWithResolvedContext()} does, plus one more step: the
 *       resolved Template AST is additionally run through
 *       {@link FinalDocumentGenerator}, which merges the (still independent)
 *       HTML and Jinja trees back into one document in source-position
 *       order. This is what lets a resolved value such as {@code {{ title }}}
 *       &rarr; {@code "Welcome"} actually show up in one complete piece of
 *       output alongside the surrounding HTML, addressing the full
 *       Python&nbsp;Source&nbsp;&rarr;&hellip;&rarr;&nbsp;Final&nbsp;HTML&nbsp;Document
 *       pipeline end to end. See {@link FinalDocumentGenerator}'s Javadoc for
 *       exactly what "merge" means and its honestly-documented limits.</li>
 * </ul>
 */
public class CodeGenerator {

    private final ProgramNode pythonAst;
    private final TemplateProgramNode templateAst;
    private final boolean semanticAnalysisSucceeded;

    private final PythonGenerator pythonGenerator = new PythonGenerator();
    private final JinjaGenerator jinjaGenerator = new JinjaGenerator();
    private final HtmlGenerator htmlGenerator = new HtmlGenerator();
    // Deliberately its own HtmlGenerator/JinjaGenerator pair (the no-arg
    // constructor), rather than sharing the two fields above: generating the
    // final merged document visits each top-level node individually (see
    // FinalDocumentGenerator.generate), which would otherwise run a second,
    // un-reset traversal through the same generators used for
    // getGeneratedHtmlSource()/getGeneratedJinjaSource() above, silently
    // double-counting entries in their source maps (and in
    // countInjectedStylesheets()). Separate instances keep each set of
    // source maps an unambiguous record of exactly one generation pass.
    private final FinalDocumentGenerator finalDocumentGenerator = new FinalDocumentGenerator();

    private String generatedPythonSource;
    private String generatedJinjaSource;
    private String generatedHtmlSource;
    private String finalHtmlDocument;
    private Context intermediateContext;
    private TemplateProgramNode resolvedTemplateAst;
    private Map<JinjaNode, List<JinjaNode>> resolvedReplacements = Collections.emptyMap();
    private Set<JinjaNode> hostedReplacementNodes = Collections.emptySet();
    private boolean generated = false;

    /**
     * @param pythonAst                  the Python AST root to generate from, or {@code null} to skip Python generation
     * @param templateAst                the Template AST root to generate from, or {@code null} to skip Jinja/HTML generation
     * @param semanticAnalysisSucceeded  whether semantic analysis has already run, on both ASTs, with zero errors;
     *                                   see the class Javadoc for why this is a plain {@code boolean} rather than a
     *                                   {@code SymbolTable} reference
     */
    public CodeGenerator(ProgramNode pythonAst, TemplateProgramNode templateAst, boolean semanticAnalysisSucceeded) {
        this.pythonAst = pythonAst;
        this.templateAst = templateAst;
        this.semanticAnalysisSucceeded = semanticAnalysisSucceeded;
    }

    /**
     * Generates Python source from {@code pythonAst} (if present) and Jinja2/HTML
     * source directly from {@code templateAst} (if present), with no
     * cross-language variable resolution. See the class Javadoc for how this
     * differs from {@link #generateWithResolvedContext()}.
     *
     * @return this instance, for chaining (e.g. {@code new CodeGenerator(...).generate().getGeneratedHtmlSource()})
     * @throws IllegalStateException if constructed with {@code semanticAnalysisSucceeded=false}
     */
    public CodeGenerator generate() {
        requireSemanticSuccess();
        if (pythonAst != null) {
            generatedPythonSource = pythonGenerator.generate(pythonAst);
        }
        if (templateAst != null) {
            generatedJinjaSource = jinjaGenerator.generate(templateAst);
            // Explicitly reset rather than assume: this method intentionally
            // does no cross-language resolution (see its own Javadoc above),
            // so a JinjaHostNode must fall back to raw Jinja2 source here -
            // even if generateWithResolvedContext() already ran on this same
            // instance and left htmlGenerator holding a stale map.
            generatedHtmlSource = htmlGenerator.withResolvedReplacements(null).generate(templateAst);
        }
        generated = true;
        return this;
    }

    /**
     * Like {@link #generate()}, but first derives {@link Context} ("Intermediate
     * Generation Data") from {@code pythonAst} and (when both ASTs are
     * present) fully resolves the Template AST against it — including
     * {@code {% for %}} unrolling and {@code {% if %}} collapsing — via the
     * existing {@link Generator}, so resolvable {@code {{ variable }}}
     * references print their concrete value instead of the variable
     * reference itself.
     *
     * @return this instance, for chaining
     * @throws IllegalStateException if constructed with {@code semanticAnalysisSucceeded=false}
     */
    public CodeGenerator generateWithResolvedContext() {
        requireSemanticSuccess();

        TemplateProgramNode effectiveTemplateAst = templateAst;

        if (pythonAst != null) {
            generatedPythonSource = pythonGenerator.generate(pythonAst);

            // Reuses the same Generator engine that CompilerPipeline.compile()
            // and PageCompiler already drive for Requirements 2 and 6, instead
            // of duplicating a second, weaker resolution mechanism here.
            // TemplateTransformer (the previous mechanism) only ever replaces
            // one resolvable {{ variable }} at a time and hands a {% for %}
            // node back unchanged - "transforms the loop body once", not once
            // per element - so a template driving its content from a Python
            // list (Requirement 2's central example) never actually rendered
            // through this path; only a hand-assembled alternative
            // (PageCompiler) did. Generator additionally unrolls {% for %}
            // over a resolved list and collapses {% if %} to its taken branch
            // when the condition is statically known, so this method and
            // PageCompiler now share one real implementation instead of two
            // differently-capable ones.
            Generator generator = new Generator(pythonAst, templateAst, null);
            effectiveTemplateAst = generator.generate();
            intermediateContext = generator.getContext();
            resolvedReplacements = generator.getResolvedReplacements();
            hostedReplacementNodes = generator.getHostedReplacementNodes();
        }

        resolvedTemplateAst = effectiveTemplateAst;

        if (effectiveTemplateAst != null) {
            generatedJinjaSource = jinjaGenerator.generate(effectiveTemplateAst);
            generatedHtmlSource = htmlGenerator.withResolvedReplacements(resolvedReplacements).generate(effectiveTemplateAst);
        }

        generated = true;
        return this;
    }

    /**
     * Runs the complete pipeline this task describes, end to end: everything
     * {@link #generateWithResolvedContext()} does, and then additionally
     * feeds the same resolved Template AST through {@link FinalDocumentGenerator}
     * to produce {@link #getFinalHtmlDocument()} — the merged HTML+Jinja
     * output, with CSS already injected wherever a {@code <style>} tag
     * appears, in one document. No logic is duplicated to do this: every
     * piece of generated text still comes from {@link JinjaGenerator} /
     * {@link HtmlGenerator} (via {@code finalDocumentGenerator}, see its
     * class Javadoc for why it uses its own dedicated pair of them rather
     * than {@link #getHtmlGenerator()}/{@link #getJinjaGenerator()}); this
     * method only adds the extract-transform steps (delegated to
     * {@link #generateWithResolvedContext()}) and the merge step on top.
     *
     * @return this instance, for chaining
     * @throws IllegalStateException if constructed with {@code semanticAnalysisSucceeded=false}
     */
    public CodeGenerator generateFinalDocument() {
        generateWithResolvedContext();
        if (resolvedTemplateAst != null) {
            // finalDocumentGenerator deliberately owns its own HtmlGenerator
            // instance rather than reusing the htmlGenerator field above
            // (see this class's own field-declaration comment), so the
            // resolution map has to be forwarded to that instance too, or a
            // JinjaHostNode inside the merged document would fall back to
            // raw Jinja2 source even though generateWithResolvedContext()
            // just resolved it correctly a moment ago.
            finalDocumentGenerator.getHtmlGenerator().withResolvedReplacements(resolvedReplacements);
            finalDocumentGenerator.withHostedReplacementNodes(hostedReplacementNodes);
            finalHtmlDocument = finalDocumentGenerator.generate(resolvedTemplateAst);
        }
        return this;
    }

    private void requireSemanticSuccess() {
        if (!semanticAnalysisSucceeded) {
            throw new IllegalStateException(
                    "CodeGenerator.generate() was called without successful semantic analysis. "
                            + "Code generation must not run until the semantic analyzer reports zero "
                            + "errors on both the Python and Template ASTs; construct this CodeGenerator "
                            + "with semanticAnalysisSucceeded=true only once that has been confirmed.");
        }
    }

    /** Whether {@link #generate()} or {@link #generateWithResolvedContext()} has completed. */
    public boolean isGenerated() {
        return generated;
    }

    /** Regenerated Python source, or {@code null} if no Python AST was supplied or generation has not run yet. */
    public String getGeneratedPythonSource() {
        return generatedPythonSource;
    }

    /** Regenerated Jinja2 template source, or {@code null} if no Template AST was supplied or generation has not run yet. */
    public String getGeneratedJinjaSource() {
        return generatedJinjaSource;
    }

    /** Regenerated HTML source, or {@code null} if no Template AST was supplied or generation has not run yet. */
    public String getGeneratedHtmlSource() {
        return generatedHtmlSource;
    }

    /**
     * The merged HTML+Jinja "Final HTML Document" produced by the last call to
     * {@link #generateFinalDocument()}, or {@code null} if that method has not
     * been called (or no Template AST was supplied). See
     * {@link FinalDocumentGenerator} for exactly how the merge works.
     */
    public String getFinalHtmlDocument() {
        return finalHtmlDocument;
    }

    /**
     * The Template AST actually used for Jinja/HTML/final-document generation
     * on the last {@link #generateWithResolvedContext()} or
     * {@link #generateFinalDocument()} call: the {@link Generator}-resolved
     * output when a Python AST was supplied, otherwise the original Template
     * AST unchanged. {@code null} until one of those methods has run, or if
     * no Template AST was supplied at all.
     */
    public TemplateProgramNode getResolvedTemplateAst() {
        return resolvedTemplateAst;
    }

    /**
     * The "Intermediate Generation Data" derived from the Python AST by the
     * last call to {@link #generateWithResolvedContext()}, or {@code null} if
     * that method has not been called (or no Python AST was supplied).
     */
    public Context getIntermediateContext() {
        return intermediateContext;
    }

    public PythonGenerator getPythonGenerator() {
        return pythonGenerator;
    }

    public JinjaGenerator getJinjaGenerator() {
        return jinjaGenerator;
    }

    public HtmlGenerator getHtmlGenerator() {
        return htmlGenerator;
    }

    /** The {@link CssGenerator} used internally by {@link #getHtmlGenerator()} for embedded {@code <style>} content. */
    public CssGenerator getCssGenerator() {
        return htmlGenerator.getCssGenerator();
    }

    /**
     * The {@link FinalDocumentGenerator} driving {@link #generateFinalDocument()}.
     * It uses its own dedicated {@code HtmlGenerator}/{@code JinjaGenerator}
     * pair rather than {@link #getHtmlGenerator()}/{@link #getJinjaGenerator()}
     * — see the field comment on {@code finalDocumentGenerator} for why
     * sharing them would silently double-count entries in their source maps.
     * {@link FinalDocumentGenerator#countInjectedStylesheets()} and
     * {@link FinalDocumentGenerator#getCombinedSourceMap()} on the object
     * returned here describe exactly the merged final-document pass.
     */
    public FinalDocumentGenerator getFinalDocumentGenerator() {
        return finalDocumentGenerator;
    }

    /** A short, human-readable report of what this instance has (or has not yet) generated. */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("CodeGenerator Summary\n");
        sb.append("======================\n");
        sb.append("Semantic analysis succeeded: ").append(semanticAnalysisSucceeded).append('\n');
        sb.append("Generated: ").append(generated).append('\n');
        sb.append("Python AST supplied: ").append(pythonAst != null).append('\n');
        sb.append("Template AST supplied: ").append(templateAst != null).append('\n');
        if (generated) {
            sb.append("Python source: ")
                    .append(generatedPythonSource != null ? generatedPythonSource.length() + " chars" : "(skipped)")
                    .append('\n');
            sb.append("Jinja source: ")
                    .append(generatedJinjaSource != null ? generatedJinjaSource.length() + " chars" : "(skipped)")
                    .append('\n');
            sb.append("HTML source: ")
                    .append(generatedHtmlSource != null ? generatedHtmlSource.length() + " chars" : "(skipped)")
                    .append('\n');
            sb.append("Final HTML document: ")
                    .append(finalHtmlDocument != null ? finalHtmlDocument.length() + " chars, "
                            + finalDocumentGenerator.countInjectedStylesheets() + " stylesheet(s) injected"
                            : "(not generated - call generateFinalDocument())")
                    .append('\n');
            if (intermediateContext != null) {
                sb.append("Intermediate context variables: ").append(intermediateContext.size()).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getSummary();
    }
}
