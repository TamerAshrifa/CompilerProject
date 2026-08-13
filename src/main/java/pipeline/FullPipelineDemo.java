package pipeline;

import generator.CompilationResult;

/**
 * Main execution demo for the full compiler pipeline, debug mode included:
 * {@code Parse -> AST -> Semantic -> Print -> Generate HTML}, exactly as
 * {@link CompilerPipeline#compileToHtml(String, String, boolean)} already
 * runs it - this class adds no logic of its own, it only calls that one
 * method a few times with different inputs to show what each path looks
 * like on screen.
 *
 * <p>Three runs, back to back:
 * <ol>
 *   <li><b>Clean source, debug mode on.</b> No semantic errors, so the
 *       pipeline runs all the way through to a real generated HTML
 *       document - the full report {@link CompilerPipeline}'s class
 *       documentation describes, ending with that HTML.</li>
 *   <li><b>Broken source, debug mode on.</b> Two undefined variables, so
 *       code generation is correctly skipped - proving debug mode's
 *       printing does not change that existing rule (Integration
 *       Requirement #1, unchanged since the very first version of this
 *       pipeline) even though it is now printing more than before.</li>
 *   <li><b>The same clean source again, debug mode off.</b> Included so the
 *       contrast is visible directly in the output: nothing between this
 *       line and the next prints - {@code debugMode=false} is exactly
 *       {@link CompilerPipeline#compileToHtml(String, String)}, the
 *       original, print-free method, and the returned {@link
 *       CompilationResult} is exactly as complete either way.</li>
 * </ol>
 *
 * <p>Run directly ({@code java pipeline.FullPipelineDemo}) to see the output.
 */
public final class FullPipelineDemo {

    private FullPipelineDemo() {
        // Demo entry point - not instantiated.
    }

    public static void main(String[] args) {
        String pythonSource = String.join("\n",
                "from flask import Flask, render_template",
                "app = Flask(__name__)",
                "",
                "@app.route('/products')",
                "def show_products():",
                "    products = ['Apple', 'Bread', 'Milk']",
                "    title = 'Our Products'",
                "    return render_template('products.html', products=products, title=title)",
                ""
        );
        String templateSource = String.join("\n",
                "<html>",
                "<body>",
                "<h1>{{ title }}</h1>",
                "<ul>",
                "{% for product in products %}",
                "{{ product }}",
                "{% endfor %}",
                "</ul>",
                "</body>",
                "</html>",
                ""
        );

        System.out.println("############################################################");
        System.out.println("# RUN 1: clean source, debugMode = true");
        System.out.println("############################################################");
        CompilationResult clean = CompilerPipeline.compileToHtml(pythonSource, templateSource, true);
        System.out.println();
        System.out.println("Run 1 result: isFullyGenerated=" + clean.isFullyGenerated()
                + ", semantic errors=" + clean.getSemanticErrors().size());

        String brokenPythonSource = String.join("\n",
                "from flask import Flask, render_template",
                "app = Flask(__name__)",
                "",
                "@app.route('/broken')",
                "def broken_view():",
                "    return render_template('broken.html', total=subtotal + tax)",
                ""
        );
        String brokenTemplateSource = "<p>{{ total }}</p>\n";

        System.out.println();
        System.out.println("############################################################");
        System.out.println("# RUN 2: broken source (undefined variables), debugMode = true");
        System.out.println("############################################################");
        CompilationResult broken = CompilerPipeline.compileToHtml(brokenPythonSource, brokenTemplateSource, true);
        System.out.println();
        System.out.println("Run 2 result: isFullyGenerated=" + broken.isFullyGenerated()
                + ", semantic errors=" + broken.getSemanticErrors().size());

        System.out.println();
        System.out.println("############################################################");
        System.out.println("# RUN 3: the clean source again, debugMode = false");
        System.out.println("############################################################");
        System.out.println("(nothing should print between this line and the result line below)");
        CompilationResult silent = CompilerPipeline.compileToHtml(pythonSource, templateSource, false);
        boolean sameHtml = silent.isFullyGenerated() && clean.isFullyGenerated()
                && silent.getFinalHtmlDocument().equals(clean.getFinalHtmlDocument());
        System.out.println("Run 3 result: isFullyGenerated=" + silent.isFullyGenerated()
                + ", identical HTML as run 1=" + sameHtml);
    }
}
