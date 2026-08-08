package generator;

import template.ast.TemplateProgramNode;
import template.ast.html.HtmlNode;
import template.ast.jinja.JinjaNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Assembles the pipeline's "Final HTML Document" stage: it merges the output
 * of {@link HtmlGenerator} and {@link JinjaGenerator} — run over the same
 * (ideally already-resolved, see {@link CodeGenerator#generateWithResolvedContext()})
 * {@link TemplateProgramNode} — back into a single document, in source order.
 *
 * <h2>Why this class exists</h2>
 * As documented on {@link HtmlGenerator} and {@link template.ast.TemplateProgramNode}
 * itself, {@code TemplateASTBuilder} builds the HTML and Jinja2 trees as two
 * independent lists: a Jinja construct that textually sat inside an HTML
 * tag's content is hoisted out to the program's top-level Jinja list while
 * parsing, rather than kept nested. {@link HtmlGenerator} alone therefore
 * cannot show resolved {@code {{ variable }}} values (they live only in the
 * Jinja tree) and {@link JinjaGenerator} alone cannot show them in their HTML
 * context. This class is what actually recombines them — driven entirely by
 * data already on the existing AST nodes (their own {@code getLine()}/
 * {@code getColumn()}), never by re-parsing text or string search-and-replace.
 *
 * <h2>What "merge by position" means, precisely</h2>
 * Every {@link HtmlNode} and {@link JinjaNode} records the source line/column
 * it was parsed from — that information survives the hoisting even though the
 * parent/child relationship does not. This class collects
 * {@code program.getHtmlElements()} and {@code program.getJinjaElements()}
 * into one list, sorts it by {@code (line, column)} — a stable sort, so items
 * that legitimately share a position keep their HTML-before-Jinja relative
 * order — and generates each item, in that order, through whichever of its
 * two generators actually owns that node's type. No new generation logic is
 * written here: every piece of text still comes from {@link HtmlGenerator} or
 * {@link JinjaGenerator} exactly as {@link CodeGeneratorTest} already
 * verifies them in isolation; this class only decides the order to call them in.
 *
 * <h2>Honest scope</h2>
 * This reconstructs top-level document order correctly and deterministically
 * — the common, important case ({@code {% extends %}}, {@code {% block %}},
 * a {@code {{ variable }}} between two top-level tags, and so on). It does
 * <em>not</em> attempt to re-insert a hoisted Jinja construct back inside the
 * specific HTML element it originally sat inside several levels deep: doing
 * that would require knowing where each HTML element's content ends, and
 * nothing in the existing AST records that (only each node's own start
 * position is kept) — inventing an end-position heuristic would mean
 * guessing rather than reading the AST, which is exactly what this class is
 * built to avoid. {@link HtmlGenerator} and {@link JinjaGenerator} each
 * continue to reproduce their own half of the tree faithfully on their own
 * (see {@code CodeGeneratorTest.runJinjaRoundTripTest} /
 * {@code runHtmlElementAndAttributeTest}); this class adds top-level
 * recombination on top of that without weakening either.
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
     * Produces the Final HTML Document: every top-level HTML and Jinja node
     * in {@code program}, generated through the appropriate existing
     * generator and joined in ascending source-position order.
     */
    public String generate(TemplateProgramNode program) {
        List<PositionedItem> items = new ArrayList<>();
        for (HtmlNode node : program.getHtmlElements()) {
            items.add(new PositionedItem(node.getLine(), node.getColumn(), node, null));
        }
        for (JinjaNode node : program.getJinjaElements()) {
            items.add(new PositionedItem(node.getLine(), node.getColumn(), null, node));
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
