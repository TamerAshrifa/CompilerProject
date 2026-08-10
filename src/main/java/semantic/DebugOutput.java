package semantic;

import flask.ast.nodes.ASTNode;
import printer.ASTPrinter;
import semantic.error.SemanticError;
import semantic.scope.SymbolTable;
import template.ast.TemplateProgramNode;
import template.ast.jinja.JinjaNode;

import java.util.List;

/**
 * Unified debug output for one semantic-analysis run: a single call prints
 * the Python AST, the Jinja2 AST, the symbol table {@link SymbolTable}
 * built while walking them, and every {@link SemanticError} found, each
 * section clearly labeled.
 *
 * <p>This class is a thin orchestrator, not a new source of behavior: the
 * Python/Jinja2 AST sections delegate straight to {@link ASTPrinter}
 * (itself built on every node's own {@code print(String)}, see {@link
 * flask.ast.nodes.ASTNode#print(String)} / {@link
 * template.ast.jinja.JinjaNode#print(String)}), the symbol table section
 * delegates to {@link SymbolTable#printSymbolTable()}, and the errors
 * section delegates to {@link SemanticError#format()} - all it adds on top
 * is the section banners tying them into one readable report. Nothing about
 * how any of those four pieces already print was changed to build this.
 *
 * <p>Deliberately independent of {@code pipeline.CompilerPipeline}: {@code
 * pipeline} already depends on {@code semantic} (for {@link
 * semantic.SemanticAnalyzer}), so this class - living in {@code semantic} -
 * does not depend back on {@code pipeline.CompilerPipeline.Result}, in order
 * to avoid a package cycle. A caller already holding a {@code
 * CompilerPipeline.Result} can pass its pieces straight through, since that
 * class exposes exactly the four this one needs:
 *
 * <pre>
 * CompilerPipeline.Result result = CompilerPipeline.compile(pythonSource, templateSource);
 * DebugOutput.printFullDebugOutput(
 *         result.getPythonAst(),
 *         result.getTemplateAst(),
 *         result.getSemanticAnalyzer().getSymbolTable(),
 *         result.getSemanticErrors());
 * </pre>
 */
public final class DebugOutput {

    private DebugOutput() {
        // Static utility class - never instantiated.
    }

    /**
     * Prints the full debug report: Python AST, Jinja2 AST, Symbol Table,
     * and Semantic Errors, in that order, each under its own banner.
     *
     * @param pythonRoot   the analyzed Python AST's root, or {@code null} if
     *                     no Python source was analyzed
     * @param templateRoot the analyzed template's root, or {@code null} if
     *                     no template source was analyzed; only its {@link
     *                     TemplateProgramNode#getJinjaElements() Jinja2 elements}
     *                     are printed, matching {@link ASTPrinter}'s own
     *                     Jinja2-specific scope
     * @param symbolTable  the symbol table populated by analysis
     * @param errors       every semantic error analysis reported, in
     *                     discovery order
     */
    public static void printFullDebugOutput(ASTNode pythonRoot, TemplateProgramNode templateRoot,
                                             SymbolTable symbolTable, List<SemanticError> errors) {
        printPythonSection(pythonRoot);
        printBanner("JINJA2 AST");
        ASTPrinter.printJinjaAst(templateRoot);
        System.out.println();
        printSymbolTableAndErrors(symbolTable, errors);
    }

    /**
     * Same as {@link #printFullDebugOutput(ASTNode, TemplateProgramNode, SymbolTable, List)},
     * for a caller that already has a bare Jinja2 root (e.g. a hand-built
     * {@code JinjaProgramNode}, as in {@code printer.PrintDemo}) rather than
     * a full {@code TemplateProgramNode}.
     */
    public static void printFullDebugOutput(ASTNode pythonRoot, JinjaNode jinjaRoot,
                                             SymbolTable symbolTable, List<SemanticError> errors) {
        printPythonSection(pythonRoot);
        printBanner("JINJA2 AST");
        ASTPrinter.printJinjaAst(jinjaRoot);
        System.out.println();
        printSymbolTableAndErrors(symbolTable, errors);
    }

    private static void printPythonSection(ASTNode pythonRoot) {
        printBanner("PYTHON AST");
        ASTPrinter.printPythonAst(pythonRoot);
        System.out.println();
    }

    private static void printSymbolTableAndErrors(SymbolTable symbolTable, List<SemanticError> errors) {
        printBanner("SYMBOL TABLE");
        if (symbolTable == null) {
            System.out.println("(no symbol table to print)");
        } else {
            symbolTable.printSymbolTable();
        }
        System.out.println();

        printBanner("SEMANTIC ERRORS");
        printErrors(errors);
    }

    /** Prints every error via {@link SemanticError#format()}, separated by a blank line, or a clean "none found" message. */
    private static void printErrors(List<SemanticError> errors) {
        if (errors == null || errors.isEmpty()) {
            System.out.println("No semantic errors found.");
            return;
        }
        for (int i = 0; i < errors.size(); i++) {
            System.out.println(errors.get(i).format());
            if (i < errors.size() - 1) {
                System.out.println();
            }
        }
    }

    private static void printBanner(String title) {
        String rule = "=".repeat(60);
        System.out.println(rule);
        System.out.println(" " + title);
        System.out.println(rule);
    }
}
