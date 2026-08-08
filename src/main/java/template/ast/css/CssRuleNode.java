package template.ast.css;

/** Common base for the two kinds of top-level CSS rule: at-rules (e.g.
 * {@code @media ... }) and qualified/style rules (a selector list plus a
 * declaration block).
 */
public abstract class CssRuleNode extends CssNode {
    protected CssRuleNode(int line, int column) {
        super(line, column);
    }
}
