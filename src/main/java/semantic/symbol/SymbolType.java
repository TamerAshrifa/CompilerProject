package semantic.symbol;

/**
 * The kind of program entity a {@link Symbol} represents.
 *
 * <p>Every name the semantic analyzer records in a {@code Scope} is tagged
 * with exactly one of these four kinds. The set is intentionally small and
 * flat (no subtyping) because, at this stage, "what kind of thing is this
 * name" is all the infrastructure needs to answer — richer information
 * (a resolved type, a "used" flag, whether it was ever reassigned, ...)
 * belongs on {@link Symbol} itself once later phases need it, not as new
 * enum constants here.
 *
 * <p>Both AST hierarchies feed the same four kinds. On the Python (Flask)
 * side: {@code FUNCTION} comes from {@code FunctionDefNode}, {@code
 * PARAMETER} from its {@code Parameter} list, {@code LOOP_VARIABLE} from a
 * {@code ForStatementNode} target, and {@code VARIABLE} from an {@code
 * AssignmentNode} target. On the Jinja2 (template) side: {@code FUNCTION}
 * comes from {@code JinjaMacroNode} (a macro is, semantically, a callable
 * with parameters — the same shape as a Python function), {@code
 * PARAMETER} from its parameter list, and {@code LOOP_VARIABLE} from a
 * {@code JinjaForNode}'s loop variable. Jinja has no declaration syntax
 * that binds a plain {@code VARIABLE} yet ({@code {% set %}} is not part
 * of the current grammar), so that kind is populated by the Python side
 * only until such a construct exists.
 */
public enum SymbolType {

    /** A name bound by a plain assignment (Python {@code x = ...}). */
    VARIABLE,

    /**
     * A callable's own name — a Python {@code def} or a Jinja2
     * {@code {% macro %}}. Recorded in the scope the callable is declared
     * in, not the scope it introduces for its own body.
     */
    FUNCTION,

    /**
     * A name bound by a callable's parameter list — a Python function
     * parameter or a Jinja2 macro parameter. Recorded inside the callable's
     * own scope.
     */
    PARAMETER,

    /**
     * A name bound by a loop header — a Python {@code for} target or a
     * Jinja2 {@code {% for %}} loop variable. Recorded inside the loop's
     * own scope, not the scope the loop appears in.
     */
    LOOP_VARIABLE
}
