package flask.ast.nodes.helpers;

import flask.ast.nodes.Expression;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Decorator {

    private final Expression name;
    private final List<Expression> args;
    private final Map<String, Expression> kwargs;

    public Decorator(Expression name, List<Expression> args, Map<String, Expression> kwargs) {
        this.name = name;
        this.args = new ArrayList<>(args);
        this.kwargs = new LinkedHashMap<>(kwargs);
    }

    public Expression getName() {
        return name;
    }

    public List<Expression> getArgs() {
        return Collections.unmodifiableList(args);
    }

    public Map<String, Expression> getKwargs() {
        return Collections.unmodifiableMap(kwargs);
    }
}