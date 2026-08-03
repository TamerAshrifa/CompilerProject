package flask.ast.nodes.expressions.comprehensions;

import flask.ast.nodes.Expression;
import flask.ast.visitor.ASTVisitor;
import java.util.List;

/**
 * Generator expression: {@code element for target in iterable if cond ...}
 * written WITHOUT surrounding brackets as the sole argument of a call, e.g.
 * {@code sum(x for x in items)} or {@code any(x > 0 for x in values)}.
 *
 * <p>Structurally identical to {@link ListComprehensionNode} (an element
 * plus an ordered list of {@link ForClause}), which is why it shares the
 * same abstract base and the same {@code visitComprehension} dispatch
 * method: for AST-shape and static-analysis purposes a generator
 * expression's sequence of produced values is the same as a list
 * comprehension's, the only difference (lazy vs. eager evaluation) is a
 * runtime concern outside the scope of this static AST.
 */
public class GeneratorExpressionNode extends ComprehensionNode {

    private final Expression element;

    public GeneratorExpressionNode(Expression element, List<ForClause> clauses, int line, int column) {
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
