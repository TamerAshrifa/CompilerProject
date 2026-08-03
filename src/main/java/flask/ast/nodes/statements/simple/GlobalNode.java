package flask.ast.nodes.statements.simple;

import flask.ast.nodes.Statement;
import flask.ast.visitor.ASTVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GlobalNode extends Statement {

    private final List<String> names;

    public GlobalNode(List<String> names, int line, int column) {
        super(line, column);
        this.names = new ArrayList<>(names);
    }

    public List<String> getNames() {
        return Collections.unmodifiableList(names);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitGlobal(this);
    }
}