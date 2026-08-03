package flask.ast.nodes.expressions.comprehensions;

import flask.ast.nodes.Expression;
import flask.ast.visitor.ASTVisitor;
import java.util.List;

/**
 * Set comprehension: {element for target in iterable if cond ...}
 */
public class SetComprehensionNode extends ComprehensionNode {

    private final Expression element;

    public SetComprehensionNode(Expression element, List<ForClause> clauses, int line, int column) {
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
}
