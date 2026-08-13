package printer;

import flask.ast.nodes.ASTNode;
import template.ast.TemplateProgramNode;
import template.ast.jinja.JinjaNode;
import template.ast.jinja.JinjaProgramNode;

/**
 * Central, single front door for printing a full AST - from any root,
 * belonging to either of this project's two AST hierarchies.
 *
 * <p>Every node in both hierarchies already knows how to print its own
 * subtree (see {@link flask.ast.nodes.ASTNode#print(String)} and {@link
 * JinjaNode#print(String)}, added earlier, both backed by the shared {@link
 * TreePrinter} engine): all a caller strictly needs is {@code root.print("")}.
 * {@code ASTPrinter} exists on top of that so a caller does not need to
 * remember that one-line incantation, and does not need to know the two
 * hierarchies are unrelated types - one class, with one pair of names
 * ({@code printPythonAst} / {@code printJinjaAst}), works for both, with a
 * {@code TemplateProgramNode} overload for the Jinja2 side so this class
 * works directly with whatever {@code SemanticAnalyzer}/{@code CompilerPipeline}
 * already produced, with no extra unwrapping at the call site.
 */
public final class ASTPrinter {

    private ASTPrinter() {
        // Static utility class - never instantiated.
    }

    /** Prints a full Python AST, starting from {@code root}. */
    public static void printPythonAst(ASTNode root) {
        if (root == null) {
            System.out.println("(no Python AST to print)");
            return;
        }
        root.print("");
    }

    /** Prints a full Jinja2 AST, starting from {@code root}. */
    public static void printJinjaAst(JinjaNode root) {
        if (root == null) {
            System.out.println("(no Jinja2 AST to print)");
            return;
        }
        root.print("");
    }

    /**
     * Prints the Jinja2 AST embedded in a full template document.
     *
     * <p>{@code TemplateProgramNode} - the root {@code SemanticAnalyzer} and
     * {@code CompilerPipeline} actually work with - carries the template's
     * HTML and Jinja2 elements as two separate, independent lists (see its
     * own class documentation); this overload prints only the Jinja2 side,
     * by wrapping {@link TemplateProgramNode#getJinjaElements()} in a fresh
     * {@link JinjaProgramNode} purely for display and delegating to {@link
     * #printJinjaAst(JinjaNode)}. {@code templateProgram} itself is never
     * modified - the wrapper is a new, throwaway node that simply reuses the
     * same element references TemplateProgramNode is already holding.
     */
    public static void printJinjaAst(TemplateProgramNode templateProgram) {
        if (templateProgram == null) {
            System.out.println("(no Jinja2 AST to print)");
            return;
        }
        printJinjaAst(asJinjaProgramNode(templateProgram));
    }

    /**
     * Prints both AST hierarchies together, one after the other, with a
     * short label above each so the two are easy to tell apart on screen.
     * Either root may be {@code null} (e.g. a source file that has a Python
     * side but no template, or vice versa).
     */
    public static void printAllAsts(ASTNode pythonRoot, JinjaNode jinjaRoot) {
        System.out.println("--- Python AST ---");
        printPythonAst(pythonRoot);
        System.out.println();
        System.out.println("--- Jinja2 AST ---");
        printJinjaAst(jinjaRoot);
    }

    /** Convenience overload of {@link #printAllAsts(ASTNode, JinjaNode)} taking a full {@code TemplateProgramNode}. */
    public static void printAllAsts(ASTNode pythonRoot, TemplateProgramNode templateProgram) {
        printAllAsts(pythonRoot, (templateProgram == null) ? null : asJinjaProgramNode(templateProgram));
    }

    private static JinjaProgramNode asJinjaProgramNode(TemplateProgramNode templateProgram) {
        return new JinjaProgramNode(
                templateProgram.getJinjaElements(),
                templateProgram.getLine(),
                templateProgram.getColumn());
    }
}
