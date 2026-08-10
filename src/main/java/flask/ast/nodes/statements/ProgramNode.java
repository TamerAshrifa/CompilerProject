package flask.ast.nodes.statements;

import flask.ast.nodes.Statement;
import flask.ast.visitor.ASTVisitor;
import printer.TreePrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProgramNode extends Statement {

    private final List<Statement> statements;

    public ProgramNode(List<Statement> statements, int line, int column) {
        super(line, column);
        this.statements = new ArrayList<>(statements);
    }

    /** Convenience constructor for hand-built ASTs with no real source position (e.g. tests). */
    public ProgramNode(List<Statement> statements) {
        this(statements, 0, 0);
    }

    public List<Statement> getStatements() {
        return Collections.unmodifiableList(statements);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitProgram(this);
    }

    /** Prints this program as its identity line followed by its top-level statements. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        TreePrinter.children(TreePrinter.continuation(indent), true, "Statements", statements);
    }
}