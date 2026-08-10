package semantic.symbol;

import semantic.scope.ScopeType;
import java.util.Objects;

/**
 * A single declared name recorded in a {@code Scope}: a variable, a
 * function/macro, a parameter, or a loop variable.
 *
 * <p>Symbol is a plain, immutable data holder — it does not know how to
 * resolve itself, does not belong to any particular {@code Scope} instance
 * (avoiding a parent/child reference cycle between the two classes), and
 * carries no behavior beyond simple getters and a debug-friendly {@link
 * #toString()}. This mirrors the flat, dependency-light shape of the
 * existing helper types in {@code flask.ast.nodes.helpers} (e.g. {@code
 * Parameter}), rather than the polymorphic AST node hierarchies.
 *
 * <p>Symbol deliberately stays independent of both {@code flask.ast.nodes.ASTNode}
 * and the {@code template.ast.*} node hierarchies: it is populated by
 * reading the handful of fields it needs (name, source position, and the
 * declaring node's {@code getNodeName()}) at the call site in whichever
 * visitor discovered the declaration, rather than holding a reference to
 * the node itself. That keeps this class usable identically from the
 * Python-side and the template-side visitors even though those two node
 * hierarchies share no common supertype.
 * <p>Since the previous phase, Symbol also optionally carries a {@link
 * FunctionSignature} — {@code null} for every symbol except a checkable
 * {@link SymbolType#FUNCTION} — added via a new constructor overload
 * rather than changing the original one, which still behaves exactly as
 * before (and now simply delegates to the new overload with a {@code null}
 * signature).
 */
public class Symbol {

    private final String name;
    private final SymbolType type;
    private final int line;
    private final int column;
    private final String declaringNodeName;
    private final ScopeType declaringScopeType;
    private final FunctionSignature signature;

    /**
     * @param name               the declared identifier, e.g. {@code "items"}
     * @param type               what kind of symbol this is
     * @param line               source line of the declaration (0 if unknown)
     * @param column             source column of the declaration (0 if unknown)
     * @param declaringNodeName  {@code getNodeName()} of the AST node that
     *                           introduced this symbol (e.g. {@code "AssignmentNode"},
     *                           {@code "JinjaForNode"}) — the "node information"
     *                           every symbol and error in this framework carries
     * @param declaringScopeType the kind of scope this symbol was defined in
     */
    public Symbol(String name, SymbolType type, int line, int column,
                  String declaringNodeName, ScopeType declaringScopeType) {
        this(name, type, line, column, declaringNodeName, declaringScopeType, null);
    }

    /**
     * Full constructor, additionally recording a {@link FunctionSignature}
     * for a callable symbol whose parameter list is statically known.
     *
     * @param signature the callable's parameter-list metadata, or {@code
     *                  null} if this symbol isn't a checkable function (or
     *                  its signature isn't known)
     */
    public Symbol(String name, SymbolType type, int line, int column,
                  String declaringNodeName, ScopeType declaringScopeType, FunctionSignature signature) {
        this.name = Objects.requireNonNull(name, "name");
        this.type = Objects.requireNonNull(type, "type");
        this.line = line;
        this.column = column;
        this.declaringNodeName = declaringNodeName;
        this.declaringScopeType = declaringScopeType;
        this.signature = signature;
    }

    public String getName() {
        return name;
    }

    public SymbolType getType() {
        return type;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    /** The {@code getNodeName()} of the AST node that declared this symbol. */
    public String getDeclaringNodeName() {
        return declaringNodeName;
    }

    /** The kind of scope this symbol lives in (Global, Function, Loop, or Jinja). */
    public ScopeType getDeclaringScopeType() {
        return declaringScopeType;
    }

    /**
     * This function/macro's parameter-list metadata, or {@code null} if
     * this isn't a checkable function symbol (e.g. it's a variable, or a
     * function whose real signature isn't known — an imported name, for
     * instance).
     */
    public FunctionSignature getSignature() {
        return signature;
    }

    @Override
    public String toString() {
        return type + " '" + name + "' (declared by " + declaringNodeName
            + " at line " + line + ", in " + declaringScopeType + " scope)";
    }
}
