package semantic.scope;

import semantic.symbol.FunctionSignature;
import semantic.symbol.Symbol;
import semantic.symbol.SymbolType;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks the scopes active while a semantic visitor walks an AST, and
 * remembers every scope ever created so the whole structure can be
 * inspected after the walk finishes.
 *
 * <p>A single {@code SymbolTable} is shared across both AST worlds this
 * compiler has: the Python/Flask visitor and the Jinja2/template visitor
 * each push their own root scope ({@link ScopeType#GLOBAL} and {@link
 * ScopeType#JINJA} respectively) into the same table. Sharing one table
 * rather than keeping two is what allows a later phase to check names that
 * cross from one AST into the other — e.g. whether a variable a template
 * references was actually passed in from the Python side — without this
 * class needing to change. Nothing about that cross-checking is
 * implemented yet; this class only keeps the door open for it.
 *
 * <p><b>Scope stack.</b> {@link #enterScope(ScopeType)} pushes a new scope
 * whose parent is whatever scope is currently active (or {@code null},
 * starting a new root, if none is), and {@link #exitScope()} pops it back
 * off. Callers are expected to pair every {@code enterScope} with exactly
 * one {@code exitScope}, typically in a {@code try}/{@code finally} block
 * around the traversal of that construct's children, so that an
 * unexpected exception mid-traversal cannot leave the stack unbalanced.
 *
 * <p><b>History.</b> Every scope ever pushed is also kept in {@link
 * #getAllScopes()} (and, transitively, {@link #getAllSymbols()}), even
 * after it has been popped off the active stack — a scope that has already
 * been exited is still a legitimate part of the finished symbol table, it
 * is simply no longer where new definitions are added.
 */
public class SymbolTable {

    private final Deque<Scope> activeScopes = new ArrayDeque<>();
    private final List<Scope> allScopes = new ArrayList<>();

    /**
     * Pushes a new scope of the given kind, nested inside whatever scope is
     * currently active (or as a new root, if none is active).
     *
     * @return the newly pushed scope, in case the caller wants to hold on
     *         to it directly rather than going back through {@link #currentScope()}
     */
    public Scope enterScope(ScopeType type) {
        Scope parent = activeScopes.peek();
        Scope scope = new Scope(type, parent);
        activeScopes.push(scope);
        allScopes.add(scope);
        return scope;
    }

    /**
     * Pops the current scope off the active stack. The scope remains
     * available afterward through {@link #getAllScopes()}.
     *
     * @return the scope that was just exited
     * @throws IllegalStateException if no scope is currently active
     */
    public Scope exitScope() {
        Scope scope = activeScopes.poll();
        if (scope == null) {
            throw new IllegalStateException("exitScope() called with no active scope to exit.");
        }
        return scope;
    }

    /** The innermost currently-active scope, or {@code null} if none is active. */
    public Scope currentScope() {
        return activeScopes.peek();
    }

    /** How many scopes are currently active/nested (0 if none). */
    public int getActiveDepth() {
        return activeScopes.size();
    }

    /**
     * Defines {@code symbol} in the current scope.
     *
     * @return {@code true} if the name was new to the current scope, {@code
     *         false} if it replaced an existing symbol there — see {@link
     *         Scope#define(Symbol)}
     * @throws IllegalStateException if no scope is currently active
     */
    public boolean define(Symbol symbol) {
        Scope scope = currentScope();
        if (scope == null) {
            throw new IllegalStateException(
                "Cannot define symbol '" + symbol.getName() + "': no active scope. "
                    + "Call enterScope(...) before defining symbols.");
        }
        return scope.define(symbol);
    }

    /**
     * Resolves {@code name} starting from the current scope and walking
     * outward through its enclosing scopes.
     *
     * @return the resolved symbol, or {@code null} if unresolved or if no
     *         scope is currently active
     */
    public Symbol resolve(String name) {
        Scope scope = currentScope();
        return (scope == null) ? null : scope.resolve(name);
    }

    /** Every scope created over the lifetime of this table, in creation order. */
    public List<Scope> getAllScopes() {
        return Collections.unmodifiableList(allScopes);
    }

    /** Every symbol defined in any scope this table has ever held. */
    public List<Symbol> getAllSymbols() {
        List<Symbol> all = new ArrayList<>();
        for (Scope scope : allScopes) {
            all.addAll(scope.getSymbols());
        }
        return all;
    }

    /* ======================================================================
     * Structured symbol table printing.
     *
     * A pure extension of the class above: it adds a new capability
     * (printing every scope this table has ever held, nested under its
     * parent, with the symbols declared in it) without touching any
     * existing field or method. Nothing above this point was changed to
     * support it.
     * ====================================================================== */

    /**
     * Prints every scope this table has ever held (see {@link #getAllScopes()})
     * as an indented outline: each root scope (the Python side's {@link
     * ScopeType#GLOBAL} scope, and/or the template side's {@link
     * ScopeType#JINJA} root — see this class's own documentation on why
     * there can be more than one root) heads its own block, with every
     * scope nested inside it indented one level further than its parent,
     * for example:
     *
     * <pre>
     * Global Scope:
     *   - products : Variable
     *   - getProduct(id) : Function
     *   Scope (function getProduct):
     *     - id : Parameter
     * </pre>
     *
     * <p>Unlike {@link flask.ast.nodes.ASTNode#print(String)}/{@link
     * template.ast.jinja.JinjaNode#print(String)}, this uses plain
     * two-space indentation with a leading {@code "- "} on each symbol line
     * rather than the {@code printer.TreePrinter} box-drawing style used
     * for AST trees: a symbol table reads more like a nested outline of
     * declarations than a tree of node attributes, so it intentionally
     * looks different from an AST dump at a glance.
     *
     * <p><b>Where a scope's label comes from.</b> A {@link Scope} itself
     * carries no name — only its {@link ScopeType} — so a scope's
     * descriptive label ({@code "function getProduct"}, {@code "for item"}, ...)
     * is reconstructed here from context, without needing any change to
     * {@link Scope} or {@link Symbol}:
     * <ul>
     *   <li>A {@link ScopeType#FUNCTION} scope (Python) or a {@link
     *       ScopeType#JINJA} scope that is a macro body (see below) is
     *       matched to the {@link semantic.symbol.SymbolType#FUNCTION}
     *       symbol of the same name that must have just been defined in
     *       its parent scope — both {@code FlaskSemanticVisitor#visitFunctionDef}
     *       and {@code TemplateSemanticVisitor#visitJinjaMacro} always
     *       define that symbol immediately before pushing the new scope, so
     *       the Nth such symbol in a parent and the Nth such child scope of
     *       that parent name the same callable, in declaration order.</li>
     *   <li>A {@link ScopeType#JINJA} scope is distinguished as a {@code
     *       {% for %}} body rather than a macro body by checking for the
     *       implicit {@code "loop"} {@link semantic.symbol.SymbolType#LOOP_VARIABLE}
     *       that {@code TemplateSemanticVisitor#visitJinjaFor} always
     *       defines directly inside every for-loop's own scope — a
     *       reliable marker no macro scope ever has.</li>
     *   <li>A {@link ScopeType#LOOP} scope (Python) or a {@code {% for %}}'s
     *       {@link ScopeType#JINJA} scope shows its own loop variable
     *       name(s) directly: those are defined inside the loop's own
     *       scope, so no cross-scope correlation is needed for them.</li>
     * </ul>
     * A scope that cannot be matched this way (which should only happen for
     * a same-named redeclaration — see {@link Scope#define}, which already
     * reports that as a {@link semantic.error.SemanticErrorType#DUPLICATE_DEFINITION}
     * error elsewhere) falls back to a plain, un-named label instead of
     * guessing.
     */
    public void printSymbolTable() {
        if (allScopes.isEmpty()) {
            System.out.println("(empty symbol table - no scopes were ever entered)");
            return;
        }

        Map<Scope, List<Scope>> childrenOf = groupChildrenByParent();
        Map<Scope, String> ownerNames = correlateCallableOwnerNames(childrenOf);

        for (Scope scope : allScopes) {
            if (scope.isRoot()) {
                printScope(scope, childrenOf, ownerNames, "");
            }
        }
    }

    /** Every scope, keyed by its parent - the downward links {@link Scope} itself does not keep. */
    private Map<Scope, List<Scope>> groupChildrenByParent() {
        Map<Scope, List<Scope>> childrenOf = new LinkedHashMap<>();
        for (Scope scope : allScopes) {
            childrenOf.put(scope, new ArrayList<>());
        }
        for (Scope scope : allScopes) {
            if (!scope.isRoot()) {
                childrenOf.get(scope.getParent()).add(scope);
            }
        }
        return childrenOf;
    }

    /**
     * Matches every {@code FUNCTION}-scoped or macro-shaped {@code JINJA}-scoped
     * child scope to the name of the callable it belongs to, using the
     * ordered correlation described in {@link #printSymbolTable()}'s
     * documentation.
     */
    private Map<Scope, String> correlateCallableOwnerNames(Map<Scope, List<Scope>> childrenOf) {
        Map<Scope, String> owners = new LinkedHashMap<>();
        for (Map.Entry<Scope, List<Scope>> entry : childrenOf.entrySet()) {
            Scope parent = entry.getKey();

            List<String> functionSymbolNames = new ArrayList<>();
            for (Symbol symbol : parent.getSymbols()) {
                if (symbol.getType() == SymbolType.FUNCTION) {
                    functionSymbolNames.add(symbol.getName());
                }
            }

            List<Scope> callableChildren = new ArrayList<>();
            for (Scope child : entry.getValue()) {
                if (child.getType() == ScopeType.FUNCTION
                        || (child.getType() == ScopeType.JINJA && !isJinjaForLoopScope(child))) {
                    callableChildren.add(child);
                }
            }

            int matchable = Math.min(functionSymbolNames.size(), callableChildren.size());
            for (int i = 0; i < matchable; i++) {
                owners.put(callableChildren.get(i), functionSymbolNames.get(i));
            }
        }
        return owners;
    }

    /** Whether {@code scope} is a Jinja2 {@code {% for %}} body - see {@link #printSymbolTable()}. */
    private boolean isJinjaForLoopScope(Scope scope) {
        for (Symbol symbol : scope.getSymbols()) {
            if (symbol.getType() == SymbolType.LOOP_VARIABLE && "loop".equals(symbol.getName())) {
                return true;
            }
        }
        return false;
    }

    /** Every loop-variable name bound directly in {@code scope}, excluding {@code exclude} (e.g. Jinja's implicit {@code "loop"}). */
    private String loopVariableNames(Scope scope, String exclude) {
        List<String> names = new ArrayList<>();
        for (Symbol symbol : scope.getSymbols()) {
            if (symbol.getType() == SymbolType.LOOP_VARIABLE
                    && (exclude == null || !symbol.getName().equals(exclude))) {
                names.add(symbol.getName());
            }
        }
        return names.isEmpty() ? null : String.join(", ", names);
    }

    /** This scope's descriptive heading, e.g. {@code "Global Scope"} or {@code "Scope (function getProduct)"}. */
    private String scopeLabel(Scope scope, Map<Scope, String> ownerNames) {
        if (scope.isRoot()) {
            return (scope.getType() == ScopeType.GLOBAL) ? "Global Scope" : "Template Scope";
        }
        switch (scope.getType()) {
            case FUNCTION: {
                String owner = ownerNames.get(scope);
                return "Scope (function" + (owner != null ? " " + owner : "") + ")";
            }
            case LOOP: {
                String vars = loopVariableNames(scope, null);
                return "Scope (" + (vars != null ? "for " + vars : "loop") + ")";
            }
            case JINJA: {
                if (isJinjaForLoopScope(scope)) {
                    String vars = loopVariableNames(scope, "loop");
                    return "Scope (" + (vars != null ? "for " + vars : "for-loop") + ")";
                }
                String owner = ownerNames.get(scope);
                return "Scope (macro" + (owner != null ? " " + owner : "") + ")";
            }
            default:
                return "Scope";
        }
    }

    /** One symbol line, e.g. {@code "products : Variable"} or {@code "getProduct(id, greeting?) : Function"}. */
    private String describeSymbol(Symbol symbol) {
        String kind = humanReadableKind(symbol.getType());
        if (symbol.getType() == SymbolType.FUNCTION && symbol.getSignature() != null) {
            return symbol.getName() + "(" + formatParameters(symbol.getSignature()) + ") : " + kind;
        }
        return symbol.getName() + " : " + kind;
    }

    /**
     * A function/macro signature's parameters as a short, readable list:
     * required parameters plain, parameters beyond {@link
     * FunctionSignature#getRequiredParameterCount()} marked with a
     * trailing {@code ?} (Python's syntax already guarantees defaulted
     * parameters are declared after every required one, so a straight
     * positional split is safe here), and a trailing {@code ...} appended
     * when {@link FunctionSignature#isVariadic()}.
     */
    private String formatParameters(FunctionSignature signature) {
        List<String> names = signature.getParameterNames();
        int required = signature.getRequiredParameterCount();
        List<String> parts = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            parts.add(i < required ? names.get(i) : names.get(i) + "?");
        }
        if (signature.isVariadic()) {
            parts.add("...");
        }
        return String.join(", ", parts);
    }

    /**
     * A {@link SymbolType} does not track a real, inferred data type (this
     * compiler's semantic analysis does not do type inference - see {@link
     * SymbolType}'s own documentation) - what {@link Symbol#getType()}
     * actually records is which of the four symbol <em>kinds</em> a name
     * is. This turns that enum constant into the human-readable label used
     * after the {@code " : "} in {@link #describeSymbol}, e.g. {@code
     * SymbolType.LOOP_VARIABLE} to {@code "Loop Variable"}.
     */
    private String humanReadableKind(SymbolType type) {
        switch (type) {
            case VARIABLE:
                return "Variable";
            case FUNCTION:
                return "Function";
            case PARAMETER:
                return "Parameter";
            case LOOP_VARIABLE:
                return "Loop Variable";
            default:
                return type.toString();
        }
    }

    /** Recursively prints one scope, its symbols, and then its own nested child scopes. */
    private void printScope(Scope scope, Map<Scope, List<Scope>> childrenOf,
                             Map<Scope, String> ownerNames, String indent) {
        System.out.println(indent + scopeLabel(scope, ownerNames) + ":");

        String innerIndent = indent + "  ";
        for (Symbol symbol : scope.getSymbols()) {
            System.out.println(innerIndent + "- " + describeSymbol(symbol));
        }
        for (Scope child : childrenOf.get(scope)) {
            printScope(child, childrenOf, ownerNames, innerIndent);
        }
    }
}
