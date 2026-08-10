package org.example;

import webapp.PageCompiler;
import webapp.ProductCatalogPages;

/**
 * Tests for Requirement 6, second half: the Product Details and Delete
 * Product pages, plus the four-page navigation that now links Product
 * List, Add Product, Product Details, and Delete Product together.
 *
 * <p>Kept as its own file rather than added to {@link
 * ProductCatalogPagesTest} - that file is left completely unmodified (its
 * existing test methods are exactly as they were), matching this project's
 * existing convention of one test file per requirement stage ({@link
 * CodeGeneratorTest} for Requirement 5, {@link ProductCatalogPagesTest} for
 * Requirement 6's first half, this file for its second half). Same plain
 * {@code main()} / {@link AssertionError} style as every other test class
 * here - not JUnit.
 */
public class ProductDetailAndDeletePagesTest {

    public static void main(String[] args) {
        runProductDetailsNoSemanticErrorsTest();
        runProductDetailsDisplaysCompleteProductInformationTest();
        runDeleteProductNoSemanticErrorsTest();
        runDeleteProductDisplaysProductInformationTest();
        runDeleteProductHasConfirmationAndBothButtonsTest();
        runAllFourPagesShareIdenticalNavigationTest();
        runAllFourPagesNavigationLinksToAllFourPagesTest();
        runAllFourPagesShareIdenticalStylesheetTest();
        runExistingTwoPagesStillCompileTest();

        System.out.println("Product detail and delete pages test passed");
    }

    /** The Product Details page - a single product's data flowing through the pipeline - must compile cleanly. */
    private static void runProductDetailsNoSemanticErrorsTest() {
        PageCompiler.RenderedPage page = PageCompiler.compile(
                ProductCatalogPages.buildProductDetailsPythonSource(),
                ProductCatalogPages.buildProductDetailsTemplateSource());

        if (page.hasErrors()) {
            throw new AssertionError("Expected no semantic errors for the Product Details page, got: " + page.getErrors());
        }
        if (page.getFinalHtml() == null || page.getFinalHtml().isEmpty()) {
            throw new AssertionError("Expected a non-empty Final HTML Document for the Product Details page");
        }

        System.out.println("runProductDetailsNoSemanticErrorsTest passed");
    }

    /**
     * "Display complete product information" / "Receive product
     * information from the current generation pipeline": all four fields
     * must be present and fully resolved (no leftover {@code {{ }}}
     * template syntax) - not hardcoded page copy, but the same {@code
     * Context}/{@code Generator} resolution mechanism Product List uses.
     */
    private static void runProductDetailsDisplaysCompleteProductInformationTest() {
        String html = PageCompiler.compile(
                ProductCatalogPages.buildProductDetailsPythonSource(),
                ProductCatalogPages.buildProductDetailsTemplateSource()).getFinalHtml();

        String[] expectedFields = {
                "Wireless Mouse", "19.99", "Electronics", "Ergonomic wireless mouse with a 2.4GHz USB receiver."
        };
        for (String field : expectedFields) {
            if (!html.contains(field)) {
                throw new AssertionError("Expected the Final HTML Document to contain '" + field + "', got:\n" + html);
            }
        }
        if (html.contains("{{ product") || html.contains("{% ")) {
            throw new AssertionError("Expected every {{ }} reference to be resolved, found leftover template syntax in:\n" + html);
        }

        System.out.println("runProductDetailsDisplaysCompleteProductInformationTest passed");
    }

    /** The Delete Product page - also a single product's data, plus static confirmation UI - must compile cleanly. */
    private static void runDeleteProductNoSemanticErrorsTest() {
        PageCompiler.RenderedPage page = PageCompiler.compile(
                ProductCatalogPages.buildDeleteProductPythonSource(),
                ProductCatalogPages.buildDeleteProductTemplateSource());

        if (page.hasErrors()) {
            throw new AssertionError("Expected no semantic errors for the Delete Product page, got: " + page.getErrors());
        }

        System.out.println("runDeleteProductNoSemanticErrorsTest passed");
    }

    /** "Delete Product page: Display product information." */
    private static void runDeleteProductDisplaysProductInformationTest() {
        String html = PageCompiler.compile(
                ProductCatalogPages.buildDeleteProductPythonSource(),
                ProductCatalogPages.buildDeleteProductTemplateSource()).getFinalHtml();

        String[] expectedFields = {
                "Wireless Mouse", "19.99", "Electronics", "Ergonomic wireless mouse with a 2.4GHz USB receiver."
        };
        for (String field : expectedFields) {
            if (!html.contains(field)) {
                throw new AssertionError("Expected the Delete Product page to display '" + field + "', got:\n" + html);
            }
        }

        System.out.println("runDeleteProductDisplaysProductInformationTest passed");
    }

    /** "Ask for confirmation. Include Delete and Cancel buttons." */
    private static void runDeleteProductHasConfirmationAndBothButtonsTest() {
        String html = PageCompiler.compile(
                ProductCatalogPages.buildDeleteProductPythonSource(),
                ProductCatalogPages.buildDeleteProductTemplateSource()).getFinalHtml();

        if (!html.toLowerCase().contains("cannot be undone") && !html.toLowerCase().contains("confirm")) {
            throw new AssertionError("Expected a confirmation message on the Delete Product page, got:\n" + html);
        }
        if (!html.contains("class=\"delete-btn\">Delete</button>")) {
            throw new AssertionError("Expected a Delete button, got:\n" + html);
        }
        if (!html.contains("class=\"cancel-btn\">Cancel</a>")) {
            throw new AssertionError("Expected a Cancel button, got:\n" + html);
        }

        System.out.println("runDeleteProductHasConfirmationAndBothButtonsTest passed");
    }

    /**
     * "Navigation should feel like a complete website. Avoid duplicated
     * navigation code": all four pages must render byte-identical
     * navigation markup - proof it comes from one shared constant, not
     * four hand-written copies.
     */
    private static void runAllFourPagesShareIdenticalNavigationTest() {
        String[] pages = allFourPagesHtml();
        String firstNav = extractBetween(pages[0], "<nav>", "</nav>");

        for (int i = 1; i < pages.length; i++) {
            String nav = extractBetween(pages[i], "<nav>", "</nav>");
            if (!nav.equals(firstNav)) {
                throw new AssertionError("Expected identical <nav> markup on every page, page " + i
                        + " differed:\n" + firstNav + "\n---vs---\n" + nav);
            }
        }

        System.out.println("runAllFourPagesShareIdenticalNavigationTest passed");
    }

    /** Every page must offer a way to reach every page - "smooth navigation between ALL pages". */
    private static void runAllFourPagesNavigationLinksToAllFourPagesTest() {
        String[] requiredHrefs = {
                "href=\"product_list.html\"", "href=\"add_product.html\"",
                "href=\"product_details.html\"", "href=\"delete_product.html\""
        };

        for (String html : allFourPagesHtml()) {
            String nav = extractBetween(html, "<nav>", "</nav>");
            for (String href : requiredHrefs) {
                if (!nav.contains(href)) {
                    throw new AssertionError("Expected every page's navigation to link to all four pages, missing '"
                            + href + "' in:\n" + nav);
                }
            }
        }

        System.out.println("runAllFourPagesNavigationLinksToAllFourPagesTest passed");
    }

    /** All four pages render as one consistent site: the same injected stylesheet on every page. */
    private static void runAllFourPagesShareIdenticalStylesheetTest() {
        String[] pages = allFourPagesHtml();
        String firstStyle = extractBetween(pages[0], "<style>", "</style>");

        for (int i = 1; i < pages.length; i++) {
            String style = extractBetween(pages[i], "<style>", "</style>");
            if (!style.equals(firstStyle)) {
                throw new AssertionError("Expected identical <style> content on every page, page " + i + " differed");
            }
        }

        System.out.println("runAllFourPagesShareIdenticalStylesheetTest passed");
    }

    /**
     * "Preserve every existing feature": Product List and Add Product -
     * untouched by this stage's changes to {@link ProductCatalogPages} -
     * must still compile cleanly. ({@link ProductCatalogPagesTest}, run in
     * full and unmodified, is the primary check for this; this is a light
     * sanity check alongside it.)
     */
    private static void runExistingTwoPagesStillCompileTest() {
        PageCompiler.RenderedPage productList = PageCompiler.compile(
                ProductCatalogPages.buildProductListPythonSource(),
                ProductCatalogPages.buildProductListTemplateSource());
        if (productList.hasErrors()) {
            throw new AssertionError("Expected Product List to still compile cleanly, got: " + productList.getErrors());
        }

        PageCompiler.RenderedPage addProduct = PageCompiler.compile(
                ProductCatalogPages.buildAddProductPythonSource(),
                ProductCatalogPages.buildAddProductTemplateSource());
        if (addProduct.hasErrors()) {
            throw new AssertionError("Expected Add Product to still compile cleanly, got: " + addProduct.getErrors());
        }

        System.out.println("runExistingTwoPagesStillCompileTest passed");
    }

    private static String[] allFourPagesHtml() {
        return new String[] {
                PageCompiler.compile(
                        ProductCatalogPages.buildProductListPythonSource(),
                        ProductCatalogPages.buildProductListTemplateSource()).getFinalHtml(),
                PageCompiler.compile(
                        ProductCatalogPages.buildAddProductPythonSource(),
                        ProductCatalogPages.buildAddProductTemplateSource()).getFinalHtml(),
                PageCompiler.compile(
                        ProductCatalogPages.buildProductDetailsPythonSource(),
                        ProductCatalogPages.buildProductDetailsTemplateSource()).getFinalHtml(),
                PageCompiler.compile(
                        ProductCatalogPages.buildDeleteProductPythonSource(),
                        ProductCatalogPages.buildDeleteProductTemplateSource()).getFinalHtml()
        };
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
