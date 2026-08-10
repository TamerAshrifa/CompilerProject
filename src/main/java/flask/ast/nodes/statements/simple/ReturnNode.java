package flask.ast.nodes.statements.simple;

import flask.ast.nodes.Expression;
import flask.ast.nodes.Statement;
import flask.ast.visitor.ASTVisitor;
import printer.TreePrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReturnNode extends Statement {

    private final List<Expression> values;

    public ReturnNode(List<Expression> values, int line, int column) {
        super(line, column);
        this.values = new ArrayList<>(values);
    }

    /** Convenience constructor for hand-built ASTs with no real source position (e.g. tests). */
    public ReturnNode(List<Expression> values) {
        this(values, 0, 0);
    }

    public List<Expression> getValues() {
        return Collections.unmodifiableList(values);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitReturn(this);
    }

    /**
     * Prints the returned value(s) - a bare {@code return} has an empty list,
     * a single value the common {@code return x} case, and multiple values a
     * tuple-return such as {@code return a, b}.
     */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        TreePrinter.children(TreePrinter.continuation(indent), true, "Values", values);
    }
}