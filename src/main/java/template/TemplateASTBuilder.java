package template;

import grammar.template.TemplateParser;
import grammar.template.TemplateParserBaseVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;

// HTML AST imports
import template.ast.html.HtmlNode;
import template.ast.html.HtmlElementNode;
import template.ast.html.HtmlAttributeNode;
import template.ast.html.HtmlTextNode;
import template.ast.html.HtmlCommentNode;
import template.ast.html.StyleElementNode;

// CSS AST imports
import template.ast.css.CssAtRuleNode;
import template.ast.css.CssDeclarationNode;
import template.ast.css.CssRuleNode;
import template.ast.css.CssStyleRuleNode;
import template.ast.css.CssStylesheetNode;

// Jinja2 AST imports
import template.ast.jinja.JinjaNode;
import template.ast.jinja.JinjaProgramNode;
import template.ast.jinja.JinjaVariableNode;
import template.ast.jinja.JinjaExpressionNode;
import template.ast.jinja.JinjaFilterNode;
import template.ast.jinja.JinjaIfNode;
import template.ast.jinja.JinjaElifNode;
import template.ast.jinja.JinjaElseNode;
import template.ast.jinja.JinjaForNode;
import template.ast.jinja.JinjaBlockNode;
import template.ast.jinja.JinjaMacroNode;
import template.ast.jinja.JinjaCommentNode;
import template.ast.jinja.JinjaExtendsNode;
import template.ast.jinja.JinjaIncludeNode;
import template.ast.jinja.LiteralNode;
import template.ast.jinja.JinjaIdentifierNode;
import template.ast.jinja.JinjaBinaryOpNode;
import template.ast.jinja.JinjaCompareNode;
import template.ast.jinja.JinjaUnaryOpNode;
import template.ast.jinja.JinjaAttributeAccessNode;
import template.ast.jinja.JinjaSubscriptNode;
import template.ast.jinja.JinjaCallNode;
import template.ast.jinja.JinjaFilterApplicationNode;

// Template core imports (legacy compatibility)
import template.ast.TemplateNode;
import template.ast.TemplateProgramNode;
import template.ast.TemplateRuleNode;
import template.visitor.TemplateVisitor;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * AST Builder for Template documents (HTML + Jinja2).
 * 
 * This builder creates completely independent AST hierarchies:
 * - HTML nodes from template.ast.html.*
 * - Jinja2 nodes from template.ast.jinja.*
 * 
 * Every parser rule maps to one specific AST node type.
 * There is NO merging or overlap between HTML and Jinja2 nodes.
 */
public class TemplateASTBuilder extends TemplateParserBaseVisitor<Object> {

    private TemplateProgramNode programNode;

    // Jinja2 control structures (if/for/block/macro) and expressions can
    // appear textually nested inside an HTML tag's content (e.g.
    // "<ul>{% for x in xs %}<li>{{ x }}</li>{% endfor %}</ul>", a very
    // common real-world pattern). HtmlElementNode.children is deliberately
    // typed List<HtmlNode> only - the two trees stay independent - so any
    // JinjaNode found while building a tag's content is hoisted here
    // instead of being silently dropped, and gets appended to the
    // program's top-level Jinja list once the whole document is built.
    private final List<JinjaNode> hoistedJinjaNodes = new ArrayList<>();

    public TemplateProgramNode build(ParseTree tree) {
        programNode = new TemplateProgramNode();
        visit(tree);
        for (JinjaNode jinjaNode : hoistedJinjaNodes) {
            programNode.addJinjaElement(jinjaNode);
        }
        return programNode;
    }

    // ========================================
    // HTML Document & Elements
    // ========================================

    @Override
    public Object visitDocument(TemplateParser.DocumentContext ctx) {
        // Step 1: collect every top-level node (HTML tags, Jinja blocks/exprs/
        // comments, doctypes, comments) in source order. Each htmlElements
        // context can contain more than one relevant child (leading/trailing
        // htmlMisc *and* the element itself), so every direct child is
        // visited individually instead of delegating to visitHtmlElements()
        // and keeping only its first result.
        List<Object> flat = new ArrayList<>();
        for (TemplateParser.HtmlElementsContext htmlElementsContext : ctx.htmlElements()) {
            flat.addAll(flattenHtmlElements(htmlElementsContext));
        }

        // Step 2: the grammar treats {% for %}, {% endfor %}, {% if %},
        // {% elif %}, {% else %}, {% endif %}, {% block %}/{% endblock %},
        // and {% macro %}/{% endmacro %} as independent, flat tags - it has
        // no rule that nests the tokens between an opening and closing tag.
        // This pass reconstructs the real nested Jinja2 tree (matching
        // control-flow tags and folding everything between them into the
        // owning node's body) so the Generator receives an actual AST with
        // real bodies instead of control nodes with permanently empty ones.
        List<Object> nested = nestJinjaControlStructures(flat);

        for (Object element : nested) {
            if (element instanceof HtmlNode htmlNode) {
                programNode.addHtmlElement(htmlNode);
            } else if (element instanceof JinjaNode jinjaNode) {
                programNode.addJinjaElement(jinjaNode);
            }
        }

        return programNode;
    }

    /**
     * Visits every direct child of an {@code htmlElements} context
     * (leading htmlMisc*, the htmlElement, trailing htmlMisc*) exactly once,
     * in document order, collecting all non-null results.
     */
    private List<Object> flattenHtmlElements(TemplateParser.HtmlElementsContext ctx) {
        List<Object> results = new ArrayList<>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            Object result = visit(ctx.getChild(i));
            if (result != null) {
                results.add(result);
            }
        }
        return results;
    }

    /**
     * Internal marker produced for {@code endfor}/{@code endif}/
     * {@code endblock}/{@code endmacro} tags. It only ever exists inside
     * {@link #nestJinjaControlStructures(List)} - it is consumed while
     * closing the matching frame and must never end up in the final tree.
     */
    private static final class JinjaEndMarkerNode extends JinjaNode {
        private final String tag;

        JinjaEndMarkerNode(String tag) {
            this.tag = tag;
        }

        String getTag() {
            return tag;
        }

        @Override
        public <T> T accept(TemplateVisitor<T> visitor) {
            throw new UnsupportedOperationException(
                "Internal marker node '" + tag + "' was not consumed during AST nesting "
                    + "and must not appear in the final tree.");
        }
    }

    /**
     * One open Jinja2 control-flow tag (for/if/block/macro) being assembled
     * while walking the flat tag sequence.
     */
    private static final class Frame {
        enum Kind { FOR, IF, BLOCK, MACRO }

        Kind kind;
        int line;
        int column;

        // FOR
        String loopVariable;
        String iterable;
        JinjaNode iterableTree;

        // IF
        String condition;
        JinjaNode conditionTree;
        final List<JinjaNode> thenBody = new ArrayList<>();
        final List<JinjaElifNode> elifNodes = new ArrayList<>();
        String pendingElifCondition;
        JinjaNode pendingElifConditionTree;
        List<JinjaNode> pendingElifBody;
        int pendingElifLine;
        int pendingElifColumn;
        boolean inElse = false;
        int elseLine;
        int elseColumn;
        final List<JinjaNode> elseBody = new ArrayList<>();

        // BLOCK / MACRO / FOR body accumulator
        final List<JinjaNode> body = new ArrayList<>();

        // BLOCK / MACRO
        String name;
        List<String> parameters;

        void commitPendingElif() {
            if (pendingElifCondition != null) {
                elifNodes.add(new JinjaElifNode(pendingElifCondition, pendingElifConditionTree, pendingElifBody, pendingElifLine, pendingElifColumn));
                pendingElifCondition = null;
                pendingElifConditionTree = null;
                pendingElifBody = null;
            }
        }

        /** Appends a Jinja child to whichever segment is currently active. */
        void addJinjaChild(JinjaNode child) {
            if (kind == Kind.IF) {
                if (inElse) {
                    elseBody.add(child);
                } else if (pendingElifCondition != null) {
                    pendingElifBody.add(child);
                } else {
                    thenBody.add(child);
                }
            } else {
                body.add(child);
            }
        }

        JinjaNode build() {
            if (kind == Kind.FOR) {
                return new JinjaForNode(loopVariable, iterable, iterableTree, body, line, column);
            }
            if (kind == Kind.IF) {
                commitPendingElif();
                JinjaElseNode elseNode = inElse ? new JinjaElseNode(elseBody, elseLine, elseColumn) : null;
                return new JinjaIfNode(condition, conditionTree, thenBody, elifNodes, elseNode, line, column);
            }
            if (kind == Kind.BLOCK) {
                return new JinjaBlockNode(name, body, line, column);
            }
            return new JinjaMacroNode(name, parameters, body, line, column);
        }
    }

    /**
     * Reconstructs real nested Jinja2 control structures from the flat
     * sequence of top-level tags produced by the parser. HTML nodes are
     * always placed at the top level (the HTML tree and the Jinja2 tree
     * stay independent, as documented on this class), so an HTML tag that
     * textually sits between e.g. {@code {% for %}} and {@code {% endfor %}}
     * is emitted once at the top level rather than duplicated per iteration
     * - only Jinja2 nodes participate in the nested body that the Generator
     * later unrolls/evaluates.
     */
    private List<Object> nestJinjaControlStructures(List<Object> flat) {
        Deque<Frame> stack = new ArrayDeque<>();
        List<Object> topLevel = new ArrayList<>();

        for (Object item : flat) {
            if (item instanceof JinjaEndMarkerNode marker) {
                if (stack.isEmpty()) {
                    // Stray/unmatched closing tag: ignore rather than fail
                    // the whole parse.
                    continue;
                }
                JinjaNode built = stack.pop().build();
                emit(built, stack, topLevel);
                continue;
            }

            if (item instanceof JinjaForNode forStub) {
                Frame frame = new Frame();
                frame.kind = Frame.Kind.FOR;
                frame.loopVariable = forStub.getLoopVariable();
                frame.iterable = forStub.getIterable();
                frame.iterableTree = forStub.getIterableTree();
                frame.line = forStub.getLine();
                frame.column = forStub.getColumn();
                stack.push(frame);
                continue;
            }

            if (item instanceof JinjaIfNode ifStub) {
                Frame frame = new Frame();
                frame.kind = Frame.Kind.IF;
                frame.condition = ifStub.getCondition();
                frame.conditionTree = ifStub.getConditionTree();
                frame.line = ifStub.getLine();
                frame.column = ifStub.getColumn();
                stack.push(frame);
                continue;
            }

            if (item instanceof JinjaElifNode elifStub) {
                if (!stack.isEmpty() && stack.peek().kind == Frame.Kind.IF) {
                    Frame top = stack.peek();
                    top.commitPendingElif();
                    top.pendingElifCondition = elifStub.getCondition();
                    top.pendingElifConditionTree = elifStub.getConditionTree();
                    top.pendingElifBody = new ArrayList<>();
                    top.pendingElifLine = elifStub.getLine();
                    top.pendingElifColumn = elifStub.getColumn();
                }
                continue;
            }

            if (item instanceof JinjaElseNode elseStub) {
                if (!stack.isEmpty() && stack.peek().kind == Frame.Kind.IF) {
                    Frame top = stack.peek();
                    top.commitPendingElif();
                    top.inElse = true;
                    top.elseLine = elseStub.getLine();
                    top.elseColumn = elseStub.getColumn();
                }
                continue;
            }

            if (item instanceof JinjaBlockNode blockStub) {
                Frame frame = new Frame();
                frame.kind = Frame.Kind.BLOCK;
                frame.name = blockStub.getBlockName();
                frame.line = blockStub.getLine();
                frame.column = blockStub.getColumn();
                stack.push(frame);
                continue;
            }

            if (item instanceof JinjaMacroNode macroStub) {
                Frame frame = new Frame();
                frame.kind = Frame.Kind.MACRO;
                frame.name = macroStub.getMacroName();
                frame.parameters = new ArrayList<>(macroStub.getParameters());
                frame.line = macroStub.getLine();
                frame.column = macroStub.getColumn();
                stack.push(frame);
                continue;
            }

            // Leaf node: HtmlNode, JinjaVariableNode, JinjaExpressionNode,
            // JinjaCommentNode, JinjaExtendsNode, JinjaIncludeNode, ...
            emit(item, stack, topLevel);
        }

        // Unclosed tags (malformed template): flush whatever was
        // accumulated instead of silently dropping the content.
        while (!stack.isEmpty()) {
            JinjaNode built = stack.pop().build();
            emit(built, stack, topLevel);
        }

        return topLevel;
    }

    private void emit(Object item, Deque<Frame> stack, List<Object> topLevel) {
        if (stack.isEmpty() || item instanceof HtmlNode) {
            topLevel.add(item);
        } else if (item instanceof JinjaNode jinjaNode) {
            stack.peek().addJinjaChild(jinjaNode);
        } else {
            topLevel.add(item);
        }
    }

    // NOTE ON METHOD NAMES: every rule below has ALL of its alternatives
    // labeled ("#TagElement", "#Attribute", "#MiscWhitespace", ...) in the
    // grammar. When every alternative of a rule is labeled, ANTLR4 generates
    // one visitor method PER LABEL (e.g. "visitTagElement") instead of one
    // method per rule (e.g. "visitHtmlElement") - the rule-named methods
    // therefore do not exist on TemplateParserBaseVisitor at all. The
    // original code here used the rule names with @Override, which does not
    // compile against the generated visitor; the methods below use the
    // actual generated names.

    @Override
    public Object visitTagElement(TemplateParser.TagElementContext ctx) {
        // HTML element: <tag attributes>content</tag>
        String tagName = ctx.TAG_NAME(0) != null ? ctx.TAG_NAME(0).getText() : "";
        List<HtmlAttributeNode> attributes = new ArrayList<>();

        for (TemplateParser.HtmlAttributeContext attrCtx : ctx.htmlAttribute()) {
            HtmlAttributeNode attrNode = (HtmlAttributeNode) visit(attrCtx);
            if (attrNode != null) {
                attributes.add(attrNode);
            }
        }

        List<HtmlNode> children = new ArrayList<>();
        if (ctx.htmlContent() != null) {
            for (Object item : collectContentItems(ctx.htmlContent())) {
                if (item instanceof HtmlNode htmlNode) {
                    children.add(htmlNode);
                } else if (item instanceof JinjaNode jinjaNode) {
                    hoistedJinjaNodes.add(jinjaNode);
                }
            }
        }

        boolean isSelfClosing = ctx.TAG_SLASH_CLOSE() != null;
        return new HtmlElementNode(tagName, attributes, children, isSelfClosing, lineOf(ctx), columnOf(ctx));
    }

    /**
     * Collects every child of an htmlContent context (leading/trailing
     * chardata, and any mix of nested tags, comments, and Jinja2 blocks/
     * expressions/comments), then reconstructs real Jinja2 nesting among
     * them exactly like visitDocument does at the top level - htmlContent
     * has the same "flat sequence of tags and Jinja fragments" shape as
     * htmlElements, so the same two-step flatten-then-nest logic applies.
     */
    private List<Object> collectContentItems(TemplateParser.HtmlContentContext ctx) {
        List<Object> flat = new ArrayList<>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            Object result = visit(ctx.getChild(i));
            if (result != null) {
                flat.add(result);
            }
        }
        return nestJinjaControlStructures(flat);
    }

    @Override
    public Object visitStyleElement(TemplateParser.StyleElementContext ctx) {
        return visit(ctx.style());
    }

    @Override
    public Object visitJinjaBlockElement(TemplateParser.JinjaBlockElementContext ctx) {
        return visit(ctx.jinjaBlock());
    }

    @Override
    public Object visitJinjaExprElement(TemplateParser.JinjaExprElementContext ctx) {
        return visit(ctx.jinjaExpr());
    }

    @Override
    public Object visitJinjaCommentElement(TemplateParser.JinjaCommentElementContext ctx) {
        return visit(ctx.jinjaComment());
    }

    @Override
    public Object visitAttribute(TemplateParser.AttributeContext ctx) {
        String name = ctx.TAG_NAME().getText();
        String value = null;

        if (ctx.ATTVALUE_VALUE() != null) {
            value = stripQuotes(ctx.ATTVALUE_VALUE().getText());
        }

        return new HtmlAttributeNode(name, value, lineOf(ctx), columnOf(ctx));
    }

    @Override
    public Object visitTextContent(TemplateParser.TextContentContext ctx) {
        String text = ctx.getText();
        if (text != null && !text.isEmpty()) {
            return new HtmlTextNode(text, lineOf(ctx), columnOf(ctx));
        }
        return null;
    }

    @Override
    public Object visitWhitespaceContent(TemplateParser.WhitespaceContentContext ctx) {
        // Preserve prior behaviour: whitespace chardata was also wrapped as
        // text if present (harmless extra whitespace text nodes).
        String text = ctx.getText();
        if (text != null && !text.isEmpty()) {
            return new HtmlTextNode(text, lineOf(ctx), columnOf(ctx));
        }
        return null;
    }

    @Override
    public Object visitMiscDoctype(TemplateParser.MiscDoctypeContext ctx) {
        return visit(ctx.htmlDoctype());
    }

    @Override
    public Object visitMiscComment(TemplateParser.MiscCommentContext ctx) {
        return visit(ctx.htmlComment());
    }

    @Override
    public Object visitMiscWhitespace(TemplateParser.MiscWhitespaceContext ctx) {
        // Skip whitespace between top-level elements.
        return null;
    }

    @Override
    public Object visitMiscText(TemplateParser.MiscTextContext ctx) {
        // Freestanding text between/around top-level elements or inside a
        // Jinja control-flow body (e.g. "Hello, " and "!" in
        // {% macro greet(name) %}Hello, {{ name }}!{% endmacro %}), i.e.
        // text that is not wrapped in any HTML tag. Mirrors visitTextContent.
        String text = ctx.getText();
        if (text != null && !text.isEmpty()) {
            return new HtmlTextNode(text, lineOf(ctx), columnOf(ctx));
        }
        return null;
    }

    @Override
    public Object visitHtmlDoctype(TemplateParser.HtmlDoctypeContext ctx) {
        // htmlDoctype has no alternative labels, so this rule-named method
        // is (unlike the ones above) the actual generated name.
        String doctypeText = ctx.getText();
        return new HtmlTextNode(doctypeText, lineOf(ctx), columnOf(ctx));
    }

    @Override
    public Object visitStandardComment(TemplateParser.StandardCommentContext ctx) {
        return buildHtmlComment(ctx.getText(), lineOf(ctx), columnOf(ctx));
    }

    @Override
    public Object visitConditionalComment(TemplateParser.ConditionalCommentContext ctx) {
        return buildHtmlComment(ctx.getText(), lineOf(ctx), columnOf(ctx));
    }

    private HtmlCommentNode buildHtmlComment(String commentText, int line, int column) {
        // Strip HTML comment delimiters when present (standard comments);
        // conditional comments use a looser "<! ... >" form and are kept
        // as-is.
        if (commentText.startsWith("<!--") && commentText.endsWith("-->")) {
            commentText = commentText.substring(4, commentText.length() - 3).trim();
        }
        return new HtmlCommentNode(commentText, line, column);
    }

    // ========================================
    // CSS Styling
    // ========================================

    @Override
    public Object visitStyleBlock(TemplateParser.StyleBlockContext ctx) {
        CssStylesheetNode stylesheet = ctx.cssStylesheet() != null
                ? buildCssStylesheet(ctx.cssStylesheet())
                : new CssStylesheetNode(List.of(), lineOf(ctx), columnOf(ctx));
        return new StyleElementNode(stylesheet, lineOf(ctx), columnOf(ctx));
    }

    private CssStylesheetNode buildCssStylesheet(TemplateParser.CssStylesheetContext ctx) {
        List<CssRuleNode> rules = new ArrayList<>();
        for (TemplateParser.CssStylesheetItemContext itemContext : ctx.cssStylesheetItem()) {
            if (itemContext.cssRule() != null) {
                rules.add(buildCssRule(itemContext.cssRule()));
            }
        }
        return new CssStylesheetNode(rules, lineOf(ctx), columnOf(ctx));
    }

    private CssRuleNode buildCssRule(TemplateParser.CssRuleContext ctx) {
        if (ctx.cssAtRule() != null) {
            return buildCssAtRule(ctx.cssAtRule());
        }
        return buildCssStyleRule(ctx.cssQualifiedRule());
    }

    private CssStyleRuleNode buildCssStyleRule(TemplateParser.CssQualifiedRuleContext ctx) {
        List<String> selectors = new ArrayList<>();
        for (TemplateParser.CssSelectorContext selectorContext : ctx.cssSelectorList().cssSelector()) {
            selectors.add(originalText(selectorContext).trim());
        }
        List<CssDeclarationNode> declarations = new ArrayList<>();
        for (TemplateParser.CssBlockItemContext itemContext : ctx.cssBlock().cssBlockItem()) {
            if (itemContext.cssDeclaration() != null) {
                declarations.add(buildCssDeclaration(itemContext.cssDeclaration()));
            }
        }
        return new CssStyleRuleNode(selectors, declarations, lineOf(ctx), columnOf(ctx));
    }

    private CssAtRuleNode buildCssAtRule(TemplateParser.CssAtRuleContext ctx) {
        String name = ctx.CSS_AT_KEYWORD().getText().substring(1); // strip leading '@'
        StringBuilder prelude = new StringBuilder();
        for (TemplateParser.CssAtRulePreludeContext preludeContext : ctx.cssAtRulePrelude()) {
            if (prelude.length() > 0) {
                prelude.append(' ');
            }
            prelude.append(originalText(preludeContext).trim());
        }

        List<CssDeclarationNode> declarations = new ArrayList<>();
        List<CssRuleNode> nestedRules = new ArrayList<>();
        if (ctx.cssBlock() != null) {
            for (TemplateParser.CssBlockItemContext itemContext : ctx.cssBlock().cssBlockItem()) {
                if (itemContext.cssDeclaration() != null) {
                    declarations.add(buildCssDeclaration(itemContext.cssDeclaration()));
                } else if (itemContext.cssRule() != null) {
                    nestedRules.add(buildCssRule(itemContext.cssRule()));
                }
            }
        }
        return new CssAtRuleNode(name, prelude.toString(), declarations, nestedRules, lineOf(ctx), columnOf(ctx));
    }

    private CssDeclarationNode buildCssDeclaration(TemplateParser.CssDeclarationContext ctx) {
        String property = originalText(ctx.cssPropertyName()).trim();
        String value = originalText(ctx.cssValueSequence()).trim();
        boolean important = ctx.cssImportant() != null;
        return new CssDeclarationNode(property, value, important, lineOf(ctx), columnOf(ctx));
    }

    // ========================================
    // Jinja2 Blocks
    // ========================================

    @Override
    public Object visitJinjaBlockRule(TemplateParser.JinjaBlockRuleContext ctx) {
        return visit(ctx.jinjaTag());
    }

    @Override
    public Object visitIfTag(TemplateParser.IfTagContext ctx) {
        JinjaNode conditionTree = buildJinjaExpressionTree(ctx.jinjaOrExpr());
        return new JinjaIfNode(originalText(ctx.jinjaOrExpr()), conditionTree, new ArrayList<>(), lineOf(ctx), columnOf(ctx));
    }

    @Override
    public Object visitElifTag(TemplateParser.ElifTagContext ctx) {
        JinjaNode conditionTree = buildJinjaExpressionTree(ctx.jinjaOrExpr());
        return new JinjaElifNode(originalText(ctx.jinjaOrExpr()), conditionTree, new ArrayList<>(), lineOf(ctx), columnOf(ctx));
    }

    @Override
    public Object visitElseTag(TemplateParser.ElseTagContext ctx) {
        return new JinjaElseNode(new ArrayList<>(), lineOf(ctx), columnOf(ctx));
    }

    @Override
    public Object visitEndIfTag(TemplateParser.EndIfTagContext ctx) {
        return new JinjaEndMarkerNode("endif");
    }

    @Override
    public Object visitForTag(TemplateParser.ForTagContext ctx) {
        String loopVariable = ctx.jinjaForTargets().getText();
        String iterable = originalText(ctx.jinjaOrExpr());
        JinjaNode iterableTree = buildJinjaExpressionTree(ctx.jinjaOrExpr());
        return new JinjaForNode(loopVariable, iterable, iterableTree, new ArrayList<>(), lineOf(ctx), columnOf(ctx));
    }

    @Override
    public Object visitEndForTag(TemplateParser.EndForTagContext ctx) {
        return new JinjaEndMarkerNode("endfor");
    }

    @Override
    public Object visitBlockTag(TemplateParser.BlockTagContext ctx) {
        return new JinjaBlockNode(ctx.JJ_IDENTIFIER().getText(), new ArrayList<>(), lineOf(ctx), columnOf(ctx));
    }

    @Override
    public Object visitEndBlockTag(TemplateParser.EndBlockTagContext ctx) {
        return new JinjaEndMarkerNode("endblock");
    }

    @Override
    public Object visitMacroTag(TemplateParser.MacroTagContext ctx) {
        String macroName = ctx.JJ_IDENTIFIER().getText();
        List<String> parameters = new ArrayList<>();
        if (ctx.jinjaParamList() != null) {
            for (org.antlr.v4.runtime.tree.TerminalNode identifier : ctx.jinjaParamList().JJ_IDENTIFIER()) {
                parameters.add(identifier.getText());
            }
        }
        return new JinjaMacroNode(macroName, parameters, new ArrayList<>(), lineOf(ctx), columnOf(ctx));
    }

    @Override
    public Object visitEndMacroTag(TemplateParser.EndMacroTagContext ctx) {
        return new JinjaEndMarkerNode("endmacro");
    }

    @Override
    public Object visitExtendsTag(TemplateParser.ExtendsTagContext ctx) {
        return new JinjaExtendsNode(stripQuotes(ctx.JJ_STRING().getText()), lineOf(ctx), columnOf(ctx));
    }

    @Override
    public Object visitIncludeTag(TemplateParser.IncludeTagContext ctx) {
        return new JinjaIncludeNode(stripQuotes(ctx.JJ_STRING().getText()), lineOf(ctx), columnOf(ctx));
    }

    @Override
    public Object visitJinjaExpression(TemplateParser.JinjaExpressionContext ctx) {
        return buildVariableOrExpression(ctx.jinjaOrExpr(), lineOf(ctx), columnOf(ctx));
    }

    @Override
    public Object visitJinjaCommentRule(TemplateParser.JinjaCommentRuleContext ctx) {
        return new JinjaCommentNode(extractJinjaContent(ctx), lineOf(ctx), columnOf(ctx));
    }

    // ========================================
    // Jinja2 expression construction
    //
    // The grammar now formally recognizes Jinja2's real syntax (keywords,
    // operator precedence, attribute/subscript/call trailers, filters) -
    // the methods below only need to walk the already-structured parse
    // tree and fold it back into the existing JinjaVariableNode /
    // JinjaExpressionNode API (a plain "name[.attr|[idx]]*" chain with
    // optional filters keeps using the more specific JinjaVariableNode,
    // exactly like before; anything with a real operator becomes a
    // JinjaExpressionNode), so Generator.java's evaluation logic keeps
    // working unchanged.
    // ========================================

    private JinjaNode buildVariableOrExpression(TemplateParser.JinjaOrExprContext ctx, int line, int column) {
        TemplateParser.JinjaFilteredPrimaryContext plain = asPlainFilteredPrimary(ctx);
        if (plain != null && isIdentifierRooted(plain.jinjaPrimary())) {
            JinjaVariableNode variableNode = new JinjaVariableNode(originalText(plain.jinjaPrimary()), line, column);
            for (TemplateParser.JinjaFilterCallContext filterCallContext : plain.jinjaFilterCall()) {
                variableNode.addFilter(buildFilter(filterCallContext));
            }
            return variableNode;
        }
        JinjaNode tree = buildJinjaExpressionTree(ctx);
        return new JinjaExpressionNode(originalText(ctx), tree, line, column);
    }

    /**
     * True for "name", "name.attr", "name[0]", "name(args)" and chains
     * thereof - i.e. an identifier followed by zero or more trailers.
     * False for bare literals (numbers, strings, true/false/none), which
     * should become a JinjaExpressionNode rather than a JinjaVariableNode
     * literally named after the quoted/literal text.
     */
    private boolean isIdentifierRooted(TemplateParser.JinjaPrimaryContext ctx) {
        return ctx.jinjaAtomTrailer() != null && ctx.jinjaAtomTrailer().jinjaAtom().JJ_IDENTIFIER() != null;
    }

    /**
     * Unwraps jinjaOrExpr down to its jinjaFilteredPrimary only if every
     * level in between is a trivial single-child pass-through - i.e. no
     * or/and/not/comparison/+-~/star-slash operator is actually present.
     * Returns null the moment a real operator is found, since the
     * expression is then no longer a "plain variable (+ filters)".
     */
    private TemplateParser.JinjaFilteredPrimaryContext asPlainFilteredPrimary(TemplateParser.JinjaOrExprContext ctx) {
        if (ctx.jinjaAndExpr().size() != 1) {
            return null;
        }
        TemplateParser.JinjaNotExprContext notExpr = ctx.jinjaAndExpr(0).jinjaNotExpr(0);
        if (ctx.jinjaAndExpr(0).jinjaNotExpr().size() != 1 || notExpr.jinjaComparisonExpr() == null) {
            return null;
        }
        TemplateParser.JinjaComparisonExprContext comparisonExpr = notExpr.jinjaComparisonExpr();
        if (comparisonExpr.jinjaAdditiveExpr().size() != 1) {
            return null;
        }
        TemplateParser.JinjaAdditiveExprContext additiveExpr = comparisonExpr.jinjaAdditiveExpr(0);
        if (additiveExpr.jinjaMultiplicativeExpr().size() != 1) {
            return null;
        }
        TemplateParser.JinjaMultiplicativeExprContext multiplicativeExpr = additiveExpr.jinjaMultiplicativeExpr(0);
        if (multiplicativeExpr.jinjaFilteredPrimary().size() != 1) {
            return null;
        }
        return multiplicativeExpr.jinjaFilteredPrimary(0);
    }

    private JinjaFilterNode buildFilter(TemplateParser.JinjaFilterCallContext ctx) {
        String filterName = ctx.JJ_IDENTIFIER().getText();
        String arguments = ctx.jinjaArgList() != null ? originalText(ctx.jinjaArgList()) : null;
        return new JinjaFilterNode(filterName, arguments, lineOf(ctx), columnOf(ctx));
    }

    // ========================================
    // Jinja2 structured expression tree construction
    //
    // Walks the exact same grammar chain as asPlainFilteredPrimary above
    // (or -> and -> not -> comparison -> +-~ -> */ -> filters -> primary
    // -> atom+trailers), but instead of only distinguishing "plain
    // reference" from "everything else", produces real nested nodes for
    // every operator/trailer/filter along the way. This is what populates
    // JinjaExpressionNode#getRoot(), JinjaIfNode/JinjaElifNode's
    // getConditionTree() and JinjaForNode#getIterableTree().
    // ========================================

    private JinjaNode buildJinjaExpressionTree(TemplateParser.JinjaOrExprContext ctx) {
        return buildOrExprTree(ctx);
    }

    private JinjaNode buildOrExprTree(TemplateParser.JinjaOrExprContext ctx) {
        JinjaNode result = buildAndExprTree(ctx.jinjaAndExpr(0));
        for (int i = 1; i < ctx.jinjaAndExpr().size(); i++) {
            JinjaNode right = buildAndExprTree(ctx.jinjaAndExpr(i));
            result = new JinjaBinaryOpNode(result, "or", right, lineOf(ctx), columnOf(ctx));
        }
        return result;
    }

    private JinjaNode buildAndExprTree(TemplateParser.JinjaAndExprContext ctx) {
        JinjaNode result = buildNotExprTree(ctx.jinjaNotExpr(0));
        for (int i = 1; i < ctx.jinjaNotExpr().size(); i++) {
            JinjaNode right = buildNotExprTree(ctx.jinjaNotExpr(i));
            result = new JinjaBinaryOpNode(result, "and", right, lineOf(ctx), columnOf(ctx));
        }
        return result;
    }

    private JinjaNode buildNotExprTree(TemplateParser.JinjaNotExprContext ctx) {
        if (ctx.JJ_NOT() != null) {
            return new JinjaUnaryOpNode("not", buildNotExprTree(ctx.jinjaNotExpr()), lineOf(ctx), columnOf(ctx));
        }
        return buildComparisonExprTree(ctx.jinjaComparisonExpr());
    }

    private JinjaNode buildComparisonExprTree(TemplateParser.JinjaComparisonExprContext ctx) {
        JinjaNode left = buildAdditiveExprTree(ctx.jinjaAdditiveExpr(0));
        if (ctx.jinjaAdditiveExpr().size() == 1) {
            return left;
        }
        JinjaNode right = buildAdditiveExprTree(ctx.jinjaAdditiveExpr(1));
        return new JinjaCompareNode(left, comparisonOperatorText(ctx), right, lineOf(ctx), columnOf(ctx));
    }

    private String comparisonOperatorText(TemplateParser.JinjaComparisonExprContext ctx) {
        if (ctx.JJ_EQ() != null) return "==";
        if (ctx.JJ_NEQ() != null) return "!=";
        if (ctx.JJ_LE() != null) return "<=";
        if (ctx.JJ_GE() != null) return ">=";
        if (ctx.JJ_LT() != null) return "<";
        return ">";
    }

    private JinjaNode buildAdditiveExprTree(TemplateParser.JinjaAdditiveExprContext ctx) {
        JinjaNode result = buildMultiplicativeExprTree(ctx.jinjaMultiplicativeExpr(0));
        for (int i = 1; i < ctx.getChildCount(); i += 2) {
            String operator = ctx.getChild(i).getText();
            JinjaNode right = buildMultiplicativeExprTree(ctx.jinjaMultiplicativeExpr((i + 1) / 2));
            result = new JinjaBinaryOpNode(result, operator, right, lineOf(ctx), columnOf(ctx));
        }
        return result;
    }

    private JinjaNode buildMultiplicativeExprTree(TemplateParser.JinjaMultiplicativeExprContext ctx) {
        JinjaNode result = buildFilteredPrimaryTree(ctx.jinjaFilteredPrimary(0));
        for (int i = 1; i < ctx.getChildCount(); i += 2) {
            String operator = ctx.getChild(i).getText();
            JinjaNode right = buildFilteredPrimaryTree(ctx.jinjaFilteredPrimary((i + 1) / 2));
            result = new JinjaBinaryOpNode(result, operator, right, lineOf(ctx), columnOf(ctx));
        }
        return result;
    }

    private JinjaNode buildFilteredPrimaryTree(TemplateParser.JinjaFilteredPrimaryContext ctx) {
        JinjaNode primary = buildPrimaryTree(ctx.jinjaPrimary());
        if (ctx.jinjaFilterCall().isEmpty()) {
            return primary;
        }
        List<JinjaFilterNode> filters = new ArrayList<>();
        for (TemplateParser.JinjaFilterCallContext filterCallContext : ctx.jinjaFilterCall()) {
            filters.add(buildFilter(filterCallContext));
        }
        return new JinjaFilterApplicationNode(primary, filters, lineOf(ctx), columnOf(ctx));
    }

    private JinjaNode buildPrimaryTree(TemplateParser.JinjaPrimaryContext ctx) {
        if (ctx.JJ_MINUS() != null) {
            return new JinjaUnaryOpNode("-", buildPrimaryTree(ctx.jinjaPrimary()), lineOf(ctx), columnOf(ctx));
        }
        if (ctx.jinjaAtomTrailer() != null) {
            return buildAtomTrailerTree(ctx.jinjaAtomTrailer());
        }
        // Parenthesized sub-expression: "(" jinjaOrExpr ")" is transparent grouping.
        return buildOrExprTree(ctx.jinjaOrExpr());
    }

    private JinjaNode buildAtomTrailerTree(TemplateParser.JinjaAtomTrailerContext ctx) {
        JinjaNode result = buildAtomTree(ctx.jinjaAtom());
        for (TemplateParser.JinjaTrailerContext trailerContext : ctx.jinjaTrailer()) {
            if (trailerContext.JJ_DOT() != null) {
                result = new JinjaAttributeAccessNode(result, trailerContext.JJ_IDENTIFIER().getText(),
                        lineOf(trailerContext), columnOf(trailerContext));
            } else if (trailerContext.JJ_LBRACKET() != null) {
                JinjaNode index = buildOrExprTree(trailerContext.jinjaOrExpr());
                result = new JinjaSubscriptNode(result, index, lineOf(trailerContext), columnOf(trailerContext));
            } else {
                List<JinjaNode> arguments = new ArrayList<>();
                if (trailerContext.jinjaArgList() != null) {
                    for (TemplateParser.JinjaOrExprContext argContext : trailerContext.jinjaArgList().jinjaOrExpr()) {
                        arguments.add(buildOrExprTree(argContext));
                    }
                }
                result = new JinjaCallNode(result, arguments, lineOf(trailerContext), columnOf(trailerContext));
            }
        }
        return result;
    }

    private JinjaNode buildAtomTree(TemplateParser.JinjaAtomContext ctx) {
        if (ctx.JJ_IDENTIFIER() != null) {
            return new JinjaIdentifierNode(ctx.JJ_IDENTIFIER().getText(), lineOf(ctx), columnOf(ctx));
        }
        if (ctx.JJ_NUMBER() != null) {
            String text = ctx.JJ_NUMBER().getText();
            Object value = text.contains(".") ? (Object) Double.parseDouble(text) : (Object) Integer.parseInt(text);
            return new LiteralNode(value, lineOf(ctx), columnOf(ctx));
        }
        if (ctx.JJ_STRING() != null) {
            return new LiteralNode(stripQuotes(ctx.JJ_STRING().getText()), lineOf(ctx), columnOf(ctx));
        }
        if (ctx.JJ_TRUE() != null) {
            return new LiteralNode(Boolean.TRUE, lineOf(ctx), columnOf(ctx));
        }
        if (ctx.JJ_FALSE() != null) {
            return new LiteralNode(Boolean.FALSE, lineOf(ctx), columnOf(ctx));
        }
        // Only JJ_NONE is left.
        return new LiteralNode(null, lineOf(ctx), columnOf(ctx));
    }

    // ========================================
    // Utility Methods
    // ========================================

    /**
     * Extract content from Jinja2 delimiters.
     */
    private String extractJinjaContent(ParserRuleContext ctx) {
        String text = ctx.getText();
        if (text == null) {
            return "";
        }
        
        text = text.trim();
        
        // Remove {{ }}, {% %}, or {# #}
        if (text.startsWith("{{") && text.endsWith("}}")) {
            return text.substring(2, text.length() - 2).trim();
        } else if (text.startsWith("{%") && text.endsWith("%}")) {
            return text.substring(2, text.length() - 2).trim();
        } else if (text.startsWith("{#") && text.endsWith("#}")) {
            return text.substring(2, text.length() - 2).trim();
        }
        
        return text;
    }

    /**
     * Reconstructs the exact original source text spanned by ctx, including
     * whitespace - unlike ParserRuleContext.getText(), which concatenates
     * token texts with nothing in between and would silently collapse
     * "a + b" into "a+b". This keeps the strings fed to the Generator's
     * condition/expression evaluators identical in shape to what they
     * always expected.
     */
    private String originalText(ParserRuleContext ctx) {
        int start = ctx.getStart().getStartIndex();
        int stop = ctx.getStop().getStopIndex();
        if (stop < start) {
            return "";
        }
        return ctx.getStart().getInputStream().getText(org.antlr.v4.runtime.misc.Interval.of(start, stop));
    }

    private int lineOf(ParserRuleContext ctx) {
        return ctx.getStart().getLine();
    }

    private int columnOf(ParserRuleContext ctx) {
        return ctx.getStart().getCharPositionInLine();
    }

    /**
     * Strip quotes from a quoted string.
     */
    private String stripQuotes(String text) {
        if (text == null) {
            return null;
        }
        
        String trimmed = text.trim();
        
        if ((trimmed.startsWith("\"") && trimmed.endsWith("\"")) || 
            (trimmed.startsWith("'") && trimmed.endsWith("'"))) {
            return trimmed.substring(1, trimmed.length() - 1);
        }
        
        return trimmed;
    }

    @Override
    protected Object defaultResult() {
        return null;
    }

    @Override
    protected Object aggregateResult(Object aggregate, Object nextResult) {
        return nextResult != null ? nextResult : aggregate;
    }

    private String ruleName(ParserRuleContext ctx) {
        String simpleName = ctx.getClass().getSimpleName();
        return simpleName.endsWith("Context") ? simpleName.substring(0, simpleName.length() - 7) : simpleName;
    }
}
