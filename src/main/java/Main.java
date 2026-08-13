import pipeline.CompilerPipeline;
import generator.CompilationResult;
import semantic.error.SemanticError;
import template.ast.TemplateProgramNode;
import template.ast.jinja.JinjaForNode;
import template.ast.jinja.JinjaNode;
import template.ast.jinja.LiteralNode;
import webapp.PageCompiler;
import webapp.ProductCatalogPages;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * End-to-end demo of the compiler pipeline described in the project
 * requirement:
 *
 *   1) Build the Python AST from real Flask source (Lexer -> Parser -> FlaskASTBuilder).
 *   2) Build the Jinja2 AST from a real template source (Lexer -> Parser -> TemplateASTBuilder).
 *   3) Run semantic analysis over both ASTs (SemanticAnalyzer), building the
 *      symbol table consumed by the next step.
 *   4) Run the Generator, which reads a data array from the Python AST
 *      (the "items" list, passed into render_template()) and threads it
 *      through into the Jinja2 tree, unrolling the {% for %} loop with the
 *      real values.
 *
 * <p>As of this phase, steps 1-4 are no longer performed inline here: they
 * are owned by {@link CompilerPipeline}, which is what actually enforces
 * that step 4 runs only when step 3 found no semantic errors. This class
 * now only builds the two source strings, calls {@link
 * CompilerPipeline#compile}, and prints the result — first for a valid
 * program (the same demo as before, producing the exact same output), then
 * a second time for a deliberately invalid one, to demonstrate the
 * semantic-error gate actually stopping the pipeline before code
 * generation, with every error's message, line number, and node type
 * printed.
 *
 * <p><b>Requirement 5, complete:</b> {@link #runFullCodeGenerationDemo()}
 * and {@link #runFullCodeGenerationErrorGateDemo()} go one stage further,
 * through {@link CompilerPipeline#compileToHtml}, the single entry point
 * connecting every phase (Lexing, Parsing, AST Construction, Semantic
 * Analysis, and now Code Generation) - producing, printing, and writing to
 * disk a complete Final HTML Document, with the exact same automatic
 * semantic-error gate demonstrated a second time at this final stage.
 *
 * <p><b>Requirement 6, first half:</b> {@link #runProductCatalogPagesDemo()}
 * builds on the same, still-unmodified pipeline to add two pages, Product
 * List and Add Product (content in {@link webapp.ProductCatalogPages},
 * compiled via {@link webapp.PageCompiler} - see that class's Javadoc for
 * why Product List, whose data is a list rendered through a {@code {% for
 * %}} loop, is compiled through {@link CompilerPipeline#compile}'s existing
 * Generator stage rather than {@link CompilerPipeline#compileToHtml}).
 *
 * <p><b>Requirement 6, complete:</b> {@link #runProductCatalogPagesDemo()}
 * now renders all four pages - Product List, Add Product, Product Details,
 * and Delete Product - the same way, through the exact same {@link
 * webapp.PageCompiler#compile}. No second generation path was added: the
 * two new pages are new content in {@link webapp.ProductCatalogPages}, not
 * new code in the pipeline itself.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        runValidPipelineDemo();
        System.out.println();
        runSemanticErrorGateDemo();
        System.out.println();
        runFullCodeGenerationDemo();
        System.out.println();
        runFullCodeGenerationErrorGateDemo();
        System.out.println();
        runProductCatalogPagesDemo();
    }

    /** The original demo: valid Flask source and a valid Jinja2 template, all the way through generation. */
    private static void runValidPipelineDemo() {
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

        System.out.println("=== 2) Jinja2 template source ===");
        System.out.println(templateSource);

        CompilerPipeline.Result result = CompilerPipeline.compile(pythonSource, templateSource);

        System.out.println("Python AST built: " + result.getPythonAst().getStatements().size() + " top-level statement(s)\n");
        System.out.println("Template AST built: " + result.getTemplateAst().getHtmlElements().size() + " HTML element(s), "
            + result.getTemplateAst().getJinjaElements().size() + " Jinja2 element(s)");
        describeForLoop("Template AST BEFORE generation", result.getTemplateAst());

        System.out.println("\n=== 3) Semantic Analysis: walking both ASTs to build the symbol table ===");
        System.out.println(result.getSemanticAnalyzer().getSummary());

        // Integration Requirement #1: the Generator must execute ONLY if
        // there are no semantic errors. CompilerPipeline already enforced
        // this while building `result` - by this point the Generator has
        // either already run (isGenerated() == true) or never ran at all.
        if (result.hasSemanticErrors()) {
            printSemanticErrorsAndHalt(result);
            return;
        }

        System.out.println("\n=== 4) Generator: passing the Python array into the Jinja2 tree ===");
        System.out.println(result.getGenerator().getSummary());
        describeForLoop("Template AST AFTER generation", result.getGeneratedTemplate());

        System.out.println("\n=== Rendered {{ item }} values (proof the 'items' array flowed from Python into the Jinja2 tree) ===");
        for (JinjaNode node : result.getGeneratedTemplate().getJinjaElements()) {
            if (node instanceof LiteralNode literal) {
                System.out.println("  -> " + literal.getStringValue());
            }
        }
    }

    /**
     * Integration Requirements #1-#2, demonstrated concretely: this
     * source references an undefined variable, so semantic analysis must
     * report it and the Generator must never run.
     */
    private static void runSemanticErrorGateDemo() {
        String brokenPythonSource = String.join("\n",
            "from flask import render_template",
            "",
            "def show_items():",
            "    return render_template('items.html', items=missing_variable)",
            ""
        );
        String templateSource = String.join("\n",
            "{% for item in items %}",
            "{{ item }}",
            "{% endfor %}",
            ""
        );

        System.out.println("=== Semantic-error demo: an undefined variable must halt the pipeline before generation ===");
        System.out.println(brokenPythonSource);

        CompilerPipeline.Result result = CompilerPipeline.compile(brokenPythonSource, templateSource);

        if (result.hasSemanticErrors()) {
            printSemanticErrorsAndHalt(result);
        } else {
            // Should be unreachable for this deliberately-broken source;
            // printed instead of asserted since this is a runnable demo, not a test.
            System.out.println("(unexpected: no semantic errors were found for this source)");
        }
    }

    /**
     * Requirement 5, complete: the same shape of demo as {@link #runValidPipelineDemo()},
     * but run all the way through {@link CompilerPipeline#compileToHtml} - the
     * single pipeline connecting Lexing, Parsing, AST Construction, Semantic
     * Analysis, and Code Generation (Jinja generation, HTML generation, and
     * CSS generation/injection) in one call - printing the resulting Final
     * HTML Document and then writing it to an actual file on disk.
     */
    private static void runFullCodeGenerationDemo() throws Exception {
        String pythonSource = String.join("\n",
            "from flask import Flask, render_template",
            "",
            "app = Flask(__name__)",
            "",
            "@app.route('/profile')",
            "def profile():",
            "    name = 'Ada'",
            "    visits = 3",
            "    return render_template('profile.html', name=name, visits=visits)",
            ""
        );

        String templateSource = String.join("\n",
            "<html>",
            "<head>",
            "    <title>Profile</title>",
            "    <style>",
            "        body { font-family: sans-serif; color: navy; }",
            "    </style>",
            "</head>",
            "<body>",
            "<h1>Hello, {{ name }}</h1>",
            "<p>Visits: {{ visits }}</p>",
            "</body>",
            "</html>",
            ""
        );

        System.out.println("=== Requirement 5, complete: Python Source -> Lexer -> Parser -> Python AST ===");
        System.out.println("===   -> Semantic Analysis -> Generator -> Jinja AST -> Jinja Generator      ===");
        System.out.println("===   -> HTML Generator -> CSS Generator -> Final HTML File                  ===");
        System.out.println(pythonSource);
        System.out.println(templateSource);

        CompilationResult result = CompilerPipeline.compileToHtml(pythonSource, templateSource);

        // Requirement 5, task 3: generation must stop automatically if
        // semantic errors exist. CompilerPipeline#compileToHtml already
        // enforced this while building `result` - reusing the exact same
        // CompilerPipeline.Result-based printing the earlier demos use.
        if (result.hasSemanticErrors()) {
            printSemanticErrorsAndHalt(result.getCompileResult());
            return;
        }

        System.out.println("\n=== Semantic Analysis ===");
        System.out.println(result.getSemanticAnalyzer().getSummary());

        System.out.println("=== Code Generation (" + result.getCodeGenerator().getClass().getSimpleName() + ") ===");
        System.out.println(result.getCodeGenerator().getSummary());

        System.out.println("=== Final HTML Document ===");
        System.out.println(result.getFinalHtmlDocument());

        Path outputFile = Files.createTempFile("compiler-project-final-output", ".html");
        result.writeFinalHtmlToFile(outputFile);
        System.out.println("=== Final HTML File written to disk: " + outputFile.toAbsolutePath() + " ===");
        System.out.println("(" + Files.size(outputFile) + " bytes)");
    }

    /**
     * Requirement 5, task 3, demonstrated at the fully-integrated pipeline
     * level: the same undefined-variable source as {@link #runSemanticErrorGateDemo()},
     * this time through {@link CompilerPipeline#compileToHtml}, confirming
     * that code generation - not just the earlier Generator - is also
     * skipped automatically rather than run against a broken AST.
     */
    private static void runFullCodeGenerationErrorGateDemo() {
        String brokenPythonSource = String.join("\n",
            "from flask import render_template",
            "",
            "def profile():",
            "    return render_template('profile.html', name=undefined_name)",
            ""
        );
        String templateSource = String.join("\n",
            "<h1>Hello, {{ name }}</h1>",
            ""
        );

        System.out.println("=== Requirement 5 error-gate demo: compileToHtml must also stop before code generation ===");
        System.out.println(brokenPythonSource);

        CompilationResult result = CompilerPipeline.compileToHtml(brokenPythonSource, templateSource);

        if (result.hasSemanticErrors()) {
            printSemanticErrorsAndHalt(result.getCompileResult());
            System.out.println("Code generation ran: " + result.isFullyGenerated()
                + " | Final HTML Document: " + result.getFinalHtmlDocument());
        } else {
            // Should be unreachable for this deliberately-broken source;
            // printed instead of asserted since this is a runnable demo, not a test.
            System.out.println("(unexpected: no semantic errors were found for this source)");
        }
    }

    /**
     * Requirement 6, complete: Product List, Add Product, Product Details,
     * and Delete Product - all four pages this stage adds, all compiled
     * through {@link PageCompiler#compile}, then written to the same
     * directory under their real page filenames (rather than the single
     * random temp filename {@link #runFullCodeGenerationDemo()} uses) so
     * the navigation links {@link ProductCatalogPages} embeds between the
     * pages actually resolve when all four files sit side by side.
     */
    private static void runProductCatalogPagesDemo() throws Exception {
        Path outputDirectory = Files.createTempDirectory("compiler-project-web-pages");
        System.out.println("=== Requirement 6, complete: Product List, Add Product, Product Details, Delete Product ===");
        System.out.println("(all four pages written to " + outputDirectory.toAbsolutePath() + ")\n");

        renderWebPage("Product List", "product_list.html",
            ProductCatalogPages.buildProductListPythonSource(),
            ProductCatalogPages.buildProductListTemplateSource(),
            outputDirectory);

        System.out.println();

        renderWebPage("Add Product", "add_product.html",
            ProductCatalogPages.buildAddProductPythonSource(),
            ProductCatalogPages.buildAddProductTemplateSource(),
            outputDirectory);

        System.out.println();

        renderWebPage("Product Details", "product_details.html",
            ProductCatalogPages.buildProductDetailsPythonSource(),
            ProductCatalogPages.buildProductDetailsTemplateSource(),
            outputDirectory);

        System.out.println();

        renderWebPage("Delete Product", "delete_product.html",
            ProductCatalogPages.buildDeleteProductPythonSource(),
            ProductCatalogPages.buildDeleteProductTemplateSource(),
            outputDirectory);
    }

    /** Shared by every Requirement 6 page: compile, print, and write one page to disk under its real filename. */
    private static void renderWebPage(String label, String filename, String pythonSource,
                                       String templateSource, Path outputDirectory) throws Exception {
        System.out.println("--- " + label + ": Python source ---");
        System.out.println(pythonSource);
        System.out.println("--- " + label + ": Jinja2/HTML template source ---");
        System.out.println(templateSource);

        PageCompiler.RenderedPage page = PageCompiler.compile(pythonSource, templateSource);

        if (page.hasErrors()) {
            printSemanticErrorsAndHalt(page.getCompilationResult());
            return;
        }

        System.out.println("--- " + label + ": Final HTML Document ---");
        System.out.println(page.getFinalHtml());

        Path outputFile = outputDirectory.resolve(filename);
        Files.writeString(outputFile, page.getFinalHtml());
        System.out.println("--- " + label + " written to disk: " + outputFile.toAbsolutePath()
            + " (" + Files.size(outputFile) + " bytes) ---");
    }

    /** Integration Requirement #2: print every semantic error, preserving line numbers and node names. */
    private static void printSemanticErrorsAndHalt(CompilerPipeline.Result result) {
        System.out.println("\n=== Semantic errors found - compilation halted before code generation ===");
        for (SemanticError error : result.getSemanticErrors()) {
            System.out.println("  " + error);
        }
        System.out.println(result.getSemanticErrors().size() + " error(s) reported. Generator executed: " + result.isGenerated());
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
