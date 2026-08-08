package semantic.error;

/**
 * The category of a {@link SemanticError}.
 *
 * <p>As of this phase, the checks described below are implemented by
 * {@code semantic.visitor.FlaskSemanticVisitor} and {@code
 * semantic.visitor.TemplateSemanticVisitor}. Each constant is tied to a
 * construct the AST already supports:
 *
 * <ul>
 *   <li>{@link #UNDEFINED_VARIABLE} — a Python name used with no matching
 *       {@code Symbol} anywhere in the enclosing scope chain (and not a
 *       recognized builtin or import).</li>
 *   <li>{@link #UNDEFINED_FUNCTION} — reserved for a future, more precise
 *       split of "resolves to something, but not a function" from plain
 *       undefined-ness; not produced yet — see {@link #UNDEFINED_VARIABLE}
 *       and {@link #ARGUMENT_MISMATCH}, which currently cover a call to an
 *       unresolved or non-function name.</li>
 *   <li>{@link #DUPLICATE_DEFINITION} — a name defined twice in the same
 *       scope: a re-declared variable, function, parameter, or (Jinja2)
 *       loop variable (see {@link semantic.scope.Scope#define}, which
 *       reports whether a name was new).</li>
 *   <li>{@link #INVALID_BREAK_OR_CONTINUE} — a {@code break}/{@code
 *       continue} found while no {@link semantic.scope.ScopeType#LOOP}
 *       scope is active (without crossing an enclosing function boundary).</li>
 *   <li>{@link #INVALID_RETURN} — a {@code return} found while no {@link
 *       semantic.scope.ScopeType#FUNCTION} scope is active.</li>
 *   <li>{@link #ARGUMENT_MISMATCH} — a call site whose argument count
 *       does not match the callee's known {@code FunctionSignature}.</li>
 *   <li>{@link #TYPE_MISMATCH} — reserved for a future type-checking
 *       pass.</li>
 *   <li>{@link #UNDEFINED_JINJA_VARIABLE} — a {@code {{ name }}}, {@code
 *       {% if name %}}, or {@code {% for x in name %}} referencing a
 *       template variable with no matching symbol and no corresponding
 *       Python-side {@code render_template(...)} binding.</li>
 *   <li>{@link #INVALID_ATTRIBUTE_ACCESS} — a {@code .attr} access whose
 *       base resolves to a macro ({@link semantic.symbol.SymbolType#FUNCTION})
 *       rather than a value.</li>
 *   <li>{@link #UNDEFINED_JINJA_MACRO} — a call expression ({@code
 *       name(...)}) whose callee does not resolve to a defined {@code
 *       JinjaMacroNode}.</li>
 *   <li>{@link #GENERAL} — a catch-all for a check that does not yet fit
 *       one of the categories above.</li>
 * </ul>
 */
public enum SemanticErrorType {
    UNDEFINED_VARIABLE,
    UNDEFINED_FUNCTION,
    DUPLICATE_DEFINITION,
    INVALID_BREAK_OR_CONTINUE,
    INVALID_RETURN,
    ARGUMENT_MISMATCH,
    TYPE_MISMATCH,
    UNDEFINED_JINJA_VARIABLE,
    INVALID_ATTRIBUTE_ACCESS,
    UNDEFINED_JINJA_MACRO,
    GENERAL
}
