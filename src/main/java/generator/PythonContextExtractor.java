package generator;

import flask.ast.nodes.statements.ProgramNode;
import java.util.List;
import java.util.Map;

/**
 * Extracts variable bindings and render_template() arguments from the Python AST.
 *
 * <p>This used to be a naive top-level-only scan that resolved only direct
 * literal assignments. It is now a thin façade over {@link DataFlowAnalyzer},
 * which performs real flow-sensitive analysis: it follows assignments through
 * dicts/lists/object properties, walks into function bodies and branches, and
 * simulates a handful of builtins so that values reach {@code render_template()}
 * calls no matter how they got there.</p>
 *
 * <p>The public API is unchanged so existing callers (like {@link Generator})
 * keep working: {@link #getVariables()}, {@link #getRenderArguments()} and
 * {@link #getTemplateName()} report the first {@code render_template()} call
 * site found. Callers that care about programs with multiple routes/templates
 * can use {@link #getRenderCalls()} to see every call site with its own context.</p>
 */
public class PythonContextExtractor {

    private final DataFlowAnalyzer analyzer = new DataFlowAnalyzer();
    private DataFlowAnalyzer.RenderCall primaryCall;

    public void extract(ProgramNode program) {
        analyzer.analyze(program);
        List<DataFlowAnalyzer.RenderCall> calls = analyzer.getRenderCalls();
        primaryCall = calls.isEmpty() ? null : calls.get(0);
    }

    /** Every {@code render_template(...)} call site found, each with its own resolved context. */
    public List<DataFlowAnalyzer.RenderCall> getRenderCalls() {
        return analyzer.getRenderCalls();
    }

    /** Module-level (top of file) variable bindings. */
    public Map<String, Object> getVariables() {
        return analyzer.getModuleVariables();
    }

    /** Arguments reaching the first {@code render_template()} call found. */
    public Map<String, Object> getRenderArguments() {
        return primaryCall != null ? primaryCall.getArguments() : Map.of();
    }

    /** Template name (first positional argument) of the first {@code render_template()} call found. */
    public String getTemplateName() {
        return primaryCall != null ? primaryCall.getTemplateName() : null;
    }
}
