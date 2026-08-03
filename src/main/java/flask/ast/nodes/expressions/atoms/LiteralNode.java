package flask.ast.nodes.expressions.atoms;

import flask.ast.nodes.Expression;
import flask.ast.visitor.ASTVisitor;

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
}