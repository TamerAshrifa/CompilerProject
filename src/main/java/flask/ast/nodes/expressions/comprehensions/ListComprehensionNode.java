package flask.ast.nodes.expressions.comprehensions;

import flask.ast.nodes.Expression;
import flask.ast.visitor.ASTVisitor;
import printer.TreePrinter;

import java.util.List;

/**
 * List comprehension: [element for target in iterable if cond ...]
 */
public class ListComprehensionNode extends ComprehensionNode {

    private final Expression element;

    public ListComprehensionNode(Expression element, List<ForClause> clauses, int line, int column) {
        super(clauses, line, column);
        this.element = element;
    }

    public Expression getElement() {
        return element;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitComprehension(this);
    }

    /** Prints the produced element followed by its {@code for}/{@code if} clauses. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        String base = TreePrinter.continuation(indent);
        TreePrinter.fields(base,
                (ind, last) -> TreePrinter.child(ind, last, "Element", element),
                clausesField());
    }
}
