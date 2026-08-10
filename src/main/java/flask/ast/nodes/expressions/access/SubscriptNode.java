package flask.ast.nodes.expressions.access;

import flask.ast.nodes.Expression;
import flask.ast.visitor.ASTVisitor;
import printer.TreePrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubscriptNode extends Expression {

    private final Expression target;
    private final List<Expression> slices;

    public SubscriptNode(Expression target, List<Expression> slices, int line, int column) {
        super(line, column);
        this.target = target;
        this.slices = new ArrayList<>(slices);
    }

    /** Convenience constructor for hand-built ASTs with no real source position (e.g. tests). */
    public SubscriptNode(Expression target, List<Expression> slices) {
        this(target, slices, 0, 0);
    }

    public Expression getTarget() {
        return target;
    }

    public Expression getObject() {
        return target;
    }

    public List<Expression> getSlices() {
        return Collections.unmodifiableList(slices);
    }

    public Expression getIndex() {
        return slices.isEmpty() ? null : slices.get(0);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitSubscript(this);
    }

    /** Prints the subscripted target followed by its slice/index expression(s), e.g. {@code target[a:b]}. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        String base = TreePrinter.continuation(indent);
        TreePrinter.fields(base,
                (ind, last) -> TreePrinter.child(ind, last, "Target", target),
                (ind, last) -> TreePrinter.children(ind, last, "Slices", slices));
    }
}