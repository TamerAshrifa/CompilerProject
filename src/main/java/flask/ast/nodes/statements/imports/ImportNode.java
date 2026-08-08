package flask.ast.nodes.statements.imports;

import flask.ast.nodes.Statement;
import flask.ast.visitor.ASTVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImportNode extends Statement {

    private final List<String> imports;

    public ImportNode(List<String> imports, int line, int column) {
        super(line, column);
        this.imports = new ArrayList<>(imports);
    }

    public List<String> getImports() {
        return Collections.unmodifiableList(imports);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitImport(this);
    }
}