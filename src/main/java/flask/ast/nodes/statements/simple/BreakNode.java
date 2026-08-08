package flask.ast.nodes.statements.simple;

import flask.ast.nodes.Statement;
import flask.ast.visitor.ASTVisitor;

public class BreakNode extends Statement {

    public BreakNode(int line, int column) {
        super(line, column);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitBreak(this);
    }
}