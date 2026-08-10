package flask.ast.nodes.expressions.atoms;

import flask.ast.nodes.Expression;
import flask.ast.visitor.ASTVisitor;
import printer.TreePrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TupleNode extends Expression {

    private final List<Expression> elements;

    public TupleNode(List<Expression> elements, int line, int column) {
        super(line, column);
        this.elements = new ArrayList<>(elements);
    }

    public List<Expression> getElements() {
        return Collections.unmodifiableList(elements);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitTuple(this);
    }

    /** Prints this tuple literal's elements, e.g. {@code (1, 2, 3)}. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        TreePrinter.children(TreePrinter.continuation(indent), true, "Elements", elements);
    }
}