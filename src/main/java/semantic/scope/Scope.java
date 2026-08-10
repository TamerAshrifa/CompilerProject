package semantic.scope;

import semantic.symbol.Symbol;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One lexical scope: a named set of symbols plus a link to the enclosing
 * scope it is nested in.
 *
 * <p>Scopes form a tree, not a single chain: a {@code SymbolTable} may hold
 * several root scopes (the Python side's {@link ScopeType#GLOBAL} and the
 * template side's {@link ScopeType#JINJA} root are independent of each
 * other — see that class's documentation), and any scope may have several
 * children (e.g. two sibling functions both nested in the same module).
 * Each {@code Scope} instance only ever needs to see upward along its own
 * {@link #getParent()} chain, so the tree shape falls out naturally without
 * this class needing to track its own children.
 *
 * <p>Symbols are stored in a {@link LinkedHashMap} so that {@link
 * #getSymbols()} returns them in declaration order, which reads far more
 * naturally in any debug/summary output than an arbitrary hash order would.
 *
 * <p>This class intentionally does <em>not</em> decide what counts as an
 * error. {@link #define(Symbol)} always succeeds and returns whether the
 * name was new to <em>this</em> scope; a later semantic-checking phase can
 * use that boolean (or {@link #isDefinedLocally(String)} beforehand) to
 * decide whether a redeclaration should be reported — this phase only
 * builds the structure that makes such a check possible.
 */
public class Scope {

    private final ScopeType type;
    private final Scope parent;
    private final int depth;
    private final Map<String, Symbol> symbols = new LinkedHashMap<>();

    /**
     * @param type   the kind of scope this is
     * @param parent the enclosing scope, or {@code null} if this is a root
     *               scope (the start of a new AST world)
     */
    public Scope(ScopeType type, Scope parent) {
        this.type = type;
        this.parent = parent;
        this.depth = (parent == null) ? 0 : parent.depth + 1;
    }

    public ScopeType getType() {
        return type;
    }

    /** The enclosing scope, or {@code null} if this is a root scope. */
    public Scope getParent() {
        return parent;
    }

    /** How many scopes above this one, up to (and not including) its root. 0 for a root scope. */
    public int getDepth() {
        return depth;
    }

    /** Whether this scope has no parent, i.e. is the root of its AST world. */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * Defines (or redefines) {@code symbol} in this scope only.
     *
     * @return {@code true} if this name was not already present in this
     *         scope, {@code false} if it was (and has now been replaced).
     *         Neither outcome is treated as an error here — see the class
     *         documentation.
     */
    public boolean define(Symbol symbol) {
        boolean isNewInThisScope = !symbols.containsKey(symbol.getName());
        symbols.put(symbol.getName(), symbol);
        return isNewInThisScope;
    }

    /** Whether {@code name} is defined directly in this scope (not its ancestors). */
    public boolean isDefinedLocally(String name) {
        return symbols.containsKey(name);
    }

    /** The symbol named {@code name} defined directly in this scope, or {@code null}. */
    public Symbol resolveLocally(String name) {
        return symbols.get(name);
    }

    /**
     * Looks up {@code name} in this scope, then each enclosing scope in
     * turn, returning the nearest match.
     *
     * @return the resolved symbol, or {@code null} if {@code name} is not
     *         defined anywhere from this scope up to its root
     */
    public Symbol resolve(String name) {
        for (Scope scope = this; scope != null; scope = scope.parent) {
            Symbol found = scope.symbols.get(name);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /** All symbols defined directly in this scope, in declaration order. */
    public Collection<Symbol> getSymbols() {
        return Collections.unmodifiableCollection(symbols.values());
    }

    @Override
    public String toString() {
        return "Scope{type=" + type + ", depth=" + depth + ", symbols=" + symbols.keySet() + "}";
    }
}
