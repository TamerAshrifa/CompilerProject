package flask.ast.nodes.statements.compound;

import flask.ast.nodes.Statement;
import flask.ast.nodes.Expression;
import flask.ast.nodes.helpers.Decorator;
import flask.ast.nodes.helpers.Parameter;
import flask.ast.visitor.ASTVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FunctionDefNode extends Statement {

    private final String name;
    private final List<Decorator> decorators;
    private final List<Parameter> parameters;
    private final List<Statement> body;
    private final Expression returnType;

    public FunctionDefNode(String name, List<Decorator> decorators, List<Parameter> parameters, List<Statement> body,
                           Expression returnType, int line, int column) {
        super(line, column);
        this.name = name;
        this.decorators = new ArrayList<>(decorators);
        this.parameters = new ArrayList<>(parameters);
        this.body = new ArrayList<>(body);
        this.returnType = returnType;
    }

    /** Convenience constructor for hand-built ASTs with no real source position (e.g. tests). */
    public FunctionDefNode(String name, List<Decorator> decorators, List<Parameter> parameters, List<Statement> body,
                           Expression returnType) {
        this(name, decorators, parameters, body, returnType, 0, 0);
    }

    public String getName() { return name; }
    public List<Decorator> getDecorators() { return Collections.unmodifiableList(decorators); }
    public List<Parameter> getParameters() { return Collections.unmodifiableList(parameters); }
    public List<Statement> getBody() { return Collections.unmodifiableList(body); }
    public boolean hasReturnType() { return returnType != null; }
    public Expression getReturnType() { return returnType; }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitFunctionDef(this);
    }
}