package flask.ast.nodes.expressions.atoms;

import flask.ast.nodes.Expression;
import flask.ast.visitor.ASTVisitor;
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
}