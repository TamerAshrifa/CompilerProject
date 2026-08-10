package flask.ast.nodes.statements.simple;

import flask.ast.nodes.Expression;
import flask.ast.nodes.Statement;
import flask.ast.visitor.ASTVisitor;
import printer.TreePrinter;

public class ExpressionStatementNode extends Statement {

    private final Expression expression;

    public ExpressionStatementNode(Expression expression, int line, int column) {
        super(line, column);
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitExpressionStatement(this);
    }

    /** Prints the wrapped expression, e.g. a bare call like {@code app.run()}. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        TreePrinter.child(TreePrinter.continuation(indent), true, "Expression", expression);
    }
}