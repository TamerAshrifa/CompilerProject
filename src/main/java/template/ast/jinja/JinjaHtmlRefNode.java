package template.ast.jinja;

import printer.TreePrinter;
import template.ast.html.HtmlNode;
import template.visitor.TemplateVisitor;

/**
 * A bookkeeping-only marker placed inside a {@code {% if %}}/{@code {% elif %}}/
 * {@code {% else %}} branch's body wherever an {@link HtmlNode} textually
 * appeared inside that branch - e.g. the {@code <p>} in
 * {@code {% if is_profitable %}<p>Profitable quarter</p>{% else %}...{% endif %}}.
 *
 * <p><b>Why this exists.</b> Per {@link template.TemplateASTBuilder}'s
 * documented HTML/Jinja2 independence, that {@code <p>} node itself still
 * lives in the ordinary, flat, top-level HTML tree - unconditionally, since
 * the HTML tree on its own has no notion of "belongs to the branch not
 * taken". Without this marker, {@link generator.Generator}'s {@code {% if %}}
 * resolution could correctly pick the taken branch on the *Jinja* side, but
 * had no way to know that a top-level HTML node it never touches should be
 * excluded from the final document because it textually belonged to the
 * branch that lost - so both branches' HTML rendered unconditionally,
 * regardless of which one the condition actually selected.
 *
 * <p>This node is never rendered as output: {@link generator.Generator}
 * consumes it while resolving a {@code {% if %}} (collecting the {@link
 * HtmlNode}s referenced only by the untaken branch(es) so they can be
 * excluded from the resolved tree's HTML list) and strips it out of every
 * transformed body it produces. It therefore has no meaningful "accept" or
 * "print" behavior of its own beyond a safe, descriptive default - nothing
 * in the generation or printing pipeline should normally reach one still
 * attached to a tree that is being rendered or printed as an end result.
 */
public class JinjaHtmlRefNode extends JinjaNode {

    private final HtmlNode referencedHtmlNode;

    public JinjaHtmlRefNode(HtmlNode referencedHtmlNode, int line, int column) {
        super(line, column);
        this.referencedHtmlNode = referencedHtmlNode;
    }

    /** The top-level HTML node this branch textually contained, by identity. */
    public HtmlNode getReferencedHtmlNode() {
        return referencedHtmlNode;
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitJinjaHtmlRef(this);
    }

    /** Prints a one-line reference to the HTML node it stands in for. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        TreePrinter.leaf(TreePrinter.continuation(indent), true, "References",
                referencedHtmlNode.getNodeName() + " (line " + referencedHtmlNode.getLine() + ")");
    }
}
