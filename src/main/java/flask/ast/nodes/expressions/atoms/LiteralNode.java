package flask.ast.nodes.expressions.atoms;

import flask.ast.nodes.Expression;
import flask.ast.visitor.ASTVisitor;
import printer.TreePrinter;

public class LiteralNode extends Expression {

    private final Object value;

    public LiteralNode(Object value, int line, int column) {
        super(line, column);
        this.value = value;
    }

    /** Convenience constructor for hand-built ASTs with no real source position (e.g. tests). */
    public LiteralNode(Object value) {
        this(value, 0, 0);
    }

    public Object getValue() {
        return value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitLiteral(this);
    }

    /**
     * Prints this literal's value. Formatted specially (rather than via the
     * generic {@link TreePrinter#leaf}) so that Python's {@code None}
     * literal - which is a perfectly real, present value that happens to be
     * Java {@code null} - is never confused with {@code TreePrinter}'s
     * "(none)" marker for an absent field, and so string literals are shown
     * quoted to distinguish them from other atoms at a glance.
     */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        String display = (value == null) ? "None" : (value instanceof String) ? "\"" + value + "\"" : String.valueOf(value);
        TreePrinter.leaf(TreePrinter.continuation(indent), true, "Value", display);
    }
}