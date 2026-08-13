package flask.ast.nodes.expressions.operations;

import flask.ast.nodes.Expression;
import flask.ast.nodes.expressions.comprehensions.ComprehensionNode;
import flask.ast.visitor.ASTVisitor;
import printer.TreePrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LambdaNode extends Expression {

    private final List<String> parameters;
    private final Expression body;

    public LambdaNode(List<String> parameters, Expression body, int line, int column) {
        super(line, column);
        this.parameters = new ArrayList<>(parameters);
        this.body = body;
    }

    public List<String> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    public Expression getBody() {
        return body;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitLambda(this);
    }

    /** Prints the lambda's parameter names followed by its body expression. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        String base = TreePrinter.continuation(indent);
        TreePrinter.fields(base,
                (ind, last) -> TreePrinter.values(ind, last, "Parameters", parameters),
                (ind, last) -> TreePrinter.child(ind, last, "Body", body));
    }
}