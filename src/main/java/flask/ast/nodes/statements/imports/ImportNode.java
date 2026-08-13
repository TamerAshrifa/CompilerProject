package flask.ast.nodes.statements.imports;

import flask.ast.nodes.Statement;
import flask.ast.visitor.ASTVisitor;
import printer.TreePrinter;

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

    /** Prints the list of modules this statement imports, e.g. {@code import os, sys}. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        TreePrinter.values(TreePrinter.continuation(indent), true, "Imports", imports);
    }
}