package generator;

/**
 * A single entry correlating a piece of generated output with the AST node
 * that produced it.
 *
 * <p>Every {@code visitXxx} method in {@link PythonGenerator}, {@link JinjaGenerator},
 * {@link HtmlGenerator} and {@link CssGenerator} records one {@code SourceMapping}
 * (via {@link GenerationSupport#mark(String, int, int)}) before it builds the
 * text for that node. Because generation is a depth-first, pre-order traversal
 * (a node is marked, then its children are visited and their own text is
 * combined into the parent's), the resulting list is naturally ordered the
 * same way the output was assembled — entry {@code N} was emitted before
 * entry {@code N + 1}.
 *
 * <p>This is intentionally a coarse-grained trace (node identity + original
 * source position + emission order) rather than a byte-exact source map with
 * generated-file offsets. That keeps it simple and safe to build during a
 * pure, recursive "return the generated text" traversal, while still giving
 * a future pipeline stage (or a debugger, or a test) everything it needs to
 * answer "which original line produced this part of the output, and in what
 * order was it generated". A tool that wants exact offsets can be layered on
 * top of this later without changing how generation itself works.
 *
 * <p>This class has nothing to do with {@code semantic.SemanticError} or any
 * other class in the {@code semantic} package — it is generation-time
 * bookkeeping only, keeping the generator package independent as required.
 */
public final class SourceMapping {

    private final String nodeName;
    private final int sourceLine;
    private final int sourceColumn;
    private final int sequence;

    public SourceMapping(String nodeName, int sourceLine, int sourceColumn, int sequence) {
        this.nodeName = nodeName;
        this.sourceLine = sourceLine;
        this.sourceColumn = sourceColumn;
        this.sequence = sequence;
    }

    /** The originating node's {@code getNodeName()} (e.g. "IfStatementNode", "JinjaForNode"). */
    public String getNodeName() {
        return nodeName;
    }

    /** The originating node's source line (as captured by the parser/AST builder). */
    public int getSourceLine() {
        return sourceLine;
    }

    /** The originating node's source column (as captured by the parser/AST builder). */
    public int getSourceColumn() {
        return sourceColumn;
    }

    /** 0-based position of this entry in overall generation order (emission order). */
    public int getSequence() {
        return sequence;
    }

    @Override
    public String toString() {
        return "#" + sequence + " " + nodeName + " (line=" + sourceLine + ", column=" + sourceColumn + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SourceMapping)) return false;
        SourceMapping other = (SourceMapping) o;
        return sourceLine == other.sourceLine
                && sourceColumn == other.sourceColumn
                && sequence == other.sequence
                && nodeName.equals(other.nodeName);
    }

    @Override
    public int hashCode() {
        int result = nodeName.hashCode();
        result = 31 * result + sourceLine;
        result = 31 * result + sourceColumn;
        result = 31 * result + sequence;
        return result;
    }
}
