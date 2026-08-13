package generator;

import template.ast.css.CssAtRuleNode;
import template.ast.css.CssDeclarationNode;
import template.ast.css.CssRuleNode;
import template.ast.css.CssStyleRuleNode;
import template.ast.css.CssStylesheetNode;
import template.visitor.TemplateBaseVisitor;

import java.util.List;

/**
 * Regenerates CSS source text from the existing CSS AST
 * ({@code template.ast.css.*}), the content of an HTML {@code <style>}
 * element.
 *
 * <p>Extends {@link TemplateBaseVisitor} (rather than implementing
 * {@link template.visitor.TemplateVisitor} directly) to reuse the project's
 * existing default behavior for the ~35 HTML/Jinja/legacy node types this
 * class has no opinion about — it overrides only the four CSS-related
 * {@code visitXxx} methods. Every CSS node still generates itself through
 * the same {@code node.accept(this)} double dispatch as everything else in
 * this package; there is no manual type-switch here.
 *
 * <p>Used standalone (see {@link #generate(CssStylesheetNode)}) or bridged
 * in from {@link HtmlGenerator} when it encounters a {@code <style>} tag —
 * see {@code HtmlGenerator.visitStyleElement}.
 */
public class CssGenerator extends TemplateBaseVisitor<String> {

    private final GenerationSupport support = new GenerationSupport("  ");

    /** Generates CSS text for a full stylesheet (the content of a {@code <style>} tag). */
    public String generate(CssStylesheetNode stylesheet) {
        support.reset();
        return stylesheet.accept(this);
    }

    /** The recorded (node name, source line/column, emission order) trail from the last {@link #generate}. */
    public List<SourceMapping> getSourceMap() {
        return support.getSourceMap();
    }

    @Override
    public String visitCssStylesheet(CssStylesheetNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        StringBuilder sb = new StringBuilder();
        List<CssRuleNode> rules = node.getRules();
        for (int i = 0; i < rules.size(); i++) {
            if (i > 0) {
                sb.append("\n\n");
            }
            sb.append(rules.get(i).accept(this));
        }
        return sb.toString();
    }

    @Override
    public String visitCssStyleRule(CssStyleRuleNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        StringBuilder sb = new StringBuilder();
        sb.append(support.indent()).append(String.join(", ", node.getSelectors())).append(" {\n");
        sb.append(renderDeclarations(node.getDeclarations()));
        sb.append('\n').append(support.indent()).append('}');
        return sb.toString();
    }

    @Override
    public String visitCssAtRule(CssAtRuleNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        StringBuilder sb = new StringBuilder();
        sb.append(support.indent()).append('@').append(node.getName());
        if (node.getPrelude() != null && !node.getPrelude().isEmpty()) {
            sb.append(' ').append(node.getPrelude());
        }
        if (!node.hasBlock()) {
            return sb.append(';').toString();
        }
        sb.append(" {\n");
        sb.append(renderDeclarations(node.getDeclarations()));
        List<CssRuleNode> nested = node.getNestedRules();
        if (!nested.isEmpty()) {
            if (!node.getDeclarations().isEmpty()) {
                sb.append('\n');
            }
            support.increaseIndent();
            for (int i = 0; i < nested.size(); i++) {
                if (i > 0) {
                    sb.append("\n\n");
                }
                sb.append(nested.get(i).accept(this));
            }
            support.decreaseIndent();
        }
        sb.append('\n').append(support.indent()).append('}');
        return sb.toString();
    }

    @Override
    public String visitCssDeclaration(CssDeclarationNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        String text = support.indent() + node.getProperty() + ": " + node.getValue();
        if (node.isImportant()) {
            text += " !important";
        }
        return text + ";";
    }

    private String renderDeclarations(List<CssDeclarationNode> declarations) {
        support.increaseIndent();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < declarations.size(); i++) {
            if (i > 0) {
                sb.append('\n');
            }
            sb.append(declarations.get(i).accept(this));
        }
        support.decreaseIndent();
        return sb.toString();
    }
}
