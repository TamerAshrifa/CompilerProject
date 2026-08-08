package flask.ast.nodes.statements.simple;

import flask.ast.nodes.Expression;
import flask.ast.nodes.Statement;
import flask.ast.visitor.ASTVisitor;

public class AssignmentNode extends Statement {

    private final Expression target;
    private final Expression value;

    public AssignmentNode(Expression target, Expression value, int line, int column) {
        super(line, column);
        this.target = target;
        this.value = value;
    }

    /** Convenience constructor for hand-built ASTs with no real source position (e.g. tests). */
    public AssignmentNode(Expression target, Expression value) {
        this(target, value, 0, 0);
    }

    public Expression getTarget() {
        return target;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitAssignment(this);
    }
}