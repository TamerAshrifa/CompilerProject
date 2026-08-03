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
import template.ast.jinja.JinjaForNode;
import template.ast.jinja.JinjaNode;
import template.ast.jinja.LiteralNode;

/**
 * End-to-end demo of the compiler pipeline described in the project
 * requirement:
 *
 *   1) Build the Python AST from real Flask source (Lexer -> Parser -> FlaskASTBuilder).
 *   2) Build the Jinja2 AST from a real template source (Lexer -> Parser -> TemplateASTBuilder).
 *   3) Run the Generator, which reads a data array from the Python AST
 *      (the "items" list, passed into render_template()) and threads it
 *      through into the Jinja2 tree, unrolling the {% for %} loop with the
 *      real values.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        String pythonSource = String.join("\n",
            "from flask import Flask, render_template",
            "",
            "app = Flask(__name__)",
            "",
            "@app.route('/items')",
            "def show_items():",
            "    items = ['Apple', 'Banana', 'Cherry']",
            "    return render_template('items.html', items=items)",
            ""
        );

        String templateSource = String.join("\n",
            "<h2>Item List</h2>",
            "{% for item in items %}",
            "{{ item }}",
            "{% endfor %}",
            ""
        );

        System.out.println("=== 1) Python source ===");
        System.out.println(pythonSource);
        ProgramNode pythonAst = buildPythonAst(pythonSource);
        System.out.println("Python AST built: " + pythonAst.getStatements().size() + " top-level statement(s)\n");

        System.out.println("=== 2) Jinja2 template source ===");
        System.out.println(templateSource);
        TemplateProgramNode templateAst = buildTemplateAst(templateSource);
        System.out.println("Template AST built: " + templateAst.getHtmlElements().size() + " HTML element(s), "
            + templateAst.getJinjaElements().size() + " Jinja2 element(s)");
        describeForLoop("Template AST BEFORE generation", templateAst);

        System.out.println("\n=== 3) Generator: passing the Python array into the Jinja2 tree ===");
        Generator generator = new Generator(pythonAst, templateAst, null);
        TemplateProgramNode transformed = generator.generate();

        System.out.println(generator.getSummary());
        describeForLoop("Template AST AFTER generation", transformed);

        System.out.println("\n=== Rendered {{ item }} values (proof the 'items' array flowed from Python into the Jinja2 tree) ===");
        for (JinjaNode node : transformed.getJinjaElements()) {
            if (node instanceof LiteralNode literal) {
                System.out.println("  -> " + literal.getStringValue());
            }
        }
    }

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
        TemplateParser.HtmlDocumentContext tree = parser.htmlDocument();

        TemplateASTBuilder builder = new TemplateASTBuilder();
        return builder.build(tree);
    }

    private static void describeForLoop(String label, TemplateProgramNode templateAst) {
        for (JinjaNode node : templateAst.getJinjaElements()) {
            if (node instanceof JinjaForNode forNode) {
                System.out.println("  [" + label + "] {% for " + forNode.getLoopVariable() + " in "
                    + forNode.getIterable() + " %} body size = " + forNode.getBody().size());
            }
        }
    }
}
