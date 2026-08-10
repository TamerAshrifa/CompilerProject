package semantic;

import pipeline.CompilerPipeline;

/**
 * Example usage of this project's semantic-analysis debug tooling: runs
 * real Python and Jinja2/HTML source through the existing, unmodified
 * {@link CompilerPipeline} (Lexer -&gt; Parser -&gt; AST -&gt; {@link SemanticAnalyzer}
 * - exactly the same pipeline {@code Main} already drives), then prints the
 * unified report with a single call to {@link DebugOutput#printFullDebugOutput}.
 *
 * <p>The source below is deliberately written to exercise all four sections
 * of that report at once:
 * <ul>
 *   <li>Two functions (one calling the other's would-be-local variable, and
 *       one bare undefined name) so the <b>Python AST</b> has real
 *       structure - decorators, parameters with a default, a loop, an
 *       if/else - and the <b>Symbol Table</b> shows a Global scope with two
 *       nested Function scopes.</li>
 *   <li>A template that reads a variable actually supplied through the
 *       Python side's {@code render_template(...)} call (correctly
 *       resolved - see {@link SemanticAnalyzer}'s own documentation on how
 *       that cross-AST context passing works), a {@code {% for %}} loop
 *       nesting its own {@code {% if %}}/{@code {% else %}} (its own nested
 *       Jinja scope), and two variables nobody ever supplies, so the
 *       <b>Jinja2 AST</b> and its slice of the <b>Symbol Table</b> both have
 *       real, properly nested content too.</li>
 *   <li>Four resulting <b>Semantic Errors</b> (two Python, two Jinja2) to
 *       show {@link SemanticError#format()} on real, analyzer-produced
 *       diagnostics rather than hand-built ones.</li>
 * </ul>
 *
 * <p>Run directly ({@code java semantic.DebugOutputDemo}) to see the output.
 */
public final class DebugOutputDemo {

    private DebugOutputDemo() {
        // Demo entry point - not instantiated.
    }

    public static void main(String[] args) {
        String pythonSource = String.join("\n",
                "from flask import Flask, render_template",
                "app = Flask(__name__)",
                "",
                "@app.route('/products')",
                "def list_products():",
                "    products = ['Apple', 'Bread', 'Milk']",
                "    return render_template('products.html', products=products, title='Our Products')",
                "",
                "@app.route('/product/<id>')",
                "def get_product(id, verbose=False):",
                "    for item in products:",
                "        if item == id:",
                "            return render_template('product.html', item=item)",
                "    return missing_product",
                ""
        );

        String templateSource = String.join("\n",
                "<html>",
                "<body>",
                "<h1>{{ title }}</h1>",
                "{% if products %}",
                "{% for product in products %}",
                "{{ product }} - {{ price }}",
                "{% endfor %}",
                "{% else %}",
                "{{ empty_message }}",
                "{% endif %}",
                "</body>",
                "</html>",
                ""
        );

        System.out.println("Python source:");
        System.out.println(pythonSource);
        System.out.println("Template source:");
        System.out.println(templateSource);
        System.out.println();

        // Exactly the pipeline Main already drives - this demo adds nothing
        // to it, it only prints more about what came out the other end.
        CompilerPipeline.Result result = CompilerPipeline.compile(pythonSource, templateSource);

        DebugOutput.printFullDebugOutput(
                result.getPythonAst(),
                result.getTemplateAst(),
                result.getSemanticAnalyzer().getSymbolTable(),
                result.getSemanticErrors());
    }
}
