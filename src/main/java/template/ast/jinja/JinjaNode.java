package template.ast.jinja;

import template.visitor.TemplateVisitor;
import printer.Printable;

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
public abstract class JinjaNode implements Printable {

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

    /* ======================================================================
     * Structured AST tree printing.
     *
     * A pure extension of the class above: it adds a new capability
     * (printing a node and its whole subtree as a readable, indented tree)
     * without touching any existing field or method. Nothing above this
     * point was changed to support it. Mirrors
     * flask.ast.nodes.ASTNode's identical extension for the Python AST, so
     * both hierarchies gain the same capability the same way, via the
     * shared printer.TreePrinter engine.
     * ====================================================================== */

    /**
     * Prints this node, and recursively its entire subtree, to standard
     * output as an indented tree that uses box-drawing characters
     * ({@code ├──}, {@code └──}, {@code │}) to show parent/child
     * relationships. See {@link Printable#print} for the exact contract of
     * the {@code indent} parameter.
     *
     * <p>This base implementation is the fallback used by any node that does
     * not override it: it prints only the node's own identity line (its
     * {@link #getNodeName()} and {@link #getLine()}), with no attributes or
     * children, since {@code JinjaNode} itself carries no such structure.
     *
     * <p>Every concrete node class in this hierarchy overrides this method to
     * additionally display its own important attributes (a variable name, a
     * filter, a literal value, ...) and to recurse into its children using
     * the shared {@link TreePrinter} helpers, so the same call,
     * {@code node.print(indent)}, produces a different, type-appropriate
     * tree depending on which concrete node {@code node} actually is at
     * runtime.
     *
     * @param indent the exact prefix to print before this node's own line;
     *               pass {@code ""} to print this node as the root of a tree.
     */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
    }

    /**
     * "NodeType (line N)" — the identity line shared by every node's
     * {@link #print(String)} override, built from the same
     * {@link #getNodeName()} / {@link #getLine()} accessors used elsewhere in
     * this class, so every node's header stays in the exact same format
     * regardless of which subclass is printing itself.
     */
    protected final String selfDescription() {
        return getNodeName() + " (line " + getLine() + ")";
    }
}
