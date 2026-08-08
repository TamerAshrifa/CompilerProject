package flask.ast.nodes.expressions.atoms;

import flask.ast.nodes.Expression;
import flask.ast.visitor.ASTVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListNode extends Expression {

    private final List<Expression> elements;

    public ListNode(List<Expression> elements, int line, int column) {
        super(line, column);
        this.elements = new ArrayList<>(elements);
    }

    /** Convenience constructor for hand-built ASTs with no real source position (e.g. tests). */
    public ListNode(List<Expression> elements) {
        this(elements, 0, 0);
    }

    public List<Expression> getElements() {
        return Collections.unmodifiableList(elements);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitList(this);
    }
}