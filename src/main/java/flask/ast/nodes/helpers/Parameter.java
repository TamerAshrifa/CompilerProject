package flask.ast.nodes.helpers;

import flask.ast.nodes.Expression;

public class Parameter {

    /** Whether this is a normal parameter, a {@code *args}, or a {@code **kwargs}. */
    public enum Kind { NORMAL, VAR_ARGS, KW_ARGS }

    private final String name;
    private final Expression typeHint;
    private final Expression defaultValue;
    private final Kind kind;

    public Parameter(String name, Expression typeHint, Expression defaultValue) {
        this(name, typeHint, defaultValue, Kind.NORMAL);
    }

    public Parameter(String name, Expression typeHint, Expression defaultValue, Kind kind) {
        this.name = name;
        this.typeHint = typeHint;
        this.defaultValue = defaultValue;
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public boolean hasTypeHint() {
        return typeHint != null;
    }

    public Expression getTypeHint() {
        return typeHint;
    }

    public boolean hasDefault() {
        return defaultValue != null;
    }

    public Expression getDefaultValue() {
        return defaultValue;
    }

    public Kind getKind() {
        return kind;
    }

    public boolean isVarArgs() {
        return kind == Kind.VAR_ARGS;
    }

    public boolean isKwArgs() {
        return kind == Kind.KW_ARGS;
    }
}