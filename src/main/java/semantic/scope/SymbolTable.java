package semantic.scope;

import semantic.symbol.Symbol;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

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
}
