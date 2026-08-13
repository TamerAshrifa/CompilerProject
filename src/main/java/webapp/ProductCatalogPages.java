package webapp;

/**
 * Requirement 6 (first half): source content for the "Product List" and
 * "Add Product" pages, ready to be handed to {@link PageCompiler} (which
 * runs it through the existing, unmodified pipeline).
 *
 * <h2>Why the content lives here instead of directly in {@code Main}</h2>
 * Every existing demo in {@code Main} inlines its Python source and template
 * source as local variables. Product List and Add Product follow that same
 * shape - a Python/Flask source string plus a Jinja2/HTML template source
 * string, nothing else - but the content itself (product data, a full page
 * stylesheet, a working form) is sizeable, so it is kept here as its own
 * file instead of growing {@code Main} with large embedded HTML/CSS blocks.
 * {@code Main} only adds the small, demo-shaped code that calls this class
 * and {@link PageCompiler}.
 *
 * <h2>How navigation is "reusable rather than duplicated"</h2>
 * The template AST/generator only ever <em>re-emit</em> {@code {% include
 * %}}/{@code {% extends %}} as literal text - nothing in {@code
 * TemplateASTBuilder}, semantic analysis, or any generator actually loads a
 * second template file and inlines its content (there is no file-based
 * template resolver anywhere in this project). Actually resolving includes
 * would mean adding that resolution logic to the Generator/AST layer, which
 * this task's rules put off limits. So reuse instead happens where it
 * safely can: {@link #NAVIGATION} is one Java string constant, and both
 * {@link #buildProductListTemplateSource()} and {@link
 * #buildAddProductTemplateSource()} splice in that exact same constant
 * character-for-character. The navigation markup is written once; changing
 * it changes both pages the next time either is compiled - the template
 * source text is shared, not hand-copied twice. The same is true of {@link
 * #STYLE_BLOCK}, so both pages render as one consistent site.
 *
 * <h2>Why the per-product line is one Jinja expression, not several</h2>
 * {@code Generator.transformFor} unrolls a {@code {% for %}} by producing
 * one copy of the loop body's nodes per element and splicing all of those
 * copies directly into the template's top-level node list (the loop
 * wrapper itself does not survive transformation). {@link
 * generator.FinalDocumentGenerator} then sorts every top-level node by its
 * original source (line, column) before printing. Those two already-correct
 * behaviors combine in a way worth knowing when writing a loop body: if a
 * single iteration's markup were split across several nodes at different
 * source positions (e.g. one node for the name, another below it for the
 * price), sorting by position after unrolling would regroup same-field
 * nodes across iterations instead of keeping each product's fields
 * together. Giving each iteration exactly one node sidesteps this
 * entirely, so every product's four fields are combined into a single
 * Jinja expression via string concatenation ({@code +}, evaluated by the
 * existing {@code PythonArithmetic}/{@code JinjaTreeEvaluator}) rather than
 * four separate {@code {{ }}} references.
 *
 * <h2>Why the loop body has no HTML tags in it</h2>
 * {@code TemplateASTBuilder} keeps the HTML tree and the Jinja tree fully
 * independent (by design, documented on that class): an HTML tag that
 * textually sits between {@code {% for %}} and {@code {% endfor %}} is
 * hoisted to the top level and emitted once, not per iteration, and a
 * {@code {{ }}} expression textually inside that hoisted tag is hoisted
 * right along with it - outside the loop's own scope, which semantic
 * analysis correctly reports as an undefined loop variable. So the loop
 * body below is plain Jinja text (no {@code <span>}/{@code <div>} wrapping
 * a field); the surrounding page chrome (nav, headings, the column-label
 * row) is ordinary HTML placed as a sibling before the loop, which {@link
 * generator.FinalDocumentGenerator} already merges in correct document
 * order since sibling-level content (nothing nested inside anything else)
 * is exactly the case it recombines correctly.
 *
 * <h2>Requirement 6, second half: Product Details and Delete Product</h2>
 * Both added without touching a single character of the four methods
 * above. {@link #NAVIGATION} and {@link #STYLE_BLOCK} were built to be
 * shared by reference, so growing their content - two more links, a few
 * more rules for the delete confirmation UI - is the intended extension
 * point, not a redesign; every page that already splices them in picks up
 * the change automatically, which is what keeps four pages' navigation and
 * look identical without duplicating either.
 *
 * <p>Both new pages pass their product through {@code render_template} as
 * a single non-looped variable ({@code product=...}, not {@code
 * products=[...]}), so - unlike the Product List loop above - there is no
 * unrolling and therefore no risk of the position-sort-after-unroll
 * scrambling that motivated combining Product List's fields into one
 * expression. Each field can safely be its own top-level {@code {{ }}}
 * (still not nested inside an HTML tag, for the hoisting reason above), so
 * Product Details and Delete Product each show all four fields clearly
 * labeled on their own line, rather than the single joined line Product
 * List uses.
 *
 * <p>Delete Product's confirmation message and its Delete/Cancel buttons
 * are ordinary static HTML with no Jinja inside them at all, so - like Add
 * Product's form - they nest completely normally.
 */
public final class ProductCatalogPages {

    private ProductCatalogPages() {
        // Not instantiated - every member below is static.
    }

    /**
     * The reusable navigation block, linking all four pages. Spliced
     * verbatim into every page template - {@link #buildProductListTemplateSource()},
     * {@link #buildAddProductTemplateSource()}, {@link
     * #buildProductDetailsTemplateSource()}, and {@link
     * #buildDeleteProductTemplateSource()} - see this class's Javadoc for
     * why a shared Java constant is what "reusable" means in this
     * pipeline. Started with two links; grew to four in place, still one
     * constant, so every page that already splices it in gained the two
     * new links automatically.
     */
    private static final String NAVIGATION = String.join("\n",
            "<nav>",
            "<a href=\"product_list.html\">Product List</a>",
            "<a href=\"add_product.html\">Add Product</a>",
            "<a href=\"product_details.html\">Product Details</a>",
            "<a href=\"delete_product.html\">Delete Product</a>",
            "</nav>"
    );

    /**
     * One shared stylesheet for all four pages (nav, headings, the product
     * listing, the form controls Add Product needs, and the delete
     * confirmation UI Delete Product needs), spliced verbatim into every
     * template so all four pages share one consistent look.
     *
     * <p>{@code white-space: pre-line} on {@code body} is what turns the
     * plain-text loop/label output below into one visual line per entry
     * (the newline {@link generator.FinalDocumentGenerator} already places
     * between top-level nodes would otherwise just collapse to a space, as
     * bare HTML text normally does); every element that does not need that
     * - nav, headings, the intro paragraph, the column-label row, the
     * form, and the confirm box - resets it back to {@code normal} so only
     * the unwrapped product/field lines are affected.
     */
    private static final String STYLE_BLOCK = String.join("\n",
            "<style>",
            "body {",
            "  font-family: 'Segoe UI', Arial, sans-serif;",
            "  background-color: #f4f6f8;",
            "  color: #1f2933;",
            "  max-width: 760px;",
            "  margin: 0 auto;",
            "  padding: 32px 24px 60px;",
            "  line-height: 1.6;",
            "  white-space: pre-line;",
            "}",
            "nav {",
            "  background-color: #1f2933;",
            "  padding: 14px 20px;",
            "  border-radius: 8px;",
            "  margin-bottom: 24px;",
            "  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);",
            "  white-space: normal;",
            "}",
            "nav a {",
            "  color: #ffffff;",
            "  text-decoration: none;",
            "  margin-right: 20px;",
            "  font-weight: 600;",
            "  font-size: 15px;",
            "}",
            "nav a:hover {",
            "  color: #90cdf4;",
            "  text-decoration: underline;",
            "}",
            "h1 {",
            "  color: #102a43;",
            "  border-bottom: 2px solid #d9e2ec;",
            "  padding-bottom: 10px;",
            "  margin-bottom: 6px;",
            "  white-space: normal;",
            "}",
            "p.intro {",
            "  color: #52606d;",
            "  margin-top: 0;",
            "  margin-bottom: 24px;",
            "  white-space: normal;",
            "}",
            ".list-header {",
            "  font-weight: 700;",
            "  background-color: #e4e7eb;",
            "  padding: 10px 14px;",
            "  border-radius: 6px;",
            "  margin-bottom: 4px;",
            "  color: #102a43;",
            "  white-space: normal;",
            "}",
            "form {",
            "  white-space: normal;",
            "  background-color: #ffffff;",
            "  padding: 24px;",
            "  border-radius: 8px;",
            "  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);",
            "}",
            "label {",
            "  display: block;",
            "  margin-top: 16px;",
            "  margin-bottom: 6px;",
            "  font-weight: 600;",
            "  color: #334e68;",
            "}",
            "input, select, textarea {",
            "  width: 100%;",
            "  padding: 10px 12px;",
            "  border: 1px solid #cbd2d9;",
            "  border-radius: 6px;",
            "  font-size: 15px;",
            "  font-family: inherit;",
            "  box-sizing: border-box;",
            "  color: #1f2933;",
            "}",
            "input:focus, select:focus, textarea:focus {",
            "  outline: none;",
            "  border-color: #4299e1;",
            "}",
            "button {",
            "  margin-top: 24px;",
            "  background-color: #2b6cb0;",
            "  color: #ffffff;",
            "  border: none;",
            "  padding: 12px 28px;",
            "  border-radius: 6px;",
            "  font-size: 15px;",
            "  font-weight: 600;",
            "  cursor: pointer;",
            "}",
            "button:hover {",
            "  background-color: #2c5282;",
            "}",
            ".confirm-box {",
            "  white-space: normal;",
            "  background-color: #fff5f5;",
            "  border: 1px solid #fed7d7;",
            "  border-left: 4px solid #e53e3e;",
            "  border-radius: 8px;",
            "  padding: 20px 24px;",
            "  margin-top: 20px;",
            "}",
            ".confirm-box .warning {",
            "  color: #c53030;",
            "  font-weight: 600;",
            "  margin-top: 0;",
            "  margin-bottom: 16px;",
            "}",
            ".delete-form {",
            "  background-color: transparent;",
            "  padding: 0;",
            "  box-shadow: none;",
            "  display: flex;",
            "  align-items: center;",
            "  gap: 12px;",
            "}",
            ".delete-btn {",
            "  margin-top: 0;",
            "  background-color: #e53e3e;",
            "}",
            ".delete-btn:hover {",
            "  background-color: #c53030;",
            "}",
            ".cancel-btn {",
            "  display: inline-block;",
            "  padding: 12px 28px;",
            "  border-radius: 6px;",
            "  font-size: 15px;",
            "  font-weight: 600;",
            "  text-decoration: none;",
            "  color: #334e68;",
            "  background-color: #e4e7eb;",
            "}",
            ".cancel-btn:hover {",
            "  background-color: #cbd2d9;",
            "}",
            "</style>"
    );

    /**
     * Python/Flask source for the Product List page: a small catalog of
     * products (the "Python data source") passed to {@code render_template}
     * exactly like the project's existing demos pass {@code items}/{@code
     * name}/{@code visits}.
     */
    public static String buildProductListPythonSource() {
        return String.join("\n",
                "from flask import Flask, render_template",
                "app = Flask(__name__)",
                "",
                "",
                "@app.route('/products')",
                "def product_list():",
                "    products = [",
                "        {'name': 'Wireless Mouse', 'price': 19.99, 'category': 'Electronics', 'description': 'Ergonomic wireless mouse with a 2.4GHz USB receiver.'},",
                "        {'name': 'Yoga Mat', 'price': 24.95, 'category': 'Fitness', 'description': 'Non-slip 6mm exercise mat, machine washable.'},",
                "        {'name': 'Ceramic Mug', 'price': 9.75, 'category': 'Home', 'description': '12oz mug, microwave and dishwasher safe.'},",
                "        {'name': 'Dotted Notebook', 'price': 4.25, 'category': 'Office', 'description': 'A5 hardcover notebook with 120 dotted pages.'},",
                "        {'name': 'Desk Lamp', 'price': 32.99, 'category': 'Office', 'description': 'Adjustable LED desk lamp with three brightness levels.'}",
                "    ]",
                "    return render_template('product_list.html', products=products)",
                ""
        );
    }

    /**
     * Jinja2/HTML template source for the Product List page: shared styles,
     * shared navigation, a static column-label row, then a {@code {% for
     * %}} loop over {@code products} - the loop the existing {@code
     * Generator} unrolls against the data above.
     */
    public static String buildProductListTemplateSource() {
        return String.join("\n",
                STYLE_BLOCK,
                NAVIGATION,
                "<h1>Product List</h1>",
                "<p class=\"intro\">Every product currently in the catalog, generated from the Python data source.</p>",
                "<div class=\"list-header\">Name | Price | Category | Description</div>",
                "{% for product in products %}",
                "{{ product.name + '  |  $' + product.price + '  |  ' + product.category + '  |  ' + product.description }}",
                "{% endfor %}",
                ""
        );
    }

    /**
     * Python/Flask source for the Add Product page. The page is a static
     * form - nothing here needs to reach the template through {@code
     * render_template}'s keyword arguments, so there are none, exactly like
     * a Flask view that only renders a static page.
     */
    public static String buildAddProductPythonSource() {
        return String.join("\n",
                "from flask import Flask, render_template",
                "app = Flask(__name__)",
                "",
                "",
                "@app.route('/products/add')",
                "def add_product():",
                "    return render_template('add_product.html')",
                ""
        );
    }

    /**
     * Jinja2/HTML template source for the Add Product page: shared styles,
     * shared navigation, and a form with the four requested fields (Product
     * Name, Price, Category, Description) plus a Submit button. Entirely
     * static markup, so - unlike the loop above - it can be nested normally
     * (a real {@code <form>} containing real {@code <label>}/{@code
     * <input>}/{@code <select>}/{@code <textarea>} elements) with no
     * Jinja-in-HTML hoisting to work around.
     */
    public static String buildAddProductTemplateSource() {
        return String.join("\n",
                STYLE_BLOCK,
                NAVIGATION,
                "<h1>Add Product</h1>",
                "<p class=\"intro\">Fill in the details below to add a new product to the catalog.</p>",
                "<form action=\"add_product.html\" method=\"post\">",
                "<label for=\"name\">Product Name</label>",
                "<input type=\"text\" id=\"name\" name=\"name\" placeholder=\"e.g. Wireless Mouse\" required=\"required\" />",
                "<label for=\"price\">Price</label>",
                "<input type=\"number\" id=\"price\" name=\"price\" step=\"0.01\" min=\"0\" placeholder=\"e.g. 19.99\" required=\"required\" />",
                "<label for=\"category\">Category</label>",
                "<select id=\"category\" name=\"category\">",
                "<option value=\"Electronics\">Electronics</option>",
                "<option value=\"Fitness\">Fitness</option>",
                "<option value=\"Home\">Home</option>",
                "<option value=\"Office\">Office</option>",
                "<option value=\"Other\">Other</option>",
                "</select>",
                "<label for=\"description\">Description</label>",
                "<textarea id=\"description\" name=\"description\" rows=\"4\" placeholder=\"Short description of the product\"></textarea>",
                "<button type=\"submit\">Submit</button>",
                "</form>",
                ""
        );
    }

    /**
     * The product Product Details and Delete Product both show, so the two
     * pages read as "viewing/deleting the same product" rather than two
     * unrelated examples. Deliberately the same values as the first entry
     * {@link #buildProductListPythonSource()} already lists, so following
     * the navigation from Product List to either page feels like the same
     * catalog. Not reused *by* {@link #buildProductListPythonSource()}
     * itself - that existing, already-tested method is left exactly as it
     * was rather than refactored to share this constant.
     */
    private static final String FEATURED_PRODUCT_LITERAL =
            "{'name': 'Wireless Mouse', 'price': 19.99, 'category': 'Electronics', "
                    + "'description': 'Ergonomic wireless mouse with a 2.4GHz USB receiver.'}";

    /**
     * Python/Flask source for the Product Details page: one product (the
     * "Python data source" for this page, exactly as Product List's
     * {@code products} list is for that page) passed to {@code
     * render_template} as a single {@code product} variable rather than a
     * list, since this page shows one product's complete information
     * rather than a whole catalog.
     */
    public static String buildProductDetailsPythonSource() {
        return String.join("\n",
                "from flask import Flask, render_template",
                "app = Flask(__name__)",
                "",
                "",
                "@app.route('/products/details')",
                "def product_details():",
                "    product = " + FEATURED_PRODUCT_LITERAL,
                "    return render_template('product_details.html', product=product)",
                ""
        );
    }

    /**
     * Jinja2/HTML template source for the Product Details page: shared
     * styles, shared navigation, then all four fields of {@code product},
     * each labeled and resolved through the same Generator/Context
     * mechanism {@link #buildProductListTemplateSource()} uses - "complete
     * product information", "received from the current generation
     * pipeline". Unlike that page's loop, {@code product} here is not
     * loop-scoped, so there is nothing to unroll and no risk of the
     * position-based regrouping described in this class's Javadoc; each
     * field is simply its own top-level Jinja expression.
     */
    public static String buildProductDetailsTemplateSource() {
        return String.join("\n",
                STYLE_BLOCK,
                NAVIGATION,
                "<h1>Product Details</h1>",
                "<p class=\"intro\">Complete information for this product, generated from the Python data source.</p>",
                "{{ 'Name: ' + product.name }}",
                "{{ 'Price: $' + product.price }}",
                "{{ 'Category: ' + product.category }}",
                "{{ 'Description: ' + product.description }}",
                ""
        );
    }

    /**
     * Python/Flask source for the Delete Product page: the same product as
     * {@link #buildProductDetailsPythonSource()} (see {@link
     * #FEATURED_PRODUCT_LITERAL}), so confirming a delete shows the same
     * product Product Details just displayed.
     */
    public static String buildDeleteProductPythonSource() {
        return String.join("\n",
                "from flask import Flask, render_template",
                "app = Flask(__name__)",
                "",
                "",
                "@app.route('/products/delete')",
                "def delete_product():",
                "    product = " + FEATURED_PRODUCT_LITERAL,
                "    return render_template('delete_product.html', product=product)",
                ""
        );
    }

    /**
     * Jinja2/HTML template source for the Delete Product page: shared
     * styles, shared navigation, the same labeled fields {@link
     * #buildProductDetailsTemplateSource()} shows (so the product being
     * deleted is clearly displayed), then a static confirmation box with a
     * warning and Delete/Cancel buttons. The confirmation box has no Jinja
     * in it at all, so - like Add Product's form - it nests normally
     * (a real {@code <div>}/{@code <form>}, not the flat top-level shape
     * the labeled fields above need).
     */
    public static String buildDeleteProductTemplateSource() {
        return String.join("\n",
                STYLE_BLOCK,
                NAVIGATION,
                "<h1>Delete Product</h1>",
                "<p class=\"intro\">Please confirm you want to remove this product from the catalog.</p>",
                "{{ 'Name: ' + product.name }}",
                "{{ 'Price: $' + product.price }}",
                "{{ 'Category: ' + product.category }}",
                "{{ 'Description: ' + product.description }}",
                "<div class=\"confirm-box\">",
                "<p class=\"warning\">This action cannot be undone.</p>",
                "<form action=\"product_list.html\" method=\"post\" class=\"delete-form\">",
                "<button type=\"submit\" class=\"delete-btn\">Delete</button>",
                "<a href=\"product_list.html\" class=\"cancel-btn\">Cancel</a>",
                "</form>",
                "</div>",
                ""
        );
    }
}
