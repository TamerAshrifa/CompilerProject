package flask.ast.nodes.statements.simple;

import flask.ast.nodes.Expression;
import flask.ast.nodes.Statement;
import flask.ast.visitor.ASTVisitor;
import printer.TreePrinter;

import java.util.ArrayList;
import java.util.List;

public class AssertNode extends Statement {

    private final Expression test;
    private final Expression message;

    public AssertNode(Expression test, Expression message, int line, int column) {
        super(line, column);
        this.test = test;
        this.message = message;
    }

    public Expression getTest() {
        return test;
    }

    public boolean hasMessage() {
        return message != null;
    }

    public Expression getMessage() {
        return message;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitAssert(this);
    }

    /** Prints the asserted condition, plus the optional failure message when present. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        List<TreePrinter.Field> fields = new ArrayList<>();
        fields.add((ind, last) -> TreePrinter.child(ind, last, "Test", test));
        if (hasMessage()) {
            fields.add((ind, last) -> TreePrinter.child(ind, last, "Message", message));
        }
        TreePrinter.fields(TreePrinter.continuation(indent), fields);
    }
}