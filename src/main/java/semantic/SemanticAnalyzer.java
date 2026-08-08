package semantic;

import flask.ast.nodes.statements.ProgramNode;
import template.ast.TemplateProgramNode;
import semantic.error.SemanticError;
import semantic.scope.Scope;
import semantic.scope.SymbolTable;
import semantic.visitor.FlaskSemanticVisitor;
import semantic.visitor.TemplateSemanticVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entry point for semantic analysis: walks the AST(s) produced by the
 * existing builders and populates a {@link SymbolTable} describing every
 * scope and symbol found, without rebuilding or modifying either tree.
 *
 * <p>This class is a thin facade over two independent, visitor-based
 * traversals — it holds no traversal logic of its own. Rather than one
 * visitor implementing both {@code flask.ast.visitor.ASTVisitor} and
 * {@code template.visitor.TemplateVisitor} (two large interfaces, ~30 and
 * ~50 methods respectively, over two node hierarchies that share no common
 * supertype), analysis is delegated to two focused visitors:
 *
 * <ul>
 *   <li>{@link FlaskSemanticVisitor} walks the Python/Flask AST rooted at
 *       a {@code ProgramNode}.</li>
 *   <li>{@link TemplateSemanticVisitor} walks the template AST rooted at a
 *       {@code TemplateProgramNode} (HTML, Jinja2, and CSS together).</li>
 * </ul>
 *
 * <p>Both visitors share the same {@link SymbolTable} and the same error
 * list, constructed once here. Sharing one table (rather than giving each
 * visitor its own) is what keeps the door open for a later phase to check
 * names that cross from one AST into the other — e.g. whether a template
 * variable was actually passed in from the Python side — without this
 * class's shape needing to change; nothing about that cross-checking is
 * implemented yet.
 *
 * <p>Either {@link #analyze(ProgramNode)} or {@link #analyze(TemplateProgramNode)}
 * may be called independently, in either order, any number of times; each
 * pushes and fully pops its own root scope, so calling one has no effect
 * on the other's scope structure. Both return {@code this} so a caller can
 * chain a full pipeline in one expression:
 * {@code new SemanticAnalyzer().analyze(pythonAst).analyze(templateAst)}.
 *
 * <p>As of this phase, both visitors report real semantic errors — see
 * their class-level documentation for the full list — collected into one
 * shared list via {@link #getErrors()}; neither stops at the first error.
 *
 * <p>{@link #analyze(ProgramNode)} additionally passes {@link
 * FlaskSemanticVisitor#getRenderContextVariableNames()} (the
 * keyword-argument names of every {@code render_template(...)} call found)
 * into the template visitor after each Python-side walk, so that a
 * template variable legitimately supplied from the Python side — rather
 * than bound anywhere in the Jinja2 AST itself — is not flagged as
 * undefined. This is why analyzing the Python AST before the template AST
 * (the order {@code Main} already uses) gives the more complete result;
 * analyzing the template first still works, just without that context yet.
 */
public class SemanticAnalyzer {

    private final SymbolTable symbolTable;
    private final List<SemanticError> errors;
    private final FlaskSemanticVisitor flaskVisitor;
    private final TemplateSemanticVisitor templateVisitor;

    public SemanticAnalyzer() {
        this.symbolTable = new SymbolTable();
        this.errors = new ArrayList<>();
        this.flaskVisitor = new FlaskSemanticVisitor(symbolTable, errors);
        this.templateVisitor = new TemplateSemanticVisitor(symbolTable, errors);
    }

    /**
     * Walks the Python/Flask AST, if given, then refreshes the template
     * visitor's known external context-variable names from whatever
     * {@code render_template(...)} calls were found — see the class
     * documentation. Does nothing if {@code program} is {@code null}.
     *
     * @return this analyzer, for chaining
     */
    public SemanticAnalyzer analyze(ProgramNode program) {
        if (program != null) {
            program.accept(flaskVisitor);
            templateVisitor.setExternalContextVariableNames(flaskVisitor.getRenderContextVariableNames());
        }
        return this;
    }

    /**
     * Walks the template AST, if given. Does nothing if {@code template}
     * is {@code null}.
     *
     * @return this analyzer, for chaining
     */
    public SemanticAnalyzer analyze(TemplateProgramNode template) {
        if (template != null) {
            template.accept(templateVisitor);
        }
        return this;
    }

    /** The symbol table populated by every {@code analyze(...)} call so far. */
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    /** Every semantic error reported so far, across both AST worlds, in discovery order. */
    public List<SemanticError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /** A short, human-readable report of what analysis has found so far. */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Semantic Analysis Summary:\n");
        sb.append("  Scopes entered: ").append(symbolTable.getAllScopes().size()).append("\n");
        sb.append("  Symbols defined: ").append(symbolTable.getAllSymbols().size()).append("\n");
        sb.append("  Errors reported: ").append(errors.size()).append("\n");

        for (Scope scope : symbolTable.getAllScopes()) {
            sb.append("    - ").append(scope).append("\n");
        }
        for (SemanticError error : errors) {
            sb.append("    ! ").append(error).append("\n");
        }

        return sb.toString();
    }
}
