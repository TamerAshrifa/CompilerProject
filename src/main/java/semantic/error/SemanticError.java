package semantic.error;

import java.util.Objects;

/**
 * A single semantic-analysis diagnostic.
 *
 * <p>As required by the current phase, every {@code SemanticError} carries
 * exactly four things: a {@link #getType() type}, a {@link #getMessage()
 * descriptive message}, a {@link #getLine() line number}, and {@link
 * #getNodeName() node information} — the {@code getNodeName()} of whatever
 * AST node the check was inspecting (e.g. {@code "AssignmentNode"}, {@code
 * "JinjaForNode"}), giving a reader both <em>where</em> in the source and
 * <em>what kind of construct</em> triggered the diagnostic. {@link
 * #getColumn()} is included too, since every node in both AST hierarchies
 * already carries one alongside its line number at no extra cost.
 *
 * <p>Like {@link semantic.symbol.Symbol}, this class stays independent of
 * both AST hierarchies: it holds the extracted position and node-name
 * strings rather than a reference to the node itself, so it works
 * identically regardless of which of the two unrelated node hierarchies
 * (Python or template) produced it.
 *
 * <p>No code path constructs one of these yet — see the class-level
 * documentation on {@link SemanticErrorType} — but the type is complete
 * and ready for a later phase's checks to use directly.
 */
public class SemanticError {

    private final SemanticErrorType type;
    private final String message;
    private final int line;
    private final int column;
    private final String nodeName;

    public SemanticError(SemanticErrorType type, String message, int line, int column, String nodeName) {
        this.type = Objects.requireNonNull(type, "type");
        this.message = Objects.requireNonNull(message, "message");
        this.line = line;
        this.column = column;
        this.nodeName = nodeName;
    }

    /** Convenience constructor for diagnostics with no meaningful column. */
    public SemanticError(SemanticErrorType type, String message, int line, String nodeName) {
        this(type, message, line, 0, nodeName);
    }

    public SemanticErrorType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    /** The {@code getNodeName()} of the AST node this diagnostic was raised against. */
    public String getNodeName() {
        return nodeName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[').append(type).append("] ").append(message);
        sb.append(" (at ").append(nodeName == null ? "unknown node" : nodeName);
        sb.append(", line ").append(line);
        if (column > 0) {
            sb.append(", column ").append(column);
        }
        sb.append(')');
        return sb.toString();
    }

    /* ======================================================================
     * Nicely-formatted diagnostic printing.
     *
     * A pure extension of the class above: it adds a new, more readable
     * rendering of the exact same four fields {@link #toString()} already
     * exposes, without changing that existing method (still used as-is by
     * {@code semantic.SemanticAnalyzer#getSummary()}) or any other member.
     * ====================================================================== */

    /**
     * Formats this diagnostic the way a compiler's console error output
     * normally reads: a header line naming where the problem is, then the
     * message itself on its own line beneath it, for example:
     *
     * <pre>
     * [Semantic Error] Line 10:
     *   Undefined variable: price
     * </pre>
     *
     * <p>The header intentionally omits {@link #getType()} (every diagnostic
     * this analyzer reports today <em>is</em> a semantic error, so spelling
     * out which {@link SemanticErrorType} would repeat, in different words,
     * information the message text already states plainly - "Undefined
     * variable: price" already says exactly what {@link
     * SemanticErrorType#UNDEFINED_VARIABLE} means) and {@link #getNodeName()}
     * (useful for {@link #toString()}'s denser, single-line form, but mostly
     * internal bookkeeping next to a source line number a reader can
     * actually go look at) — see {@link #toString()} for a form that
     * includes both.
     */
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Semantic Error] Line ").append(line).append(":\n");
        sb.append("  ").append(message);
        return sb.toString();
    }
}
