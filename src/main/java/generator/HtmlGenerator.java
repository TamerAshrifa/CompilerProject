package generator;

import template.ast.TemplateNode;
import template.ast.TemplateProgramNode;
import template.ast.TemplateRuleNode;
import template.ast.html.HtmlAttributeNode;
import template.ast.html.HtmlCommentNode;
import template.ast.html.HtmlElementNode;
import template.ast.html.HtmlNode;
import template.ast.html.HtmlTextNode;
import template.ast.html.JinjaHostNode;
import template.ast.html.StyleElementNode;
import template.ast.jinja.JinjaNode;
import template.visitor.TemplateBaseVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Regenerates HTML source text from the existing HTML AST
 * ({@code template.ast.html.*}).
 *
 * <p>Extends {@link TemplateBaseVisitor} and overrides only the five
 * HTML-related {@code visitXxx} methods plus the shared {@code visitProgram}/
 * {@code visitRule} roots; Jinja/CSS-as-top-level methods are left to their
 * inherited defaults. {@code <style>} content is delegated to a private
 * {@link CssGenerator} instance — see {@link #visitStyleElement} — so CSS
 * generation logic lives in exactly one place and is reused rather than
 * duplicated, matching how {@link StyleElementNode} itself bridges the two
 * independent trees.
 *
 * <p>As documented on {@link template.ast.TemplateProgramNode}, the HTML and
 * Jinja2 trees are still built independently for semantic-analysis purposes:
 * a Jinja construct that textually sat inside an HTML tag's content is
 * hoisted out to the program's top-level Jinja list while parsing (see
 * {@code TemplateASTBuilder}'s {@code hoistedJinjaNodes}). Unlike before,
 * though, a {@link JinjaHostNode} placeholder is left behind at that exact
 * position among the surrounding {@link HtmlNode} children (see that class),
 * so this generator - via {@link #visitJinjaHostNode} - CAN re-interleave
 * resolved Jinja output back into its original position: {@link
 * #withResolvedReplacements} accepts the replacement map {@link Generator}
 * produces while resolving the Jinja tree, keyed by the original hoisted
 * node, and a placeholder is rendered by looking up and inlining whatever
 * that node resolved to. Without a replacement map (i.e. when only {@link
 * CodeGenerator#generate()}'s unresolved path ran), a placeholder falls back
 * to printing the construct's own regenerated Jinja2 source right there -
 * still an improvement on silently dropping it, and valid Jinja2 either way.
 *
 * <p><b>Known, honest limitation</b> (inherited from the existing parser,
 * confirmed by inspecting its output directly rather than assumed): the
 * grammar has no built-in notion of HTML5 "void" elements ({@code <meta>},
 * {@code <img>}, {@code <br>}, ...) — a tag is only treated as
 * self-closing when the source explicitly writes {@code />}.
 * {@code <meta charset="utf-8">} without a trailing slash therefore parses
 * as an ordinary opening tag still waiting for a {@code </meta>}, and
 * everything that follows in the source becomes nested under it until one
 * is found (or never is). This class's own {@code VOID_ELEMENTS} handling
 * only controls how <em>this class</em> prints a void element it is given
 * (never emitting a spurious closing tag for one) — it cannot undo mis-nesting
 * that already happened one layer down, before this class ever sees the
 * tree. Template source feeding this generator should self-close void
 * elements explicitly ({@code <meta ... />}) to get the tree its author
 * intended.
 *
 * <p>Similarly, {@code TemplateASTBuilder.visitAttribute} always constructs
 * {@link HtmlAttributeNode} through its {@code (name, value, line, column)}
 * constructor — even for a bare/boolean attribute like {@code hidden}, passed
 * as {@code value=null} — and that constructor unconditionally sets
 * {@code hasValue()} to {@code true}. So for any attribute built by the real
 * parser, {@code hasValue()} cannot actually be trusted to mean "
 * {@code getValue()} is non-null"; see {@link #visitHtmlAttribute} for how
 * this class checks {@code getValue() == null} directly instead, which is
 * correct regardless of this upstream inconsistency and requires no change
 * to the existing AST classes.
 */
public class HtmlGenerator extends TemplateBaseVisitor<String> {

    /** HTML5 elements that never have a closing tag or children, per the spec, regardless of {@code isSelfClosing()}. */
    private static final Set<String> VOID_ELEMENTS = new HashSet<>(Arrays.asList(
            "area", "base", "br", "col", "embed", "hr", "img", "input",
            "link", "meta", "param", "source", "track", "wbr"));

    private final GenerationSupport support = new GenerationSupport("  ");
    private final CssGenerator cssGenerator = new CssGenerator();
    private final JinjaGenerator jinjaGenerator = new JinjaGenerator();
    private Map<JinjaNode, List<JinjaNode>> resolvedReplacements;

    /**
     * Supplies the (original hoisted node &rarr; resolved replacement)
     * map produced by resolving the Jinja tree - see {@link
     * Generator#getResolvedReplacements()} - so {@link #visitJinjaHostNode}
     * can inline each {@link JinjaHostNode} placeholder's actual resolved
     * content instead of falling back to raw Jinja2 source. Optional: a
     * generator with no map supplied still produces correct, valid output,
     * just with unresolved {@code {{ ... }}}/{@code {% ... %}} syntax at
     * each placeholder instead of a resolved value.
     *
     * @return this instance, for chaining
     */
    public HtmlGenerator withResolvedReplacements(Map<JinjaNode, List<JinjaNode>> resolvedReplacements) {
        this.resolvedReplacements = resolvedReplacements;
        return this;
    }

    /** Generates HTML text for every top-level HTML element in a parsed template. */
    public String generate(TemplateProgramNode program) {
        support.reset();
        return program.accept(this);
    }

    /** The recorded (node name, source line/column, emission order) trail from the last {@link #generate}. */
    public List<SourceMapping> getSourceMap() {
        return support.getSourceMap();
    }

    /** The CSS generator used internally for {@code <style>} content, exposed so its own source map is inspectable too. */
    public CssGenerator getCssGenerator() {
        return cssGenerator;
    }

    @Override
    public String visitProgram(TemplateProgramNode node) {
        return renderHtmlNodes(significantChildren(node.getHtmlElements()));
    }

    @Override
    public String visitRule(TemplateRuleNode node) {
        // See JinjaGenerator#visitRule: defensive handling of the generic
        // escape-hatch node, not expected from well-formed parser output.
        StringBuilder sb = new StringBuilder();
        List<TemplateNode> children = node.getChildren();
        for (int i = 0; i < children.size(); i++) {
            if (i > 0) {
                sb.append('\n');
            }
            sb.append(children.get(i).accept(this));
        }
        return sb.toString();
    }

    @Override
    public String visitHtmlElement(HtmlElementNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        String tagName = node.getTagName();
        StringBuilder sb = new StringBuilder(support.indent()).append('<').append(tagName);
        for (HtmlAttributeNode attribute : node.getAttributes()) {
            sb.append(' ').append(attribute.accept(this));
        }

        boolean isVoid = node.isSelfClosing() || VOID_ELEMENTS.contains(tagName.toLowerCase());
        if (isVoid) {
            return sb.append(" />").toString();
        }

        // Whitespace-only text nodes here are almost always source
        // indentation between sibling tags rather than meaningful content
        // (e.g. the newline-and-spaces between "<head>" and "<title>"); this
        // class re-indents its own output regardless, so keeping them would
        // only compound into visual noise. Non-blank text is always kept,
        // verbatim, exactly like visitHtmlText on its own.
        List<HtmlNode> children = significantChildren(node.getChildren());
        if (children.isEmpty()) {
            return sb.append("></").append(tagName).append('>').toString();
        }
        if (isInlineFlowContent(children)) {
            // Every child is text and/or a resolved Jinja value (e.g.
            // "Hello, " + {{ name }} in <h1>Hello, {{ name }}</h1>), i.e.
            // there is no nested element tag - so the whole thing reads as
            // one inline unit, same as the plain-text case always has.
            sb.append('>');
            for (HtmlNode child : children) {
                sb.append(child.accept(this));
            }
            return sb.append("</").append(tagName).append('>').toString();
        }

        sb.append(">\n");
        support.increaseIndent();
        for (int i = 0; i < children.size(); i++) {
            if (i > 0) {
                sb.append('\n');
            }
            sb.append(children.get(i).accept(this));
        }
        support.decreaseIndent();
        sb.append('\n').append(support.indent()).append("</").append(tagName).append('>');
        return sb.toString();
    }

    @Override
    public String visitHtmlAttribute(HtmlAttributeNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        // Deliberately checked as "getValue() == null" rather than trusting
        // "!node.hasValue()" alone: TemplateASTBuilder.visitAttribute always
        // calls the (name, value, line, column) constructor — even for a
        // bare/boolean attribute like "hidden", where it passes value=null —
        // and that constructor unconditionally sets hasValue=true. So
        // hasValue() alone cannot be relied on to mean "getValue() is
        // non-null" for parser-produced attributes; this class stays correct
        // either way without needing any change to the existing AST classes.
        if (node.getValue() == null) {
            return node.getName();
        }
        return node.getName() + "=\"" + escapeAttributeValue(node.getValue()) + "\"";
    }

    @Override
    public String visitHtmlText(HtmlTextNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        // Text nodes hold exactly the source text that appeared between
        // tags, already in whatever HTML-escaped-or-not form it was written
        // in; re-emitting it verbatim (rather than re-escaping) is what
        // reproduces the original content rather than double-escaping it.
        return node.getContent();
    }

    @Override
    public String visitHtmlComment(HtmlCommentNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return support.indent() + "<!--" + node.getContent() + "-->";
    }

    @Override
    public String visitJinjaHostNode(JinjaHostNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        JinjaNode hosted = node.getHostedNode();
        List<JinjaNode> replacement = resolvedReplacements != null ? resolvedReplacements.get(hosted) : null;
        if (replacement == null) {
            // No resolution map (or this specific node wasn't in it): fall
            // back to the construct's own regenerated Jinja2 source, so the
            // placeholder still round-trips to valid, in-place output.
            return hosted.accept(jinjaGenerator);
        }
        StringBuilder sb = new StringBuilder();
        for (JinjaNode replacementNode : replacement) {
            // A LiteralNode here is Generator's fully-resolved substitute for
            // what was originally a {{ ... }} unit and renders bare (see
            // JinjaGenerator#visitJinjaLiteral); anything Generator could not
            // fully resolve still needs real Jinja2 source syntax - either
            // way, accept(jinjaGenerator) renders the right thing.
            sb.append(replacementNode.accept(jinjaGenerator));
        }
        return sb.toString();
    }

    @Override
    public String visitStyleElement(StyleElementNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        String css = node.getStylesheet().accept(cssGenerator);
        if (css.isEmpty()) {
            return support.indent() + "<style></style>";
        }
        StringBuilder sb = new StringBuilder(support.indent()).append("<style>\n");
        // CssGenerator indents its own output relative to itself (starting a
        // fresh stylesheet at its own indent level 0), with no way to know
        // how deeply this <style> tag happens to sit inside the HTML tree;
        // reindenting each of its lines by this generator's current level
        // keeps the CSS visually aligned with the tag that contains it.
        sb.append(reindent(css, support.indent()));
        sb.append('\n').append(support.indent()).append("</style>");
        return sb.toString();
    }

    /** Prefixes every non-blank line of {@code text} with {@code prefix}, leaving blank lines empty. */
    private String reindent(String text, String prefix) {
        String[] lines = text.split("\n", -1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                sb.append('\n');
            }
            if (!lines[i].isEmpty()) {
                sb.append(prefix);
            }
            sb.append(lines[i]);
        }
        return sb.toString();
    }

    private String renderHtmlNodes(List<HtmlNode> nodes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nodes.size(); i++) {
            if (i > 0) {
                sb.append('\n');
            }
            sb.append(nodes.get(i).accept(this));
        }
        return sb.toString();
    }

    /** Drops whitespace-only text nodes; see the call site in {@link #visitHtmlElement} for why. */
    private List<HtmlNode> significantChildren(List<HtmlNode> children) {
        List<HtmlNode> result = new ArrayList<>(children.size());
        for (HtmlNode child : children) {
            if (child instanceof HtmlTextNode && ((HtmlTextNode) child).getContent().trim().isEmpty()) {
                continue;
            }
            result.add(child);
        }
        return result;
    }

    /**
     * True when every child is an {@link HtmlTextNode} and/or a {@link
     * JinjaHostNode} - i.e. plain text possibly interleaved with resolved
     * Jinja values, but no nested element/comment/style tag - so the whole
     * sequence reads naturally as one inline unit (see the call site).
     */
    private boolean isInlineFlowContent(List<HtmlNode> children) {
        for (HtmlNode child : children) {
            if (!(child instanceof HtmlTextNode) && !(child instanceof JinjaHostNode)) {
                return false;
            }
        }
        return true;
    }

    private String escapeAttributeValue(String value) {
        // Consistent with visitHtmlText: the stored value is raw source text
        // (quotes already stripped by the parser, nothing else decoded), so
        // it is reproduced as-is except for the one character that would
        // otherwise break out of the double-quoted attribute syntax this
        // class chooses to always emit. Unconditionally escaping "&" too
        // would double-escape any entity already present in the source
        // (e.g. turning an existing "&amp;" into "&amp;amp;").
        return value.replace("\"", "&quot;");
    }
}
