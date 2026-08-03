package flask.ast.nodes.statements.imports;

import flask.ast.nodes.Statement;
import flask.ast.visitor.ASTVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FromImportNode extends Statement {

    private final String moduleName;
    private final List<String> imports;

    public FromImportNode(String moduleName, List<String> imports, int line, int column) {
        super(line, column);
        this.moduleName = moduleName;
        this.imports = new ArrayList<>(imports);
    }

    public String getModuleName() {
        return moduleName;
    }

    public List<String> getImports() {
        return Collections.unmodifiableList(imports);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitFromImport(this);
    }
}