package flask.test;

import grammar.flask.FlaskLexer;
import grammar.flask.FlaskParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

/**
 * اختبار Parser الكامل مع جميع Flask features
 */
public class TestCompleteParser {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║       Final Flask Parser Comprehensive Test           ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        // Category 1: Simple Statements
        testSimpleStatements();

        // Category 2: Compound Statements
        testCompoundStatements();

        // Category 3: Expressions in Conditions
        testExpressionsInConditions();

        // Category 4: Real Flask Apps
        testRealFlaskApps();

        // Category 5: Trailing Commas (list/dict/set literals, calls, params)
        testTrailingCommas();

        // Category 6: Generator Expressions & *args/**kwargs
        testGeneratorExpressionsAndStarArgs();

        // Summary
        printSummary();
    }

    // ═══════════════════════════════════════════════════════════
    // Category 1: Simple Statements
    // ═══════════════════════════════════════════════════════════

    private static void testSimpleStatements() {
        System.out.println("📋 Category 1: Simple Statements");
        System.out.println("═===============================================================");

        test("Import", "from flask import Flask\n");
        test("Assignment", "x = 5\n");
        test("Expression Statement", "app.run()\n");
        test("Return", "def f():\n    return 1, 2\n");
        test("Pass", "pass\n");
        test("Break", "while True:\n    break\n");
        test("Continue", "for i in range(10):\n    continue\n");
        test("Del", "del x\n");
        test("Assert", "assert x > 0\n");
        test("Global", "global x\n");
        test("Raise", "raise ValueError('error')\n");
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════
    // Category 2: Compound Statements
    // ═══════════════════════════════════════════════════════════

    private static void testCompoundStatements() {
        System.out.println("📋 Category 2: Compound Statements");
        System.out.println("═===============================================================");

        test("Function Def", "def foo():\n    pass\n");
        test("Decorator", "@app.route('/')\ndef home():\n    pass\n");
        test("If Statement", "if x > 5:\n    pass\n");
        test("If-Elif-Else", "if x > 5:\n    pass\nelif x < 0:\n    pass\nelse:\n    pass\n");
        test("For Loop", "for i in range(10):\n    print(i)\n");
        test("While Loop", "while True:\n    break\n");
        test("With Statement", "with app.app_context():\n    pass\n");
        test("Try-Except", "try:\n    x()\nexcept:\n    pass\n");
        test("Class", "class User:\n    pass\n");
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════
    // Category 3: Expressions in Conditions (المهم!)
    // ═══════════════════════════════════════════════════════════

    private static void testExpressionsInConditions() {
        System.out.println("📋 Category 3: Expressions in Conditions (Critical!)");
        System.out.println("═===============================================================");

        test("If with comparison", "if x > 5:\n    pass\n");
        test("If with function call", "if is_valid():\n    pass\n");
        test("If with method call", "if user.is_active():\n    pass\n");
        test("If with boolean", "if True and False:\n    pass\n");
        test("While with comparison", "while x < 10:\n    x += 1\n");
        test("While with function", "while has_data():\n    process()\n");
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════
    // Category 4: Real Flask Apps
    // ═══════════════════════════════════════════════════════════

    private static void testRealFlaskApps() {
        System.out.println("📋 Category 4: Real Flask Applications");
        System.out.println("═===============================================================");

        // App 1: Basic Flask
        test("Basic Flask App",
                "from flask import Flask\n" +
                        "app = Flask(__name__)\n" +
                        "@app.route('/')\n" +
                        "def home():\n" +
                        "    return 'Hello'\n" +
                        "app.run()\n"
        );

        // App 2: Flask with error handling
        test("Flask with Error Handling",
                "from flask import Flask, jsonify\n" +
                        "app = Flask(__name__)\n" +
                        "@app.route('/api/data')\n" +
                        "def get_data():\n" +
                        "    try:\n" +
                        "        data = fetch()\n" +
                        "        return jsonify(data), 200\n" +
                        "    except Exception as e:\n" +
                        "        return jsonify(error=str(e)), 500\n"
        );

        // App 3: Flask with context
        test("Flask with Context",
                "from flask import Flask\n" +
                        "app = Flask(__name__)\n" +
                        "with app.app_context():\n" +
                        "    db.create_all()\n"
        );

        // App 4: Complete Flask App
        test("Complete Flask App",
                "from flask import Flask, jsonify\n" +
                        "\n" +
                        "app = Flask(__name__)\n" +
                        "\n" +
                        "class Database:\n" +
                        "    def connect(self):\n" +
                        "        pass\n" +
                        "\n" +
                        "@app.route('/api/data')\n" +
                        "def get_data():\n" +
                        "    try:\n" +
                        "        with app.app_context():\n" +
                        "            data = fetch_data()\n" +
                        "        return jsonify(data), 200\n" +
                        "    except Exception as e:\n" +
                        "        return jsonify(error=str(e)), 500\n" +
                        "\n" +
                        "if __name__ == '__main__':\n" +
                        "    while True:\n" +
                        "        try:\n" +
                        "            app.run()\n" +
                        "        except KeyboardInterrupt:\n" +
                        "            break\n"
        );

        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════
    // Category 5: Trailing Commas
    // ═══════════════════════════════════════════════════════════
    // Very common, idiomatic Python style (PEP 8 / auto-formatters like
    // black routinely produce this) in multi-line literals and calls.

    private static void testTrailingCommas() {
        System.out.println("📋 Category 5: Trailing Commas");
        System.out.println("═===============================================================");

        test("List literal, trailing comma",
                "items = [\n    'a',\n    'b',\n    'c',\n]\n"
        );

        test("Dict literal, trailing comma",
                "person = {\n    'name': 'Ada',\n    'age': 30,\n}\n"
        );

        test("Set literal, trailing comma",
                "tags = {1, 2, 3,}\n"
        );

        test("List of dicts, trailing commas (Flask render_template pattern)",
                "products = [\n" +
                        "    {'name': 'Widget', 'price': 10},\n" +
                        "    {'name': 'Gadget', 'price': 20},\n" +
                        "]\n" +
                        "return render_template('shop.html', products=products)\n"
        );

        test("Function call, trailing comma in kwargs",
                "render_template(\n    'index.html',\n    title=title,\n    items=items,\n)\n"
        );

        test("Function definition, trailing comma in parameters",
                "def greet(\n    name,\n    greeting='Hello',\n):\n    return greeting\n"
        );

        test("Single-element trailing-comma tuple",
                "def f():\n    return a,\n"
        );

        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════
    // Category 6: Generator Expressions & *args / **kwargs
    // ═══════════════════════════════════════════════════════════
    // *args/**kwargs in a function definition is THE standard shape of a
    // Flask decorator wrapper (def wrapper(*args, **kwargs): ...), so this
    // matters directly for this project's own domain.

    private static void testGeneratorExpressionsAndStarArgs() {
        System.out.println("📋 Category 6: Generator Expressions & *args/**kwargs");
        System.out.println("═===============================================================");

        test("Generator expression as sole call argument",
                "total = sum(x for x in items if x > 0)\n"
        );

        test("Function def with *args and **kwargs (Flask decorator pattern)",
                "def wrapper(*args, **kwargs):\n    return func(*args, **kwargs)\n"
        );

        test("Function def mixing normal params with *args/**kwargs",
                "def f(a, b=1, *args, **kwargs):\n    return a\n"
        );

        test("Lambda with *args and **kwargs",
                "f = lambda *args, **kwargs: args\n"
        );

        test("Decorator wrapper calling render_template through *args/**kwargs",
                "def route_wrapper(*args, **kwargs):\n" +
                        "    return render_template('index.html', *args, **kwargs)\n"
        );

        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════
    // Helper Methods
    // ═══════════════════════════════════════════════════════════

    private static void test(String name, String code) {
        System.out.printf("  %-40s ", name + "...");

        try {
            CharStream input = CharStreams.fromString(code);
            FlaskLexer lexer = new FlaskLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            FlaskParser parser = new FlaskParser(tokens);

            parser.removeErrorListeners();
            final boolean[] hasErrors = {false};
            parser.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer,
                                        Object offendingSymbol,
                                        int line, int charPositionInLine,
                                        String msg,
                                        RecognitionException e) {
                    hasErrors[0] = true;
                }
            });

            parser.program();

            if (!hasErrors[0] && parser.getNumberOfSyntaxErrors() == 0) {
                System.out.println("✅");
                passed++;
            } else {
                System.out.println("❌");
                failed++;
            }

        } catch (Exception e) {
            System.out.println("❌ (Exception)");
            failed++;
        }
    }

    private static void printSummary() {
        int total = passed + failed;
        double percentage = (double) passed / total * 100;

        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║                    Final Summary                       ║");
        System.out.println("╠════════════════════════════════════════════════════════╣");
        System.out.printf("║  Total Tests:      %-35d║%n", total);
        System.out.printf("║  ✅ Passed:        %-35d║%n", passed);
        System.out.printf("║  ❌ Failed:        %-35d║%n", failed);
        System.out.printf("║  Success Rate:     %.1f%%                              ║%n", percentage);
        System.out.println("╚════════════════════════════════════════════════════════╝");

        if (failed == 0) {
            System.out.println("\n🎉 PERFECT! Parser is production-ready!");
            System.out.println("   ✅ All Flask features supported");
            System.out.println("   ✅ All test cases passed");
            System.out.println("   ✅ Ready for AST implementation");
        } else {
            System.out.println("\n⚠️  Some tests failed. Review implementation.");
        }
    }
}