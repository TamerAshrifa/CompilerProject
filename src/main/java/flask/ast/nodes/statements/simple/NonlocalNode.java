package flask.ast.nodes.statements.simple;

import flask.ast.nodes.Statement;
import flask.ast.visitor.ASTVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Python {@code nonlocal} statement: {@code nonlocal x, y}.
 *
 * Declares that the listed names refer to variables bound in the nearest
 * enclosing (non-global) scope rather than being new local bindings. Mirrors
 * {@link GlobalNode} exactly, since the two statements have the identical
 * shape (a keyword followed by a comma-separated identifier list) and differ
 * only in which scope they bind to.
 */
public class NonlocalNode extends Statement {

    private final List<String> names;

    public NonlocalNode(List<String> names, int line, int column) {
        super(line, column);
        this.names = new ArrayList<>(names);
    }

    public List<String> getNames() {
        return Collections.unmodifiableList(names);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNonlocal(this);
    }
}
