package org.example;

import pipeline.CompilerPipeline;
import webapp.PageCompiler;
import webapp.ProductCatalogPages;

/**
 * Tests for Requirement 6, first half: the Product List and Add Product
 * pages ({@link ProductCatalogPages}), compiled through {@link
 * PageCompiler}.
 *
 * <p>Follows this project's existing test convention: a plain {@code
 * main()} calling one {@code runXxxTest()} per case, each asserting via
 * {@link AssertionError} - the same style as {@link CompilerPipelineTest},
 * {@link GeneratorPhaseTest}, {@link SemanticAnalyzerTest}, {@link
 * CodeGeneratorTest}, and {@link FullPipelineIntegrationTest} - not JUnit.
 *
 * <p>{@link #runExistingCompileToHtmlUnaffectedTest()} and {@link
 * #runExistingCompileUnaffectedTest()} specifically re-run source lifted
 * directly from {@code Main}'s pre-existing demos through the untouched
 * {@link CompilerPipeline} entry points, so Requirements 1-5 staying intact
 * is something this suite checks automatically rather than only asserting
 * in prose.
 */
public class ProductCatalogPagesTest {

    public static void main(String[] args) {
        runProductListNoSemanticErrorsTest();
        runProductListDisplaysEveryProductTest();
        runProductListFieldsAreNotScrambledAcrossProductsTest();
        runAddProductNoSemanticErrorsTest();
        runAddProductHasAllRequestedFieldsTest();
        runBothPagesShareIdenticalNavigationTest();
        runBothPagesShareIdenticalStylesheetTest();
        runPageCompilerSemanticErrorGateTest();
        runExistingCompileToHtmlUnaffectedTest();
        runExistingCompileUnaffectedTest();

        System.out.println("Product catalog pages test passed");
    }

    /** The Product List page - real product data flowing through a real {@code {% for %}} loop - must compile cleanly. */
    private static void runProductListNoSemanticErrorsTest() {
        PageCompiler.RenderedPage page = PageCompiler.compile(
                ProductCatalogPages.buildProductListPythonSource(),
                ProductCatalogPages.buildProductListTemplateSource());

        if (page.hasErrors()) {
            throw new AssertionError("Expected no semantic errors for the Product List page, got: " + page.getErrors());
        }
        if (page.getFinalHtml() == null || page.getFinalHtml().isEmpty()) {
            throw new AssertionError("Expected a non-empty Final HTML Document for the Product List page");
        }

        System.out.println("runProductListNoSemanticErrorsTest passed");
    }

    /**
     * "Product List page should display all products generated from the
     * Python data source": every product's four fields must appear in the
     * Final HTML Document, and the loop itself must be fully unrolled - no
     * leftover {@code {% for %}}/{@code {{ }}} template syntax.
     */
    private static void runProductListDisplaysEveryProductTest() {
        String html = PageCompiler.compile(
                ProductCatalogPages.buildProductListPythonSource(),
                ProductCatalogPages.buildProductListTemplateSource()).getFinalHtml();

        String[][] expectedProducts = {
                {"Wireless Mouse", "19.99", "Electronics", "Ergonomic wireless mouse with a 2.4GHz USB receiver."},
                {"Yoga Mat", "24.95", "Fitness", "Non-slip 6mm exercise mat, machine washable."},
                {"Ceramic Mug", "9.75", "Home", "12oz mug, microwave and dishwasher safe."},
                {"Dotted Notebook", "4.25", "Office", "A5 hardcover notebook with 120 dotted pages."},
                {"Desk Lamp", "32.99", "Office", "Adjustable LED desk lamp with three brightness levels."}
        };
        for (String[] product : expectedProducts) {
            for (String field : product) {
                if (!html.contains(field)) {
                    throw new AssertionError("Expected the Final HTML Document to contain '" + field + "', got:\n" + html);
                }
            }
        }

        if (html.contains("{% for") || html.contains("{{ product")) {
            throw new AssertionError("Expected the {% for %} loop to be fully unrolled, "
                    + "found leftover template syntax in:\n" + html);
        }

        System.out.println("runProductListDisplaysEveryProductTest passed");
    }

    /**
     * Regression guard for the exact bug investigated while building this
     * page: {@code Generator.transformFor} unrolls by splicing each
     * iteration's body nodes directly into the top-level node list, and
     * {@code FinalDocumentGenerator} sorts all top-level nodes by original
     * source (line, column). A loop body spread across multiple nodes at
     * different source positions gets regrouped by original position after
     * unrolling (every product's name together, then every price, etc.)
     * instead of staying grouped per product. This test confirms each
     * product's own fields stay together as designed (one combined Jinja
     * expression per iteration), by checking each product's name is
     * immediately followed by that same product's own price before any
     * other product's name appears.
     */
    private static void runProductListFieldsAreNotScrambledAcrossProductsTest() {
        String html = PageCompiler.compile(
                ProductCatalogPages.buildProductListPythonSource(),
                ProductCatalogPages.buildProductListTemplateSource()).getFinalHtml();

        String[] namesInExpectedOrder = {"Wireless Mouse", "Yoga Mat", "Ceramic Mug", "Dotted Notebook", "Desk Lamp"};
        String[] pricesInExpectedOrder = {"19.99", "24.95", "9.75", "4.25", "32.99"};

        for (int i = 0; i < namesInExpectedOrder.length; i++) {
            int nameIndex = html.indexOf(namesInExpectedOrder[i]);
            if (nameIndex < 0) {
                throw new AssertionError("Expected to find '" + namesInExpectedOrder[i] + "' in the Final HTML Document");
            }
            int priceIndex = html.indexOf(pricesInExpectedOrder[i]);
            if (priceIndex < 0) {
                throw new AssertionError("Expected to find '" + pricesInExpectedOrder[i] + "' in the Final HTML Document");
            }
            // That product's own price must appear on the same line as its
            // name (i.e. before the next newline), not down in a separate
            // block of all-prices grouped away from all-names.
            int nextNewline = html.indexOf('\n', nameIndex);
            if (nextNewline < 0) {
                nextNewline = html.length();
            }
            if (priceIndex < nameIndex || priceIndex > nextNewline) {
                throw new AssertionError("Expected '" + pricesInExpectedOrder[i] + "' to appear on the same line as '"
                        + namesInExpectedOrder[i] + "' (fields grouped per product, not scrambled across products), got:\n" + html);
            }
        }

        System.out.println("runProductListFieldsAreNotScrambledAcrossProductsTest passed");
    }

    /** The Add Product page - fully static markup - must compile cleanly. */
    private static void runAddProductNoSemanticErrorsTest() {
        PageCompiler.RenderedPage page = PageCompiler.compile(
                ProductCatalogPages.buildAddProductPythonSource(),
                ProductCatalogPages.buildAddProductTemplateSource());

        if (page.hasErrors()) {
            throw new AssertionError("Expected no semantic errors for the Add Product page, got: " + page.getErrors());
        }

        System.out.println("runAddProductNoSemanticErrorsTest passed");
    }

    /** "Add Product page should contain: Product Name, Price, Category, Description, Submit button." */
    private static void runAddProductHasAllRequestedFieldsTest() {
        String html = PageCompiler.compile(
                ProductCatalogPages.buildAddProductPythonSource(),
                ProductCatalogPages.buildAddProductTemplateSource()).getFinalHtml();

        String[] requiredSubstrings = {
                "Product Name", "name=\"name\"",
                "Price", "name=\"price\"",
                "Category", "name=\"category\"", "<select",
                "Description", "name=\"description\"", "<textarea",
                "type=\"submit\">Submit</button>"
        };
        for (String required : requiredSubstrings) {
            if (!html.contains(required)) {
                throw new AssertionError("Expected the Add Product page to contain '" + required + "', got:\n" + html);
            }
        }

        System.out.println("runAddProductHasAllRequestedFieldsTest passed");
    }

    /**
     * "The navigation should be reusable rather than duplicated": both
     * pages must render the exact same navigation markup, and each page
     * must link to both pages.
     */
    private static void runBothPagesShareIdenticalNavigationTest() {
        String productListHtml = PageCompiler.compile(
                ProductCatalogPages.buildProductListPythonSource(),
                ProductCatalogPages.buildProductListTemplateSource()).getFinalHtml();
        String addProductHtml = PageCompiler.compile(
                ProductCatalogPages.buildAddProductPythonSource(),
                ProductCatalogPages.buildAddProductTemplateSource()).getFinalHtml();

        String productListNav = extractBetween(productListHtml, "<nav>", "</nav>");
        String addProductNav = extractBetween(addProductHtml, "<nav>", "</nav>");

        if (!productListNav.equals(addProductNav)) {
            throw new AssertionError("Expected identical <nav> markup on both pages (reused, not duplicated), got:\n"
                    + productListNav + "\n---vs---\n" + addProductNav);
        }
        for (String html : new String[] {productListHtml, addProductHtml}) {
            if (!html.contains("href=\"product_list.html\"") || !html.contains("href=\"add_product.html\"")) {
                throw new AssertionError("Expected links to both pages in every page's navigation, got:\n" + html);
            }
        }

        System.out.println("runBothPagesShareIdenticalNavigationTest passed");
    }

    /** Both pages render as one consistent site: the same injected stylesheet, not two independently written ones. */
    private static void runBothPagesShareIdenticalStylesheetTest() {
        String productListHtml = PageCompiler.compile(
                ProductCatalogPages.buildProductListPythonSource(),
                ProductCatalogPages.buildProductListTemplateSource()).getFinalHtml();
        String addProductHtml = PageCompiler.compile(
                ProductCatalogPages.buildAddProductPythonSource(),
                ProductCatalogPages.buildAddProductTemplateSource()).getFinalHtml();

        String productListStyle = extractBetween(productListHtml, "<style>", "</style>");
        String addProductStyle = extractBetween(addProductHtml, "<style>", "</style>");

        if (!productListStyle.equals(addProductStyle)) {
            throw new AssertionError("Expected identical <style> content on both pages, got:\n"
                    + productListStyle + "\n---vs---\n" + addProductStyle);
        }

        System.out.println("runBothPagesShareIdenticalStylesheetTest passed");
    }

    /** {@link PageCompiler} must gate on semantic errors exactly like {@link CompilerPipeline#compileToHtml} already does. */
    private static void runPageCompilerSemanticErrorGateTest() {
        String brokenPython = String.join("\n",
                "from flask import render_template",
                "",
                "def product_list():",
                "    return render_template('product_list.html', products=totally_undefined)",
                ""
        );
        String templateSource = String.join("\n",
                "{% for product in products %}",
                "{{ product.name }}",
                "{% endfor %}",
                ""
        );

        PageCompiler.RenderedPage page = PageCompiler.compile(brokenPython, templateSource);

        if (!page.hasErrors()) {
            throw new AssertionError("Expected semantic errors for an undefined variable");
        }
        if (page.getErrors().isEmpty()) {
            throw new AssertionError("Expected hasErrors() and getErrors() to agree");
        }
        if (page.getFinalHtml() != null) {
            throw new AssertionError("Expected no Final HTML Document to be produced when semantic errors exist");
        }

        System.out.println("runPageCompilerSemanticErrorGateTest passed");
    }

    /**
     * Requirement 5 preserved: the exact source from {@code
     * Main.runFullCodeGenerationDemo()} must still compile through {@link
     * CompilerPipeline#compileToHtml} and produce the same resolved output
     * it always has - {@link PageCompiler} is an addition alongside this
     * method, not a change to it.
     */
    private static void runExistingCompileToHtmlUnaffectedTest() {
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

        generator.CompilationResult result = CompilerPipeline.compileToHtml(pythonSource, templateSource);
        if (result.hasSemanticErrors()) {
            throw new AssertionError("Unexpected semantic errors: " + result.getSemanticErrors());
        }
        if (!result.isFullyGenerated()) {
            throw new AssertionError("Expected successful full generation");
        }
        String html = result.getFinalHtmlDocument();
        if (!html.contains("Ada") || !html.contains("3")) {
            throw new AssertionError("Expected resolved 'Ada'/'3' values, got:\n" + html);
        }
        if (!html.contains("color: navy;")) {
            throw new AssertionError("Expected injected CSS, got:\n" + html);
        }

        System.out.println("runExistingCompileToHtmlUnaffectedTest passed");
    }

    /**
     * Requirement 4 preserved: the exact source from {@code
     * Main.runValidPipelineDemo()} must still compile through {@link
     * CompilerPipeline#compile} and have its {@code {% for %}} loop
     * unrolled by the original {@code Generator} exactly as before.
     */
    private static void runExistingCompileUnaffectedTest() {
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

        CompilerPipeline.Result result = CompilerPipeline.compile(pythonSource, templateSource);
        if (result.hasSemanticErrors()) {
            throw new AssertionError("Unexpected semantic errors: " + result.getSemanticErrors());
        }
        if (!result.isGenerated()) {
            throw new AssertionError("Expected the Generator to have run");
        }

        java.util.List<String> renderedValues = new java.util.ArrayList<>();
        for (template.ast.jinja.JinjaNode node : result.getGeneratedTemplate().getJinjaElements()) {
            if (node instanceof template.ast.jinja.LiteralNode literal) {
                renderedValues.add(literal.getStringValue());
            }
        }
        if (!renderedValues.equals(java.util.List.of("Apple", "Banana", "Cherry"))) {
            throw new AssertionError("Expected the Generator to unroll ['Apple', 'Banana', 'Cherry'] in order, got: " + renderedValues);
        }

        System.out.println("runExistingCompileUnaffectedTest passed");
    }

    private static String extractBetween(String text, String startTag, String endTag) {
        int start = text.indexOf(startTag);
        int end = text.indexOf(endTag, start);
        if (start < 0 || end < 0) {
            throw new AssertionError("Expected to find " + startTag + " ... " + endTag + " in:\n" + text);
        }
        return text.substring(start, end + endTag.length());
    }
}
