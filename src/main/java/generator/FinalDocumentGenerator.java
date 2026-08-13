package generator;

import template.ast.TemplateProgramNode;
import template.ast.html.HtmlElementNode;
import template.ast.html.HtmlNode;
import template.ast.html.JinjaHostNode;
import template.ast.jinja.JinjaNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;


/**
 * Assembles the pipeline's "Final HTML Document" stage: it merges the output
 * of {@link HtmlGenerator} and {@link JinjaGenerator} — run over the same
 * (ideally already-resolved, see {@link CodeGenerator#generateWithResolvedContext()})
 * {@link TemplateProgramNode} — back into a single document, in source order.
 *
 * <h2>Why this class exists</h2>
 * As documented on {@link template.ast.TemplateProgramNode}, {@code
 * TemplateASTBuilder} still builds the HTML and Jinja2 trees as two
 * independent lists for semantic-analysis purposes: a Jinja construct that
 * textually sat inside an HTML tag's content is hoisted out to the program's
 * top-level Jinja list while parsing, rather than kept nested there. What
 * changed is that a {@link template.ast.html.JinjaHostNode} placeholder is
 * now left behind at that exact position among the surrounding {@link
 * HtmlNode} children (see that class), so {@link HtmlGenerator} — via {@link
 * HtmlGenerator#visitJinjaHostNode} — CAN show a resolved {@code
 * {{ variable }}} value in its original HTML context on its own, given the
 * replacement map {@link Generator} produces (see {@link
 * HtmlGenerator#withResolvedReplacements}). This class's job is narrower
 * than it used to be as a result: it still recombines the two trees'
 * genuinely <em>top-level</em> siblings — driven entirely by data already on
 * the existing AST nodes (their own {@code getLine()}/{@code getColumn()}),
 * never by re-parsing text or string search-and-replace — while skipping any
 * top-level Jinja node a placeholder already renders inline, so nothing
 * doubles up (see {@link #withHostedReplacementNodes} and {@link #generate}).

 *
 * <h2>What "merge by position" means, precisely</h2>
 * Every {@link HtmlNode} and {@link JinjaNode} records the source line/column
 * it was parsed from — that information survives the hoisting even though the
 * parent/child relationship does not (except, now, where a {@link
 * template.ast.html.JinjaHostNode} placeholder preserves it directly). This
 * class collects {@code program.getHtmlElements()} and {@code
 * program.getJinjaElements()} into one list, sorts it by {@code (line,
 * column)} — a stable sort, so items that legitimately share a position keep
 * their HTML-before-Jinja relative order — and generates each item, in that
 * order, through whichever of its two generators actually owns that node's
 * type. No new generation logic is written here: every piece of text still
 * comes from {@link HtmlGenerator} or {@link JinjaGenerator} exactly as
 * {@link CodeGeneratorTest} already verifies them in isolation; this class
 * only decides the order to call them in.
 *
 * <h2>Scope</h2>
 * A hoisted Jinja construct that textually sat inside an HTML tag's content —
 * the common, important case ({@code {{ variable }}} inside a heading or
 * paragraph, an {@code {% if %}}'s HTML branches, and so on) — is now
 * correctly re-nested at its original position by {@link HtmlGenerator}
 * itself (see "Why this class exists" above), not merely placed as a
 * top-level sibling by this class. What is genuinely still out of scope is a
 * {@code {% for %}} loop wrapping HTML content: repeating a whole subtree
 * once per resolved element (as opposed to substituting a value in place, or
 * excluding a whole subtree outright — both of which are handled) would mean
 * cloning part of the HTML tree per iteration, which is a larger change than
 * this addresses. {@link webapp.PageCompiler}'s existing, tested pattern of
 * keeping a {@code {% for %}} loop body as plain Jinja text (no HTML tags
 * inside the loop itself) remains the correct way to drive a list of HTML
 * elements from resolved data.

 *
 * <h2>CSS injection</h2>
 * No separate mechanism is needed for this: a {@code <style>} tag is an
 * {@link HtmlNode} ({@link template.ast.html.StyleElementNode}) wherever it
 * sits in the HTML tree, so it is already covered by the HTML half of the
 * merge above, and {@link HtmlGenerator} already renders it by delegating to
 * a real {@link CssGenerator} over the parsed {@code CssStylesheetNode} (see
 * {@code HtmlGenerator.visitStyleElement}) — never by copying CSS source text
 * around. {@link #countInjectedStylesheets()} exposes how many {@code <style>}
 * elements actually contributed to the last {@link #generate} call, read
 * directly off the recorded {@link SourceMapping} trail (tying this back to
 * requirement 10, preserving source mapping information) rather than a
 * separate tree walk.
 */
public class FinalDocumentGenerator {

    private final HtmlGenerator htmlGenerator;
    private final JinjaGenerator jinjaGenerator;
    private Set<JinjaNode> hostedReplacementNodes = Collections.emptySet();


    public FinalDocumentGenerator() {
        this(new HtmlGenerator(), new JinjaGenerator());
    }

    /**
     * Allows a caller to supply its own {@link HtmlGenerator}/{@link JinjaGenerator}
     * pair instead of this class creating its own. Note that {@link CodeGenerator}
     * deliberately does <em>not</em> do this — sharing instances that are also
     * used elsewhere for whole-tree generation means this class's per-top-level-node
     * {@code accept()} calls (see {@link #generate}) become a second, un-reset
     * traversal through the same {@link GenerationSupport}, which silently
     * duplicates entries in {@link HtmlGenerator#getSourceMap()} (and therefore
     * inflates {@link #countInjectedStylesheets()}). Prefer the no-arg
     * constructor unless the caller has a specific reason to inspect a single,
     * combined source map across both use sites and has accounted for that.
     */
    public FinalDocumentGenerator(HtmlGenerator htmlGenerator, JinjaGenerator jinjaGenerator) {
        this.htmlGenerator = htmlGenerator;
        this.jinjaGenerator = jinjaGenerator;
    }

    /**
     * Supplies {@link Generator#getHostedReplacementNodes()} so {@link
     * #generate}'s top-level merge can skip a resolved Jinja value that
     * {@link #getHtmlGenerator()} already shows inline via a {@link
     * template.ast.html.JinjaHostNode} placeholder, rather than also
     * emitting it a second time as an unwanted top-level sibling. Optional:
     * with none supplied, every top-level Jinja node is merged in exactly as
     * before.
     *
     * @return this instance, for chaining
     */
    public FinalDocumentGenerator withHostedReplacementNodes(Set<JinjaNode> hostedReplacementNodes) {
        this.hostedReplacementNodes = hostedReplacementNodes != null ? hostedReplacementNodes : Collections.emptySet();
        return this;
    }

    /**
     * Produces the Final HTML Document: every top-level HTML and Jinja node
     * in {@code program}, generated through the appropriate existing
     * generator and joined in ascending source-position order. A top-level
     * Jinja node is skipped when either:
     * <ul>
     *   <li>it is present in {@link #withHostedReplacementNodes} (the
     *       resolved-tree case: {@code program} came from {@link
     *       Generator#getTransformedTemplate()}, whose Jinja list holds
     *       resolved <em>replacement</em> nodes — the identities {@link
     *       Generator#getHostedReplacementNodes()} tracks), or</li>
     *   <li>it is itself directly referenced by a {@link JinjaHostNode}
     *       found by scanning {@code program.getHtmlElements()} (the raw,
     *       unresolved-tree case: nothing has replaced it, so it is simply
     *       the very same node object the placeholder already points at).</li>
     * </ul>
     * Either way, the point is the same: a value {@link #getHtmlGenerator()}
     * already renders inline for this call should not <em>also</em> be
     * emitted a second time as a top-level sibling.
     */
    public String generate(TemplateProgramNode program) {
        Set<JinjaNode> hostedInThisTree = Collections.newSetFromMap(new IdentityHashMap<>());
        for (HtmlNode htmlNode : program.getHtmlElements()) {
            collectHostedJinjaNodes(htmlNode, hostedInThisTree);
        }


        List<PositionedItem> items = new ArrayList<>();
        for (HtmlNode node : program.getHtmlElements()) {
            items.add(new PositionedItem(node.getLine(), node.getColumn(), node, null));
        }
        for (JinjaNode node : program.getJinjaElements()) {
            if (!hostedReplacementNodes.contains(node) && !hostedInThisTree.contains(node)) {
                items.add(new PositionedItem(node.getLine(), node.getColumn(), null, node));
            }
        }
        // Stable sort: ties (including the common line=0,column=0 default
        // used by hand-built/synthetic nodes) keep the order the two lists
        // were appended in above, i.e. HTML before Jinja.
        items.sort(Comparator.<PositionedItem>comparingInt(i -> i.line).thenComparingInt(i -> i.column));

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sb.append('\n');
            }
            PositionedItem item = items.get(i);
            sb.append(item.htmlNode != null ? item.htmlNode.accept(htmlGenerator) : item.jinjaNode.accept(jinjaGenerator));
        }
        return sb.toString();
    }

    /** Recursively collects every original node referenced by a {@link JinjaHostNode} anywhere under {@code htmlNode}. */
    private void collectHostedJinjaNodes(HtmlNode htmlNode, Set<JinjaNode> out) {
        if (htmlNode instanceof JinjaHostNode hostNode) {
            out.add(hostNode.getHostedNode());
        } else if (htmlNode instanceof HtmlElementNode elementNode) {
            for (HtmlNode child : elementNode.getChildren()) {
                collectHostedJinjaNodes(child, out);
            }
        }
        // Other HtmlNode subtypes (text, comment, style, attribute) have no
        // nested HtmlNode children to walk.
    }


    public HtmlGenerator getHtmlGenerator() {
        return htmlGenerator;
    }

    public JinjaGenerator getJinjaGenerator() {
        return jinjaGenerator;
    }

    /** The {@link CssGenerator} used internally by {@link #getHtmlGenerator()} for embedded {@code <style>} content. */
    public CssGenerator getCssGenerator() {
        return htmlGenerator.getCssGenerator();
    }

    /**
     * How many {@code <style>} elements were rendered (and therefore had
     * their CSS injected) during the last {@link #generate} call, derived
     * from {@code getHtmlGenerator().getSourceMap()} rather than a separate
     * tree walk — see the class Javadoc's "CSS injection" section.
     */
    public int countInjectedStylesheets() {
        int count = 0;
        for (SourceMapping mapping : htmlGenerator.getSourceMap()) {
            if ("StyleElementNode".equals(mapping.getNodeName())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Every {@link SourceMapping} entry recorded by either generator during
     * the last {@link #generate} call, merged into one list ordered by each
     * entry's own original source position — a document-order view spanning
     * both the HTML and the Jinja half, addressing requirement 10 ("preserve
     * source mapping information") for the merged output specifically, not
     * just for each generator in isolation.
     */
    public List<SourceMapping> getCombinedSourceMap() {
        List<SourceMapping> combined = new ArrayList<>(htmlGenerator.getSourceMap());
        combined.addAll(jinjaGenerator.getSourceMap());
        combined.sort(Comparator.comparingInt(SourceMapping::getSourceLine)
                .thenComparingInt(SourceMapping::getSourceColumn));
        return combined;
    }

    /** Sort key pairing a node's own source position with which generator (and which node) it belongs to. */
    private static final class PositionedItem {
        final int line;
        final int column;
        final HtmlNode htmlNode;
        final JinjaNode jinjaNode;

        PositionedItem(int line, int column, HtmlNode htmlNode, JinjaNode jinjaNode) {
            this.line = line;
            this.column = column;
            this.htmlNode = htmlNode;
            this.jinjaNode = jinjaNode;
        }
    }
}
