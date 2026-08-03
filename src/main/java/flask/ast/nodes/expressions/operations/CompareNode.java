package flask.ast.nodes.expressions.operations;

import flask.ast.nodes.Expression;
import flask.ast.visitor.ASTVisitor;
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
}