package flask.ast.nodes;

import flask.ast.visitor.ASTVisitor;

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
 * </ol>
 */
public abstract class ASTNode {

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
}
