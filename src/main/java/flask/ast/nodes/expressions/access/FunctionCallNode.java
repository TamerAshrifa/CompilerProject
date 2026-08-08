package flask.ast.nodes.expressions.access;

import flask.ast.nodes.Expression;
import flask.ast.visitor.ASTVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FunctionCallNode extends Expression {

    private final Expression callee;
    private final List<Expression> args;
    private final Map<String, Expression> kwargs;
    private final List<Expression> starArgs;
    private final List<Expression> kwargsSpread;

    public FunctionCallNode(Expression callee, List<Expression> args, Map<String, Expression> kwargs,
                             int line, int column) {
        this(callee, args, kwargs, List.of(), List.of(), line, column);
    }

    /** Convenience constructor for hand-built ASTs with no real source position (e.g. tests). */
    public FunctionCallNode(Expression callee, List<Expression> args, Map<String, Expression> kwargs) {
        this(callee, args, kwargs, List.of(), List.of(), 0, 0);
    }

    /**
     * Full constructor supporting Python's *args / **kwargs call-site expansion.
     *
     * @param callee       the callee expression (e.g. identifier render_template)
     * @param args         resolved positional arguments (func(a, b))
     * @param kwargs       resolved keyword arguments (func(name=value))
     * @param starArgs     expressions passed via single-star expansion (func(*items))
     * @param kwargsSpread expressions passed via double-star expansion (func(**extra))
     * @param line         source line of the call
     * @param column       source column of the call
     */
    public FunctionCallNode(Expression callee, List<Expression> args, Map<String, Expression> kwargs,
                             List<Expression> starArgs, List<Expression> kwargsSpread, int line, int column) {
        super(line, column);
        this.callee = callee;
        this.args = new ArrayList<>(args);
        this.kwargs = new LinkedHashMap<>(kwargs);
        this.starArgs = new ArrayList<>(starArgs);
        this.kwargsSpread = new ArrayList<>(kwargsSpread);
    }

    /** Convenience constructor for hand-built ASTs with no real source position (e.g. tests). */
    public FunctionCallNode(Expression callee, List<Expression> args, Map<String, Expression> kwargs,
                             List<Expression> starArgs, List<Expression> kwargsSpread) {
        this(callee, args, kwargs, starArgs, kwargsSpread, 0, 0);
    }

    public Expression getCallee() {
        return callee;
    }

    public Expression getFunction() {
        return callee;
    }

    public List<Expression> getArgs() {
        return Collections.unmodifiableList(args);
    }

    public Map<String, Expression> getKwargs() {
        return Collections.unmodifiableMap(kwargs);
    }

    /**
     * Expressions expanded positionally via {@code *expr}, e.g. {@code func(*items)}.
     */
    public List<Expression> getStarArgs() {
        return Collections.unmodifiableList(starArgs);
    }

    /**
     * Expressions expanded as keyword arguments via {@code **expr}, e.g.
     * {@code render_template("t.html", **context)}.
     */
    public List<Expression> getKwargsSpread() {
        return Collections.unmodifiableList(kwargsSpread);
    }

    public boolean hasKwargsSpread() {
        return !kwargsSpread.isEmpty();
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitFunctionCall(this);
    }
}