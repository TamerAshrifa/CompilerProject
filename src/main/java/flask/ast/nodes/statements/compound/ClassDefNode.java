package flask.ast.nodes.statements.compound;

import flask.ast.nodes.Expression;
import flask.ast.nodes.Statement;
import flask.ast.nodes.helpers.Decorator;
import flask.ast.visitor.ASTVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassDefNode extends Statement {

    private final String name;
    private final List<Decorator> decorators;
    private final List<Expression> bases;
    private final List<Statement> body;

    public ClassDefNode(String name, List<Decorator> decorators, List<Expression> bases, List<Statement> body,
                        int line, int column) {
        super(line, column);
        this.name = name;
        this.decorators = new ArrayList<>(decorators);
        this.bases = new ArrayList<>(bases);
        this.body = new ArrayList<>(body);
    }

    public String getName() { return name; }
    public List<Decorator> getDecorators() { return Collections.unmodifiableList(decorators); }
    public List<Expression> getBases() { return Collections.unmodifiableList(bases); }
    public List<Statement> getBody() { return Collections.unmodifiableList(body); }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitClassDef(this);
    }
}