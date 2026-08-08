package template.ast.jinja;

import template.visitor.TemplateVisitor;

/**
 * Base class for all Jinja2 AST nodes.
 * Completely independent from HTML/CSS nodes.
 * 
 * This hierarchy represents Jinja2 language constructs:
 * - Variables and expressions: {{ var }}, {{ var|filter }}
 * - Control flow: {% if %}, {% for %}, {% block %}
 * - Template inheritance: {% extends %}, {% include %}, {% macro %}
 * - Comments: {# comment #}
 */
public abstract class JinjaNode {

    private final int line;
    private final int column;

    protected JinjaNode() {
        this(0, 0);
    }

    protected JinjaNode(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    /**
     * Returns the name identifying this node (e.g. "JinjaIfNode",
     * "JinjaForNode", "JinjaVariableNode"). Resolved automatically from the
     * node's runtime class via {@link Class#getSimpleName()}, mirroring
     * {@code flask.ast.nodes.ASTNode#getNodeName()} and
     * {@code template.ast.css.CssNode#getNodeName()} so that all three AST
     * hierarchies in this project (Python, Jinja2, CSS) expose node identity
     * the same way. Being a normal (non-final) method resolved from the
     * receiver's actual runtime type, this is polymorphism in the same
     * sense as {@link #accept(TemplateVisitor)}: the same call site
     * ({@code node.getNodeName()}) yields a different, always-correct
     * answer depending on the concrete node type, with zero per-class
     * boilerplate and nothing for a subclass to override unless it wants a
     * more specific name.
     */
    public String getNodeName() {
        return getClass().getSimpleName();
    }

    /**
     * Accept a visitor for this node.
     * This enables the visitor pattern for semantic analysis.
     */
    public abstract <T> T accept(TemplateVisitor<T> visitor);
}
