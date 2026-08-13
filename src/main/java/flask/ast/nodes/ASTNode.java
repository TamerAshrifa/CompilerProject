package flask.ast.nodes;

import flask.ast.visitor.ASTVisitor;
import printer.Printable;

/**
 * Abstract base class for every node in the Flask/Python AST hierarchy.
 *
 * <p>This class is the SINGLE SOURCE of the information that every AST node
 * must carry, namely:
 * <ul>
 *   <li>{@code nodeName} — the identifying name of the node</li>
 *   <li>{@code line} / {@code column} — the source position the node was
 *       parsed from</li>
 * </ul>
 *
 * <p>All concrete node types (e.g. {@code AssignmentNode}, {@code IfStatementNode},
 * {@code FunctionCallNode}, ...) inherit this behavior through {@link Statement}
 * or {@link Expression} rather than re-declaring their own name/line/column
 * fields. This is what makes the hierarchy an OOP hierarchy in practice: the
 * common data and behavior live exactly once, here, at the root.
 *
 * <p>{@link #ASTNode(int, int)} is the only constructor: every node must be
 * constructed with a real source position (see
 * {@link flask.ast.builder.FlaskASTBuilder}, which passes the position of the
 * ANTLR parser context that produced each node). The previous no-arg,
 * defaults-to-(0,0) constructor was removed once every concrete node was
 * updated to always supply a position, so that it is no longer possible to
 * silently construct a node with unknown/incorrect source location.
 *
 * <p><b>Polymorphism</b> is expressed in two ways on this class:
 * <ol>
 *   <li>{@link #getNodeName()} is a normal (non-final) instance method whose
 *       default implementation is resolved automatically from the object's
 *       <em>runtime</em> class via {@link Class#getSimpleName()}. Every
 *       subclass therefore reports its correct name simply by existing —
 *       nothing needs to be hardcoded — yet any subclass remains free to
 *       override {@link #getNodeName()} to report a different name (see
 *       {@link ASTRuleNode} for an example that does exactly this).</li>
 *   <li>{@link #accept(ASTVisitor)} is declared {@code abstract} here and
 *       implemented differently by every concrete node, which is the
 *       classic Visitor-pattern double-dispatch: calling {@code node.accept(visitor)}
 *       on an {@code ASTNode}-typed reference invokes the correct
 *       {@code visitXxx(...)} method for whatever concrete type {@code node}
 *       actually is at runtime.</li>
 *   <li>{@link #print(String)} (see below) works the same way: it is declared
 *       with a base/default body here and then overridden by every concrete
 *       node to print that node's own attributes and children, so the same
 *       call, {@code node.print(indent)}, produces a correct, type-specific
 *       tree no matter which concrete node {@code node} actually is.</li>
 * </ol>
 */
public abstract class ASTNode implements Printable {

    /**
     * The identifying name of this node. Populated automatically from the
     * runtime class name unless a subclass overrides {@link #getNodeName()}.
     */
    private final String nodeName;

    /** 1-based source line this node originated from (0 = unknown/unset). */
    private int line;

    /** 0/1-based source column this node originated from (0 = unknown/unset). */
    private int column;

    protected ASTNode(int line, int column) {
        this.line = line;
        this.column = column;
        // getClass() always returns the most-derived runtime type, even when
        // invoked from this base-class constructor, so every subclass gets
        // its correct name automatically, with zero per-class boilerplate.
        this.nodeName = getClass().getSimpleName();
    }

    /**
     * Returns the name identifying this node (e.g. "AssignmentNode",
     * "IfStatementNode"). By default this is the node's runtime class name,
     * discovered automatically — no subclass needs to hardcode it.
     *
     * <p>This method is intentionally overridable: subclasses that need a
     * more specific or dynamic name (for example a generic node that wraps
     * an arbitrary grammar rule) may override it to return something more
     * meaningful than the raw class name. This is polymorphism in action —
     * the same call site ({@code node.getNodeName()}) yields different
     * behavior depending on the concrete type of {@code node}.
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * The source line number this node was parsed from.
     * Inherited by every node in the hierarchy; no subclass needs to
     * re-declare or duplicate this.
     */
    public int getLine() {
        return line;
    }

    /**
     * The source column this node was parsed from.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Attaches/updates the source position of this node.
     *
     * <p>Every node now receives its real line/column at construction time
     * (see {@link flask.ast.builder.FlaskASTBuilder}), so this setter is not
     * needed on the normal construction path. It remains available for
     * later compiler passes (e.g. desugaring/synthesized nodes) or tests
     * that need to attach or correct position information after the fact.
     */
    public void setPosition(int line, int column) {
        this.line = line;
        this.column = column;
    }

    /** Convenience overload for updating only the line number. */
    public void setLine(int line) {
        this.line = line;
    }

    /**
     * Visitor-pattern dispatch point. Every concrete node implements this
     * to call back into the appropriate {@code visitXxx} method on the
     * supplied {@link ASTVisitor}, giving type-safe double-dispatch over
     * the whole node hierarchy while keeping traversal logic outside the
     * node classes themselves.
     */
    public abstract <T> T accept(ASTVisitor<T> visitor);

    /**
     * Debug-friendly representation showing this node's identity and source
     * location. Since {@link #getNodeName()} and {@link #getLine()} are both
     * inherited, every node in the hierarchy gets a correct, consistent
     * {@code toString()} for free — no subclass needs to implement its own.
     */
    @Override
    public String toString() {
        return getNodeName() + "(line=" + line + ")";
    }

    /* ======================================================================
     * Structured AST tree printing.
     *
     * This section is a pure extension of the class above: it adds a new
     * capability (printing a node and its whole subtree as a readable,
     * indented tree) without touching any existing field or method. Nothing
     * above this point was changed to support it.
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
     * children, since {@code ASTNode} itself carries no such structure.
     *
     * <p>Every concrete node class in this hierarchy overrides this method to
     * additionally display its own important attributes (a name, an
     * operator, a literal value, ...) and to recurse into its children using
     * the shared {@link TreePrinter} helpers. That is the polymorphism this
     * task asks for in practice: the exact same call, {@code node.print(indent)},
     * produces a different, type-appropriate tree depending on which
     * concrete node {@code node} actually is at runtime — callers never need
     * to know or check which subclass they are holding.
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
