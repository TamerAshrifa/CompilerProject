package semantic.scope;

/**
 * The kind of lexical scope a {@link Scope} represents.
 *
 * <p>A {@code Scope} is pushed for exactly two reasons: it is the root of
 * one of the project's two AST worlds, or it is introduced by a construct
 * that binds new names. That gives four scope kinds, one per required
 * category:
 *
 * <ul>
 *   <li>{@link #GLOBAL} — the root of the Python/Flask AST ({@code
 *       ProgramNode}), pushed exactly once per analysis.</li>
 *   <li>{@link #FUNCTION} — a Python {@code def} body, or a Jinja2
 *       {@code {% macro %}} body. Both are "named, parameterized, callable"
 *       constructs, so both push this same kind — the distinction is which
 *       AST produced it, not a difference in what the scope means.</li>
 *   <li>{@link #LOOP} — a Python {@code for}/{@code while} body. Reserved
 *       for the Python side; the Jinja2 {@code {% for %}} pushes {@link
 *       #JINJA} instead (see below), since it needs to sit inside the
 *       independent template scope tree rather than the Python one.</li>
 *   <li>{@link #JINJA} — the root of the template AST ({@code
 *       TemplateProgramNode}), and every name-binding construct within it
 *       ({@code {% for %}}, {@code {% macro %}}). The template AST is
 *       walked by its own visitor over an independent node hierarchy (see
 *       {@code template.ast.jinja.JinjaNode}'s "completely independent"
 *       design note), so rather than reusing {@code GLOBAL} for its root
 *       or {@code LOOP} for its for-loops and risking values that read as
 *       "this is a Python scope" in template-only analysis, every scope
 *       pushed while walking the template AST uses this one dedicated
 *       kind. {@code {% block %}} is deliberately excluded — real Jinja2
 *       blocks do not introduce a new variable scope (names from the
 *       surrounding template remain visible inside them), so the analyzer
 *       does not push one either; see {@code TemplateSemanticVisitor}.</li>
 * </ul>
 *
 * <p>This mapping is a deliberate, documented choice for this
 * infrastructure-only phase, not a claim that it is the only reasonable
 * one — a later phase could split {@code JINJA} further (e.g. a distinct
 * kind for macro scopes vs. loop scopes) if finer-grained checks need it.
 */
public enum ScopeType {

    /** The single module-level scope at the root of the Python AST. */
    GLOBAL,

    /** A Python function body, or a Jinja2 macro body. */
    FUNCTION,

    /** A Python {@code for} or {@code while} loop body. */
    LOOP,

    /** The template AST's root scope, and any name-binding construct within it. */
    JINJA
}
