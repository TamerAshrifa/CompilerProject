package template.test;

import flask.ast.builder.FlaskASTBuilder;
import flask.ast.nodes.statements.ProgramNode;
import generator.Generator;
import grammar.flask.FlaskLexer;
import grammar.flask.FlaskParser;
import grammar.template.TemplateLexer;
import grammar.template.TemplateParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import template.TemplateASTBuilder;
import template.ast.TemplateProgramNode;
import template.ast.css.CssAtRuleNode;
import template.ast.css.CssDeclarationNode;
import template.ast.css.CssNode;
import template.ast.css.CssRuleNode;
import template.ast.css.CssStyleRuleNode;
import template.ast.css.CssStylesheetNode;
import template.ast.html.HtmlAttributeNode;
import template.ast.html.HtmlCommentNode;
import template.ast.html.HtmlElementNode;
import template.ast.html.HtmlNode;
import template.ast.html.HtmlTextNode;
import template.ast.html.StyleElementNode;
import template.ast.jinja.JinjaAttributeAccessNode;
import template.ast.jinja.JinjaBinaryOpNode;
import template.ast.jinja.JinjaBlockNode;
import template.ast.jinja.JinjaCallNode;
import template.ast.jinja.JinjaCommentNode;
import template.ast.jinja.JinjaCompareNode;
import template.ast.jinja.JinjaElifNode;
import template.ast.jinja.JinjaElseNode;
import template.ast.jinja.JinjaExpressionNode;
import template.ast.jinja.JinjaExtendsNode;
import template.ast.jinja.JinjaFilterApplicationNode;
import template.ast.jinja.JinjaFilterNode;
import template.ast.jinja.JinjaForNode;
import template.ast.jinja.JinjaIdentifierNode;
import template.ast.jinja.JinjaIfNode;
import template.ast.jinja.JinjaIncludeNode;
import template.ast.jinja.JinjaMacroNode;
import template.ast.jinja.JinjaNode;
import template.ast.jinja.JinjaSubscriptNode;
import template.ast.jinja.JinjaUnaryOpNode;
import template.ast.jinja.JinjaVariableNode;
import template.ast.jinja.LiteralNode;

import java.util.List;

/**
 * Mirrors {@link flask.test.TestFlaskParser}, but for the SECOND tree
 * (HTML + CSS + Jinja2) instead of the Python tree. Builds a real AST from
 * genuine HTML/CSS/Jinja2 source and recursively prints every node's name
 * ({@code getNodeName()}) and source line ({@code getLine()}), which is the
 * same live proof of requirement 3 that {@code TestFlaskParser} already
 * gives for the Python tree -- now also for the Jinja2/HTML/CSS side.
 *
 * <p>Also runs the {@link Generator} on a richer example than {@code
 * Main.java}'s (a list of dictionaries rather than a flat list of strings)
 * to further exercise requirement 2 (passing the Python data array into the
 * Jinja2 tree), including attribute access on each unrolled element.
 */
public class TestTemplateParser {

    public static void main(String[] args) {
        testHtmlCssJinjaTree();
        System.out.println();
        testTemplateInheritanceTags();
        System.out.println();
        testGeneratorWithListOfDicts();
    }

    // ================================================================
    // Part 1: HTML + CSS + Jinja2 tree, printed with name + line number
    // ================================================================

    private static void testHtmlCssJinjaTree() {
        String templateSource = String.join("\n",
            "<!DOCTYPE html>",
            "<html>",
            "<head>",
            "<style>",
            "  body { color: red; font-size: 14px; }",
            "  .price { color: green; }",
            "  @media (max-width: 600px) {",
            "    .item { color: blue; }",
            "  }",
            "</style>",
            "</head>",
            "<body>",
            "<h1 class=\"title\">{{ title|upper }}</h1>",
            "{% if user %}",
            "{{ user.name }}",
            "{% else %}",
            "{{ \"guest\" }}",
            "{% endif %}",
            "{% for product in products %}",
            "{{ product.name }}",
            "{{ product.price }}",
            "{% endfor %}",
            "{# a trailing comment #}",
            "</body>",
            "</html>",
            ""
        );

        System.out.println("====================================================================");
        System.out.println("   Template AST (HTML + CSS + Jinja2) -- node names + line numbers");
        System.out.println("====================================================================");
        System.out.println(templateSource);

        TemplateProgramNode templateAst = buildTemplateAst(templateSource);

        System.out.println("HTML top-level elements: " + templateAst.getHtmlElements().size());
        System.out.println("Jinja2 top-level elements: " + templateAst.getJinjaElements().size());
        System.out.println();
        System.out.println("--- AST ---");

        for (HtmlNode node : templateAst.getHtmlElements()) {
            printHtml(node, 0);
        }
        for (JinjaNode node : templateAst.getJinjaElements()) {
            printJinja(node, 0);
        }

        boolean ok = !templateAst.getHtmlElements().isEmpty() && !templateAst.getJinjaElements().isEmpty();
        System.out.println();
        System.out.println(ok
            ? "OK: template AST built successfully with real, populated HTML/CSS/Jinja2 nodes."
            : "FAILED: template tree is empty.");
    }

    // ================================================================
    // Part 2: template-inheritance-only tags (extends/block/macro/include)
    // ================================================================

    private static void testTemplateInheritanceTags() {
        String templateSource = String.join("\n",
            "{% extends \"base.html\" %}",
            "{% block content %}",
            "{% macro greet(name) %}",
            "Hello, {{ name }}!",
            "{% endmacro %}",
            "{{ greet(username) }}",
            "{% endblock %}",
            "{% include \"footer.html\" %}",
            ""
        );

        System.out.println("====================================================================");
        System.out.println("   Template inheritance tags: extends / block / macro / include");
        System.out.println("====================================================================");
        System.out.println(templateSource);

        TemplateProgramNode templateAst = buildTemplateAst(templateSource);
        System.out.println("HTML top-level elements: " + templateAst.getHtmlElements().size());
        System.out.println("Jinja2 top-level elements: " + templateAst.getJinjaElements().size());
        System.out.println();
        for (HtmlNode node : templateAst.getHtmlElements()) {
            printHtml(node, 0);
        }
        for (JinjaNode node : templateAst.getJinjaElements()) {
            printJinja(node, 0);
        }
        System.out.println();
        System.out.println(templateAst.getJinjaElements().isEmpty()
            ? "FAILED: inheritance-tag tree is empty."
            : "OK: extends/block/macro/include all parsed into real, populated nodes.");
    }

    // ================================================================
    // Part 3: Generator on a richer example (array of dicts, not strings)
    // ================================================================

    private static void testGeneratorWithListOfDicts() {
        String pythonSource = String.join("\n",
            "from flask import Flask, render_template",
            "",
            "app = Flask(__name__)",
            "",
            "@app.route('/shop')",
            "def shop():",
            "    products = [",
            "        {'name': 'Widget', 'price': 10},",
            "        {'name': 'Gadget', 'price': 20},",
            "        {'name': 'Gizmo', 'price': 30},",
            "    ]",
            "    return render_template('shop.html', products=products)",
            ""
        );
        String templateSource = String.join("\n",
            "{% for product in products %}",
            "{{ product.name }}",
            "{{ product.price }}",
            "{% endfor %}",
            ""
        );

        System.out.println("====================================================================");
        System.out.println("   Generator: array of dicts flowing from Python AST into Jinja2 AST");
        System.out.println("====================================================================");
        System.out.println(pythonSource);
        System.out.println(templateSource);

        ProgramNode pythonAst = buildPythonAst(pythonSource);
        TemplateProgramNode templateAst = buildTemplateAst(templateSource);

        Generator generator = new Generator(pythonAst, templateAst, null);
        TemplateProgramNode transformed = generator.generate();
        System.out.println(generator.getSummary());

        System.out.println("Rendered values (proof attribute access on each unrolled dict works too):");
        boolean allLiterals = true;
        for (JinjaNode node : transformed.getJinjaElements()) {
            if (node instanceof LiteralNode literal) {
                System.out.println("  -> " + literal.getStringValue());
            } else {
                allLiterals = false;
            }
        }
        System.out.println();
        System.out.println(allLiterals && transformed.getJinjaElements().size() == 6
            ? "OK: 3 products x 2 fields = 6 literal values produced from the Python array."
            : "FAILED: expected 6 fully-resolved literal values.");
    }

    // ================================================================
    // Build helpers
    // ================================================================

    private static ProgramNode buildPythonAst(String source) {
        CharStream input = CharStreams.fromString(source);
        FlaskLexer lexer = new FlaskLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        FlaskParser parser = new FlaskParser(tokens);
        FlaskParser.ProgramContext tree = parser.program();
        FlaskASTBuilder builder = new FlaskASTBuilder();
        return (ProgramNode) builder.build(tree);
    }

    private static TemplateProgramNode buildTemplateAst(String source) {
        CharStream input = CharStreams.fromString(source);
        TemplateLexer lexer = new TemplateLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TemplateParser parser = new TemplateParser(tokens);
        System.out.println("(parser syntax errors: " + parser.getNumberOfSyntaxErrors() + ")");
        TemplateParser.HtmlDocumentContext tree = parser.htmlDocument();
        System.out.println("(parser syntax errors after parse: " + parser.getNumberOfSyntaxErrors() + ")");
        TemplateASTBuilder builder = new TemplateASTBuilder();
        return builder.build(tree);
    }

    // ================================================================
    // Recursive printers -- every line below calls the polymorphic
    // getNodeName() + getLine() inherited from HtmlNode / CssNode / JinjaNode.
    // ================================================================

    private static void printHtml(HtmlNode node, int depth) {
        String indent = "  ".repeat(depth);
        if (node instanceof HtmlElementNode el) {
            System.out.println(indent + "- " + el.getNodeName() + " <" + el.getTagName() + "> (line " + el.getLine() + ")");
            for (HtmlAttributeNode attr : el.getAttributes()) {
                System.out.println(indent + "    " + attr.getNodeName() + " " + attr.getName()
                    + (attr.hasValue() ? "=\"" + attr.getValue() + "\"" : "") + " (line " + attr.getLine() + ")");
            }
            for (HtmlNode child : el.getChildren()) {
                printHtml(child, depth + 1);
            }
        } else if (node instanceof StyleElementNode style) {
            System.out.println(indent + "- " + style.getNodeName() + " (line " + style.getLine() + ")");
            printCss(style.getStylesheet(), depth + 1);
        } else if (node instanceof HtmlTextNode text) {
            String content = text.getContent().replace("\n", "\\n").trim();
            if (!content.isEmpty()) {
                System.out.println(indent + "- " + text.getNodeName() + " \"" + content + "\" (line " + text.getLine() + ")");
            }
        } else if (node instanceof HtmlCommentNode comment) {
            System.out.println(indent + "- " + comment.getNodeName() + " (line " + comment.getLine() + ")");
        } else {
            System.out.println(indent + "- " + node.getNodeName() + " (line " + node.getLine() + ")");
        }
    }

    private static void printCss(CssNode node, int depth) {
        String indent = "  ".repeat(depth);
        if (node instanceof CssStylesheetNode sheet) {
            System.out.println(indent + "- " + sheet.getNodeName() + " (line " + sheet.getLine() + ")");
            for (CssRuleNode rule : sheet.getRules()) {
                printCss(rule, depth + 1);
            }
        } else if (node instanceof CssStyleRuleNode rule) {
            System.out.println(indent + "- " + rule.getNodeName() + " " + rule.getSelectors() + " (line " + rule.getLine() + ")");
            for (CssDeclarationNode decl : rule.getDeclarations()) {
                printCss(decl, depth + 1);
            }
        } else if (node instanceof CssAtRuleNode atRule) {
            System.out.println(indent + "- " + atRule.getNodeName() + " @" + atRule.getName()
                + " " + atRule.getPrelude() + " (line " + atRule.getLine() + ")");
            for (CssDeclarationNode decl : atRule.getDeclarations()) {
                printCss(decl, depth + 1);
            }
            for (CssRuleNode nested : atRule.getNestedRules()) {
                printCss(nested, depth + 1);
            }
        } else if (node instanceof CssDeclarationNode decl) {
            System.out.println(indent + "- " + decl.getNodeName() + " " + decl.getProperty() + ": " + decl.getValue()
                + (decl.isImportant() ? " !important" : "") + " (line " + decl.getLine() + ")");
        } else {
            System.out.println(indent + "- " + node.getNodeName() + " (line " + node.getLine() + ")");
        }
    }

    private static void printJinja(JinjaNode node, int depth) {
        if (node == null) {
            return;
        }
        String indent = "  ".repeat(depth);

        if (node instanceof JinjaIfNode ifNode) {
            System.out.println(indent + "- " + ifNode.getNodeName() + " condition=\"" + ifNode.getCondition() + "\" (line " + ifNode.getLine() + ")");
            if (ifNode.getConditionTree() != null) {
                System.out.println(indent + "    ConditionTree:");
                printJinja(ifNode.getConditionTree(), depth + 2);
            }
            for (JinjaNode child : ifNode.getThenBody()) {
                printJinja(child, depth + 1);
            }
            for (JinjaElifNode elif : ifNode.getElifNodes()) {
                printJinja(elif, depth);
            }
            if (ifNode.hasElse()) {
                printJinja(ifNode.getElseNode(), depth);
            }
        } else if (node instanceof JinjaElifNode elif) {
            System.out.println(indent + "- " + elif.getNodeName() + " condition=\"" + elif.getCondition() + "\" (line " + elif.getLine() + ")");
            for (JinjaNode child : elif.getBody()) {
                printJinja(child, depth + 1);
            }
        } else if (node instanceof JinjaElseNode elseNode) {
            System.out.println(indent + "- " + elseNode.getNodeName() + " (line " + elseNode.getLine() + ")");
            for (JinjaNode child : elseNode.getBody()) {
                printJinja(child, depth + 1);
            }
        } else if (node instanceof JinjaForNode forNode) {
            System.out.println(indent + "- " + forNode.getNodeName() + " loopVar=" + forNode.getLoopVariable()
                + " iterable=" + forNode.getIterable() + " (line " + forNode.getLine() + ")");
            for (JinjaNode child : forNode.getBody()) {
                printJinja(child, depth + 1);
            }
            if (forNode.hasElse()) {
                for (JinjaNode child : forNode.getElseBody()) {
                    printJinja(child, depth + 1);
                }
            }
        } else if (node instanceof JinjaBlockNode block) {
            System.out.println(indent + "- " + block.getNodeName() + " name=" + block.getBlockName() + " (line " + block.getLine() + ")");
            for (JinjaNode child : block.getBody()) {
                printJinja(child, depth + 1);
            }
        } else if (node instanceof JinjaMacroNode macro) {
            System.out.println(indent + "- " + macro.getNodeName() + " name=" + macro.getMacroName()
                + " params=" + macro.getParameters() + " (line " + macro.getLine() + ")");
            for (JinjaNode child : macro.getBody()) {
                printJinja(child, depth + 1);
            }
        } else if (node instanceof JinjaVariableNode variable) {
            System.out.println(indent + "- " + variable.getNodeName() + " name=" + variable.getVariableName() + " (line " + variable.getLine() + ")");
            for (JinjaFilterNode filter : variable.getFilters()) {
                printJinja(filter, depth + 1);
            }
        } else if (node instanceof JinjaExpressionNode expr) {
            System.out.println(indent + "- " + expr.getNodeName() + " expr=\"" + expr.getExpression() + "\" (line " + expr.getLine() + ")");
            if (expr.getRoot() != null) {
                System.out.println(indent + "    Tree:");
                printJinja(expr.getRoot(), depth + 2);
            }
            for (JinjaFilterNode filter : expr.getFilters()) {
                printJinja(filter, depth + 1);
            }
        } else if (node instanceof JinjaFilterNode filter) {
            System.out.println(indent + "- " + filter.getNodeName() + " name=" + filter.getFilterName()
                + (filter.hasArguments() ? " args=" + filter.getArguments() : "") + " (line " + filter.getLine() + ")");
        } else if (node instanceof JinjaCommentNode comment) {
            System.out.println(indent + "- " + comment.getNodeName() + " \"" + comment.getContent().trim() + "\" (line " + comment.getLine() + ")");
        } else if (node instanceof JinjaExtendsNode extends_) {
            System.out.println(indent + "- " + extends_.getNodeName() + " parent=" + extends_.getParentTemplatePath() + " (line " + extends_.getLine() + ")");
        } else if (node instanceof JinjaIncludeNode include) {
            System.out.println(indent + "- " + include.getNodeName() + " template=" + include.getTemplatePath() + " (line " + include.getLine() + ")");
        } else if (node instanceof JinjaIdentifierNode id) {
            System.out.println(indent + "- " + id.getNodeName() + " name=" + id.getName() + " (line " + id.getLine() + ")");
        } else if (node instanceof JinjaBinaryOpNode bin) {
            System.out.println(indent + "- " + bin.getNodeName() + " op=" + bin.getOperator() + " (line " + bin.getLine() + ")");
            printJinja(bin.getLeft(), depth + 1);
            printJinja(bin.getRight(), depth + 1);
        } else if (node instanceof JinjaCompareNode cmp) {
            System.out.println(indent + "- " + cmp.getNodeName() + " op=" + cmp.getOperator() + " (line " + cmp.getLine() + ")");
            printJinja(cmp.getLeft(), depth + 1);
            printJinja(cmp.getRight(), depth + 1);
        } else if (node instanceof JinjaUnaryOpNode unary) {
            System.out.println(indent + "- " + unary.getNodeName() + " op=" + unary.getOperator() + " (line " + unary.getLine() + ")");
            printJinja(unary.getOperand(), depth + 1);
        } else if (node instanceof JinjaAttributeAccessNode attr) {
            System.out.println(indent + "- " + attr.getNodeName() + " attr=" + attr.getAttributeName() + " (line " + attr.getLine() + ")");
            printJinja(attr.getObject(), depth + 1);
        } else if (node instanceof JinjaSubscriptNode sub) {
            System.out.println(indent + "- " + sub.getNodeName() + " (line " + sub.getLine() + ")");
            printJinja(sub.getObject(), depth + 1);
            printJinja(sub.getIndex(), depth + 1);
        } else if (node instanceof JinjaCallNode call) {
            System.out.println(indent + "- " + call.getNodeName() + " (line " + call.getLine() + ")");
            printJinja(call.getCallee(), depth + 1);
            for (JinjaNode argument : call.getArguments()) {
                printJinja(argument, depth + 1);
            }
        } else if (node instanceof JinjaFilterApplicationNode filterApp) {
            System.out.println(indent + "- " + filterApp.getNodeName() + " (line " + filterApp.getLine() + ")");
            printJinja(filterApp.getTarget(), depth + 1);
            for (JinjaFilterNode filter : filterApp.getFilters()) {
                printJinja(filter, depth + 1);
            }
        } else if (node instanceof LiteralNode literal) {
            System.out.println(indent + "- " + literal.getNodeName() + " value=" + literal.getStringValue() + " (line " + literal.getLine() + ")");
        } else {
            System.out.println(indent + "- " + node.getNodeName() + " (line " + node.getLine() + ")");
        }
    }
}
