package generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Small piece of bookkeeping shared by every concrete generator
 * ({@link PythonGenerator}, {@link JinjaGenerator}, {@link HtmlGenerator},
 * {@link CssGenerator}): how deeply indented the current position in the
 * output is, and the running {@link SourceMapping} trail described in that
 * class's Javadoc.
 *
 * <p>This is deliberately a plain, final helper used through composition
 * (each generator holds one as a private field) rather than a common
 * superclass. The Template-side generators need to extend
 * {@link template.visitor.TemplateBaseVisitor} to properly inherit the
 * project's existing default visitor behavior for the ~40 node types they
 * are <em>not</em> responsible for (rule: "reuse the current Visitor pattern
 * if one already exists"), and Java does not allow a class to also extend a
 * second class for shared infrastructure. Composition sidesteps that
 * limitation cleanly, and it keeps this class trivially unit-testable and
 * reusable on its own.
 */
public final class GenerationSupport {

    private final String indentUnit;
    private int indentLevel = 0;
    private final List<SourceMapping> sourceMap = new ArrayList<>();

    /** Creates a support instance that indents with 4 spaces per level (Python/PEP8 style). */
    public GenerationSupport() {
        this("    ");
    }

    /** Creates a support instance with a caller-chosen indent unit (e.g. two spaces for HTML/CSS). */
    public GenerationSupport(String indentUnit) {
        this.indentUnit = indentUnit;
    }

    /** Returns the indentation string for the current depth (empty string at depth 0). */
    public String indent() {
        StringBuilder sb = new StringBuilder(indentUnit.length() * indentLevel);
        for (int i = 0; i < indentLevel; i++) {
            sb.append(indentUnit);
        }
        return sb.toString();
    }

    /** Increases the current indentation depth by one level; pair with {@link #decreaseIndent()}. */
    public void increaseIndent() {
        indentLevel++;
    }

    /** Decreases the current indentation depth by one level (never below zero). */
    public void decreaseIndent() {
        if (indentLevel > 0) {
            indentLevel--;
        }
    }

    /** The current indentation depth, in levels (not characters). */
    public int getIndentLevel() {
        return indentLevel;
    }

    /**
     * Records that a node with this name/position is about to be generated.
     * Call this as the first action of every {@code visitXxx} override, before
     * building/returning that node's text, so the sequence order matches
     * generation (emission) order. See {@link SourceMapping} for the exact
     * semantics of what is (and is not) captured.
     */
    public void mark(String nodeName, int line, int column) {
        sourceMap.add(new SourceMapping(nodeName, line, column, sourceMap.size()));
    }

    /** Read-only view of every mapping recorded so far, in generation order. */
    public List<SourceMapping> getSourceMap() {
        return Collections.unmodifiableList(sourceMap);
    }

    /** Clears both the recorded source map and the indentation depth, for reusing an instance. */
    public void reset() {
        sourceMap.clear();
        indentLevel = 0;
    }
}
