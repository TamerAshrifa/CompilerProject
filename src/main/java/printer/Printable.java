package printer;

/**
 * Implemented by every AST node hierarchy in this project that supports
 * structured, indented tree printing - currently {@code flask.ast.nodes.ASTNode}
 * (the Python AST) and {@code template.ast.jinja.JinjaNode} (the Jinja2 AST).
 *
 * <p>The two hierarchies share no common superclass - they model two
 * completely different languages - so this tiny interface is what lets the
 * single, shared {@link TreePrinter} engine print a subtree belonging to
 * either one without caring which language it came from. Any future AST
 * added to this project can reuse the same printing engine simply by
 * implementing this interface and delegating to {@link TreePrinter} the way
 * {@code ASTNode} and {@code JinjaNode} do.
 */
public interface Printable {

    /**
     * Prints this node, and recursively its entire subtree, to standard
     * output as one indented tree that uses box-drawing characters
     * ({@code ├──}, {@code └──}, {@code │}) to show parent/child
     * relationships, for example:
     *
     * <pre>
     * FunctionDefNode (line 3)
     * ├── Name: greet
     * └── Body:
     *     └── ReturnNode (line 4)
     * </pre>
     *
     * @param indent the exact text to place before this node's own line on
     *               screen. This already includes any branch glyph chosen
     *               by the parent that is printing this node as one of its
     *               own children (e.g. {@code "├── "}); pass {@code ""}
     *               when printing a node as the root of a whole tree.
     */
    void print(String indent);
}
