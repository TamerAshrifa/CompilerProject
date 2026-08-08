package flask.test;

import flask.ast.builder.FlaskASTBuilder;
import flask.ast.nodes.ASTNode;
import flask.ast.nodes.Statement;
import flask.ast.nodes.statements.ProgramNode;
import flask.ast.nodes.statements.compound.ClassDefNode;
import flask.ast.nodes.statements.compound.ForStatementNode;
import flask.ast.nodes.statements.compound.FunctionDefNode;
import flask.ast.nodes.statements.compound.IfStatementNode;
import flask.ast.nodes.statements.compound.TryStatementNode;
import flask.ast.nodes.statements.compound.WhileStatementNode;
import flask.ast.nodes.statements.compound.WithStatementNode;
import flask.ast.nodes.helpers.ExceptClause;
import grammar.flask.FlaskLexer;
import grammar.flask.FlaskParser;
import org.antlr.v4.runtime.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * كلاس مخصص لبناء وطباعة AST (Abstract Syntax Tree)
 * يقرأ ملف Python/Flask ويبني AST ثم يطبعه بشكل هرمي يوضح كل عقدة
 * مع اسمها (getNodeName) ورقم سطرها (getLine), إثباتاً حياً للمتطلب 3.
 */
public class TestFlaskParser {

    public static void main(String[] args) {
        try {
            String filePath = "src/main/java/flask/test/test_flask.py";
            String pythonCode = new String(Files.readAllBytes(Paths.get(filePath)));
            buildAndPrintAST(pythonCode);
        } catch (IOException e) {
            System.err.println("خطأ في قراءة الملف: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("خطأ في بناء AST: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void buildAndPrintAST(String pythonCode) {
        System.out.println("====================================================================");
        System.out.println("           بناء وطباعة AST (Abstract Syntax Tree)");
        System.out.println("====================================================================");

        // Step 1: Lexical analysis
        CharStream input = CharStreams.fromString(pythonCode);
        FlaskLexer lexer = new FlaskLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Step 2: Syntactic analysis
        FlaskParser parser = new FlaskParser(tokens);
        FlaskParser.ProgramContext parseTree = parser.program();
        System.out.println("Syntax errors reported by parser: " + parser.getNumberOfSyntaxErrors());

        // Step 3: Build the custom OOP AST from the ANTLR parse tree
        FlaskASTBuilder astBuilder = new FlaskASTBuilder();
        ASTNode root = astBuilder.build(parseTree);

        if (!(root instanceof ProgramNode program)) {
            System.err.println("Root node was not a ProgramNode: " + root);
            return;
        }

        System.out.println();
        System.out.println("--- AST (Abstract Syntax Tree) ---");
        System.out.println("Top-level statements found: " + program.getStatements().size());
        System.out.println();
        for (Statement statement : program.getStatements()) {
            printNode(statement, 0);
        }

        System.out.println();
        System.out.println("====================================================================");
        System.out.println(program.getStatements().isEmpty()
                ? "FAILED: the program tree is empty."
                : "OK: AST built successfully with real, populated statements.");
        System.out.println("====================================================================");
    }

    /**
     * Minimal recursive dump used only to demonstrate, for this test/demo
     * file, that every node correctly carries its own node name and line
     * number (requirement 3) and that nested bodies (if/for/while/try/with/
     * def/class) are populated rather than dropped (requirement 2).
     */
    private static void printNode(ASTNode node, int depth) {
        String indent = "  ".repeat(depth);
        System.out.println(indent + "- " + node.getNodeName() + "  (line " + node.getLine() + ")");

        if (node instanceof FunctionDefNode functionDefNode) {
            if (!functionDefNode.getDecorators().isEmpty()) {
                System.out.println(indent + "    decorators: " + functionDefNode.getDecorators().size());
            }
            for (Statement statement : functionDefNode.getBody()) {
                printNode(statement, depth + 1);
            }
        } else if (node instanceof ClassDefNode classDefNode) {
            for (Statement statement : classDefNode.getBody()) {
                printNode(statement, depth + 1);
            }
        } else if (node instanceof IfStatementNode ifStatementNode) {
            for (Statement statement : ifStatementNode.getThenBody()) {
                printNode(statement, depth + 1);
            }
            for (Statement statement : ifStatementNode.getElseBody()) {
                printNode(statement, depth + 1);
            }
        } else if (node instanceof ForStatementNode forStatementNode) {
            for (Statement statement : forStatementNode.getBody()) {
                printNode(statement, depth + 1);
            }
        } else if (node instanceof WhileStatementNode whileStatementNode) {
            for (Statement statement : whileStatementNode.getBody()) {
                printNode(statement, depth + 1);
            }
        } else if (node instanceof WithStatementNode withStatementNode) {
            for (Statement statement : withStatementNode.getBody()) {
                printNode(statement, depth + 1);
            }
        } else if (node instanceof TryStatementNode tryStatementNode) {
            for (Statement statement : tryStatementNode.getTryBody()) {
                printNode(statement, depth + 1);
            }
            for (ExceptClause exceptClause : tryStatementNode.getExceptClauses()) {
                System.out.println(indent + "  - ExceptClause");
                for (Statement statement : exceptClause.getBody()) {
                    printNode(statement, depth + 2);
                }
            }
            for (Statement statement : tryStatementNode.getFinallyBody()) {
                printNode(statement, depth + 1);
            }
        }
    }
}
