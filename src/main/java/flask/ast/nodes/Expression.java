package flask.ast.nodes;

/**
 * Abstract base for every expression-category AST node (e.g. literals,
 * identifiers, binary/unary operations, function calls, ...).
 *
 * <p>Like {@link Statement}, Expression adds no fields of its own — node
 * name, line and column are all inherited from {@link ASTNode}. Expression
 * exists to model the "is-a Expression" branch of the hierarchy so that
 * expression-only positions in the tree (operands, call arguments, targets,
 * etc.) can be typed precisely.
 */
public abstract class Expression extends ASTNode {

    protected Expression(int line, int column) {
        super(line, column);
    }
}
