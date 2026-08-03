package flask.ast.nodes;

/**
 * Abstract base for every statement-category AST node (e.g. assignments,
 * if/while/for, function and class definitions, imports, ...).
 *
 * <p>Statement intentionally adds no fields of its own: node name, line and
 * column all come from {@link ASTNode}, its single superclass. Statement
 * exists purely to model the "is-a Statement" branch of the hierarchy so
 * that APIs (like {@code List<Statement>} bodies) can be expressed with a
 * common, meaningful type — inheritance is used here for classification,
 * not for adding duplicate state.
 */
public abstract class Statement extends ASTNode {

    protected Statement(int line, int column) {
        super(line, column);
    }
}
