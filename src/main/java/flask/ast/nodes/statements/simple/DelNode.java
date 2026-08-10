package flask.ast.nodes.statements.simple;

import flask.ast.nodes.Expression;
import flask.ast.nodes.Statement;
import flask.ast.visitor.ASTVisitor;
import printer.TreePrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DelNode extends Statement {

    private final List<Expression> targets;

    public DelNode(List<Expression> targets, int line, int column) {
        super(line, column);
        this.targets = new ArrayList<>(targets);
    }

    public List<Expression> getTargets() {
        return Collections.unmodifiableList(targets);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitDel(this);
    }

    /** Prints the list of targets this statement deletes. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        TreePrinter.children(TreePrinter.continuation(indent), true, "Targets", targets);
    }
}