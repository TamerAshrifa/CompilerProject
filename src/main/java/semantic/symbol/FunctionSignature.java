package semantic.symbol;

import java.util.List;

/**
 * Optional extra metadata attached to a {@link SymbolType#FUNCTION} {@link
 * Symbol}, recording enough about its parameter list for a call site to be
 * checked against it — specifically, "function call with an incorrect
 * number of arguments".
 *
 * <p>This is deliberately a separate, small value type rather than more
 * fields bolted directly onto {@link Symbol}: {@code Symbol} stays a
 * simple, uniform holder usable for all four symbol kinds, while the
 * richer, function-specific shape (how many parameters, how many of them
 * are required, whether the function accepts arbitrary extra arguments)
 * lives here and is {@code null} for every symbol that isn't a checkable
 * function.
 *
 * <p>Built from {@code flask.ast.nodes.helpers.Parameter} for Python
 * function definitions (where {@code hasDefault()} and {@code getKind()}
 * are known) and, more simply, from a Jinja2 {@code JinjaMacroNode}'s
 * plain {@code List<String>} parameters (which carry no default-value or
 * variadic information at all, so every macro signature built from one is
 * non-variadic with every parameter required).
 */
public class FunctionSignature {

    private final List<String> parameterNames;
    private final int requiredParameterCount;
    private final boolean variadic;

    /**
     * @param parameterNames         every parameter's name, in declaration order
     * @param requiredParameterCount how many of them have no default value
     *                               (must be provided by every call)
     * @param variadic               whether the function also accepts a
     *                               {@code *args} and/or {@code **kwargs}
     *                               parameter, making an upper bound on the
     *                               argument count meaningless
     */
    public FunctionSignature(List<String> parameterNames, int requiredParameterCount, boolean variadic) {
        this.parameterNames = List.copyOf(parameterNames);
        this.requiredParameterCount = requiredParameterCount;
        this.variadic = variadic;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    /** The total number of declared parameters (the maximum a non-variadic call may pass). */
    public int getParameterCount() {
        return parameterNames.size();
    }

    /** How many parameters have no default value and so must be supplied by every call. */
    public int getRequiredParameterCount() {
        return requiredParameterCount;
    }

    /** Whether the function accepts extra positional/keyword arguments beyond its declared list. */
    public boolean isVariadic() {
        return variadic;
    }

    @Override
    public String toString() {
        return "(" + String.join(", ", parameterNames) + ")"
            + (variadic ? " [variadic]" : "");
    }
}
