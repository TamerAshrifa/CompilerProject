package template.ast.html;

import template.ast.jinja.JinjaNode;
import template.visitor.TemplateVisitor;

/**
 * A marker left in the HTML tree at the exact position where a Jinja2
 * construct (a {@code {{ expression }}}, an inline {@code {% if %}}, etc.)
 * textually appeared inside HTML content - e.g. the {@code {{ name }}} in
 * {@code <h1>Hello, {{ name }}</h1>}, or a bare {@code {{ title }}} sitting
 * as a sibling between two HTML tags.
 *
 * <p><b>Why this exists.</b> {@link template.TemplateASTBuilder} keeps the
 * HTML tree and the Jinja2 tree fully independent by design (documented on
 * that class): a Jinja2 construct found while building an HTML element's
 * content is hoisted out to the top-level Jinja list rather than kept nested,
 * so that {@link template.ast.TemplateProgramNode#getHtmlElements()} and
 * {@link template.ast.TemplateProgramNode#getJinjaElements()} stay two
 * genuinely separate, independently-typed trees (required so semantic
 * analysis can scope-check Jinja2 constructs - e.g. a {@code {% for %}}
 * loop variable - without needing to know anything about HTML). That
 * independence is exactly right for semantic analysis, but it used to mean
 * the *generation* stage had no record of where in the HTML the hoisted
 * construct belonged, so {@link generator.FinalDocumentGenerator} could only
 * place resolved Jinja output before or after a *whole* HTML subtree, never
 * inside it - producing, for {@code <h1>Hello, {{ name }}</h1>}, a
 * <code>&lt;h1&gt;Hello, &lt;/h1&gt;</code> with the resolved name dropped
 * somewhere else in the document entirely.
 *
 * <p>This node closes that gap without touching the independence semantic
 * analysis relies on: {@link template.TemplateASTBuilder} still hoists the
 * real {@link JinjaNode} to the top-level Jinja list exactly as before, but
 * <em>additionally</em> leaves one of these behind, in place, among the
 * surrounding {@link HtmlNode} children, referencing that same hoisted node
 * by identity. {@link generator.HtmlGenerator} can then render whatever the
 * hoisted node ultimately resolved to (see {@link generator.Generator}) at
 * exactly the right position - or, if generation never ran a resolution pass
 * at all, fall back to printing the construct's own regenerated Jinja2
 * source right there, which is already an improvement on silently dropping
 * it.
 */
public class JinjaHostNode extends HtmlNode {

    private final JinjaNode hostedNode;

    public JinjaHostNode(JinjaNode hostedNode, int line, int column) {
        super(line, column);
        this.hostedNode = hostedNode;
    }

    /**
     * The original, top-level-hoisted Jinja node this placeholder stands in
     * for. Never {@code null}. Renderers should look this identity up in
     * whatever resolution/replacement map generation produced, rather than
     * calling {@code accept} on it directly, since a {@code {% for %}} may
     * have unrolled it into several replacement nodes.
     */
    public JinjaNode getHostedNode() {
        return hostedNode;
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitJinjaHostNode(this);
    }
}
