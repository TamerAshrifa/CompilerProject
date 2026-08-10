package generator;

import flask.ast.nodes.statements.ProgramNode;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Prints how data flows from the Python side to the Jinja2 side: for every
 * {@code render_template(...)} call site found in a Python AST, which
 * variable names reach it, e.g.
 *
 * <pre>
 * Data Flow:
 *   render_template("products.html"):
 *     products &#8594; products.html
 *     title &#8594; products.html
 * </pre>
 *
 * <p>This is a pure, read-only observer, not a new analysis: it runs a
 * fresh {@link PythonContextExtractor} (the existing, unmodified façade
 * over {@link DataFlowAnalyzer}, itself unmodified) purely for this
 * printout. It shares no state with whatever {@link Generator} instance
 * may separately be running the real compilation - each gets its own
 * independent extractor/analyzer - so calling this can never influence
 * the data flow it is describing, or any other part of code generation.
 */
public final class DataFlowPrinter {

    static {
        // Mirrors printer.TreePrinter's own fix, independently: the arrow
        // character this class prints must reach the console as UTF-8, and
        // this class does not assume some other class already forced that -
        // see TreePrinter's static initializer for the full explanation of
        // why stdout's encoding cannot otherwise be relied upon.
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
    }

    private DataFlowPrinter() {
        // Static utility class - never instantiated.
    }

    /**
     * Prints every {@code render_template(...)} call site found in {@code
     * pythonAst}, and the variable names that reach each one.
     *
     * <p>Only variable <em>names</em> are shown, matching the flow this
     * class is reporting on ("which Python names cross into the template"),
     * not their resolved values - the {@code SYMBOL TABLE} section already
     * covers what each name is, and printing values here would just repeat
     * the {@code render_template(...)} kwargs already visible in the
     * {@code PYTHON AST} section above it.
     */
    public static void printDataFlow(ProgramNode pythonAst) {
        System.out.println("Data Flow:");

        if (pythonAst == null) {
            System.out.println("  (no Python AST to analyze)");
            return;
        }

        PythonContextExtractor extractor = new PythonContextExtractor();
        extractor.extract(pythonAst);
        List<DataFlowAnalyzer.RenderCall> calls = extractor.getRenderCalls();

        if (calls.isEmpty()) {
            System.out.println("  (no render_template(...) calls found - nothing flows from Python to Jinja2)");
            return;
        }

        for (DataFlowAnalyzer.RenderCall call : calls) {
            String destination = (call.getTemplateName() != null) ? call.getTemplateName() : "Jinja2 template";
            System.out.println("  render_template(\"" + destination + "\"):");

            Map<String, Object> arguments = call.getArguments();
            if (arguments.isEmpty()) {
                System.out.println("    (no variables passed)");
                continue;
            }
            for (String variableName : arguments.keySet()) {
                System.out.println("    " + variableName + " \u2192 " + destination);
            }
        }
    }
}
