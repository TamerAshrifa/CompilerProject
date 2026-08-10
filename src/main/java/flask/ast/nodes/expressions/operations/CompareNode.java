package flask.ast.nodes.expressions.operations;

import flask.ast.nodes.Expression;
import flask.ast.visitor.ASTVisitor;
import printer.TreePrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompareNode extends Expression {

    private final Expression left;
    private final List<String> operators;
    private final List<Expression> comparators;

    public CompareNode(Expression left, List<String> operators, List<Expression> comparators, int line, int column) {
        super(line, column);
        this.left = left;
        this.operators = new ArrayList<>(operators);
        this.comparators = new ArrayList<>(comparators);
    }

    public Expression getLeft() { return left; }
    public List<String> getOperators() { return Collections.unmodifiableList(operators); }
    public List<Expression> getComparators() { return Collections.unmodifiableList(comparators); }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitCompare(this);
    }

    /**
     * Prints the leading operand, then the chained operators and their
     * comparators, e.g. Python's chained comparison {@code a < b <= c}.
     */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        String base = TreePrinter.continuation(indent);
        TreePrinter.fields(base,
                (ind, last) -> TreePrinter.child(ind, last, "Left", left),
                (ind, last) -> TreePrinter.values(ind, last, "Operators", operators),
                (ind, last) -> TreePrinter.children(ind, last, "Comparators", comparators));
    }
}