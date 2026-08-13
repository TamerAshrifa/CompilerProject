package flask.ast.nodes.expressions.comprehensions;

import flask.ast.nodes.Expression;
import flask.ast.visitor.ASTVisitor;
import printer.TreePrinter;

import java.util.List;

/**
 * Dict comprehension: {key: value for target in iterable if cond ...}
 */
public class DictComprehensionNode extends ComprehensionNode {

    private final Expression key;
    private final Expression value;

    public DictComprehensionNode(Expression key, Expression value, List<ForClause> clauses, int line, int column) {
        super(clauses, line, column);
        this.key = key;
        this.value = value;
    }

    public Expression getKey() {
        return key;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitComprehension(this);
    }

    /** Prints the produced key and value followed by the {@code for}/{@code if} clauses. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        String base = TreePrinter.continuation(indent);
        TreePrinter.fields(base,
                (ind, last) -> TreePrinter.child(ind, last, "Key", key),
                (ind, last) -> TreePrinter.child(ind, last, "Value", value),
                clausesField());
    }
}
