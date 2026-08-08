package template.visitor;

import template.ast.*;

/**
 * Visitor that prints the Template AST in a tree-like format
 * Similar to Flask's ASTPrinter for visualization and debugging
 */
public class TemplatePrintVisitor extends TemplateBaseVisitor<Void> {

    private StringBuilder output;
    private int indentLevel;
    private static final String BRANCH = " ├── ";
    private static final String LAST_BRANCH = " └── ";
    private static final String CONTINUE = " │   ";
    private static final String EMPTY = "     ";

    public TemplatePrintVisitor() {
        this.output = new StringBuilder();
        this.indentLevel = 0;
    }

    /**
     * Reset the visitor for printing a new tree
     */
    public void reset() {
        this.output = new StringBuilder();
        this.indentLevel = 0;
    }

    /**
     * Get the accumulated output
     */
    public String getOutput() {
        return output.toString();
    }

    /**
     * Print a node with optional attributes
     */
    private void printNode(String nodeName, String attributes) {
        String indent = getIndent();
        output.append(indent);
        if (nodeName != null) {
            output.append(nodeName);
            if (attributes != null && !attributes.isEmpty()) {
                output.append(" ").append(attributes);
            }
        }
        output.append("\n");
    }

    /**
     * Print a node without attributes
     */
    private void printNode(String nodeName) {
        printNode(nodeName, null);
    }

    /**
     * Get the current indentation string
     */
    private String getIndent() {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            indent.append("  ");
        }
        return indent.toString();
    }

    /**
     * Increase indentation level
     */
    private void increaseIndent() {
        indentLevel++;
    }

    /**
     * Decrease indentation level
     */
    private void decreaseIndent() {
        if (indentLevel > 0) {
            indentLevel--;
        }
    }

    /**
     * Format line number for display
     */
    private String formatLineNumber(int line) {
        return line > 0 ? "(line " + line + ")" : "";
    }

    // ========================================
    // Program
    // ========================================

    @Override
    public Void visitProgram(TemplateProgramNode node) {
        printNode("TemplateProgramNode", formatLineNumber(node.getLine()));
        increaseIndent();
        for (template.ast.html.HtmlNode element : node.getHtmlElements()) {
            element.accept(this);
        }
        for (template.ast.jinja.JinjaNode element : node.getJinjaElements()) {
            element.accept(this);
        }
        decreaseIndent();
        return null;
    }

    @Override
    public Void visitRule(TemplateRuleNode node) {
        printNode(node.getRuleName(), formatLineNumber(node.getLine()));
        increaseIndent();
        for (TemplateNode child : node.getChildren()) {
            child.accept(this);
        }
        decreaseIndent();
        return null;
    }

    // ========================================
    // Text and Content
    // ========================================

    @Override
    public Void visitText(TextNode node) {
        String content = node.getContent();
        // Escape content for display
        String escaped = content.replace("\n", "\\n").replace("\t", "\\t").replace("\r", "\\r");
        if (escaped.length() > 40) {
            escaped = escaped.substring(0, 40) + "...";
        }
        printNode("TextNode", "content=\"" + escaped + "\" " + formatLineNumber(node.getLine()));
        return null;
    }

    @Override
    public Void visitExpression(ExpressionNode node) {
        String expr = node.getExpression();
        if (expr != null && expr.length() > 40) {
            expr = expr.substring(0, 40) + "...";
        }
        printNode("ExpressionNode", "expression=\"" + expr + "\" " + formatLineNumber(node.getLine()));
        return null;
    }

    // ========================================
    // Variables and Filters
    // ========================================

    @Override
    public Void visitVariable(VariableNode node) {
        printNode("VariableNode", "name=\"" + node.getVariableName() + "\" " + formatLineNumber(node.getLine()));
        increaseIndent();
        
        // Visit filters
        if (node.hasFilters()) {
            for (FilterNode filter : node.getFilters()) {
                filter.accept(this);
            }
        }
        
        decreaseIndent();
        return null;
    }

    @Override
    public Void visitFilter(FilterNode node) {
        String attrs = "filter=\"" + node.getFilterName() + "\"";
        if (node.hasArguments()) {
            attrs += " arguments=\"" + node.getArguments() + "\"";
        }
        attrs += " " + formatLineNumber(node.getLine());
        printNode("FilterNode", attrs);
        return null;
    }

    // ========================================
    // Control Flow
    // ========================================

    @Override
    public Void visitIf(IfNode node) {
        printNode("IfNode", formatLineNumber(node.getLine()));
        increaseIndent();
        
        // Print condition
        if (node.getCondition() != null) {
            printNode("Condition:");
            increaseIndent();
            printNode(node.getCondition());
            decreaseIndent();
        }
        
        // Print then body
        if (!node.getThenBody().isEmpty()) {
            printNode("ThenBody:");
            increaseIndent();
            for (TemplateNode bodyNode : node.getThenBody()) {
                bodyNode.accept(this);
            }
            decreaseIndent();
        }
        
        // Print elif clauses
        for (IfNode.ElifClause elifClause : node.getElifClauses()) {
            printNode("ElifClause:");
            increaseIndent();
            
            if (elifClause.condition != null) {
                printNode("Condition:");
                increaseIndent();
                printNode(elifClause.condition);
                decreaseIndent();
            }
            
            if (!elifClause.body.isEmpty()) {
                printNode("Body:");
                increaseIndent();
                for (TemplateNode bodyNode : elifClause.body) {
                    bodyNode.accept(this);
                }
                decreaseIndent();
            }
            
            decreaseIndent();
        }
        
        // Print else body
        if (node.hasElse() && node.getElseBody() != null && !node.getElseBody().isEmpty()) {
            printNode("ElseBody:");
            increaseIndent();
            for (TemplateNode bodyNode : node.getElseBody()) {
                bodyNode.accept(this);
            }
            decreaseIndent();
        }
        
        decreaseIndent();
        return null;
    }

    @Override
    public Void visitFor(ForNode node) {
        String attrs = "loopVar=\"" + node.getLoopVariable() + "\"";
        if (node.getIterable() != null) {
            attrs += " iterable=\"" + node.getIterable() + "\"";
        }
        attrs += " " + formatLineNumber(node.getLine());
        printNode("ForNode", attrs);
        increaseIndent();
        
        // Print body
        if (!node.getBody().isEmpty()) {
            printNode("Body:");
            increaseIndent();
            for (TemplateNode bodyNode : node.getBody()) {
                bodyNode.accept(this);
            }
            decreaseIndent();
        }
        
        // Print else clause if present
        if (node.hasElse() && node.getElseBody() != null && !node.getElseBody().isEmpty()) {
            printNode("ElseBody:");
            increaseIndent();
            for (TemplateNode bodyNode : node.getElseBody()) {
                bodyNode.accept(this);
            }
            decreaseIndent();
        }
        
        decreaseIndent();
        return null;
    }

    // ========================================
    // Template Inheritance
    // ========================================

    @Override
    public Void visitBlock(BlockNode node) {
        printNode("BlockNode", "name=\"" + node.getBlockName() + "\" " + formatLineNumber(node.getLine()));
        increaseIndent();
        
        if (node.hasBody()) {
            for (TemplateNode bodyNode : node.getBody()) {
                bodyNode.accept(this);
            }
        }
        
        decreaseIndent();
        return null;
    }

    @Override
    public Void visitExtends(ExtendsNode node) {
        printNode("ExtendsNode", "parentTemplate=\"" + node.getParentTemplatePath() + "\" " + formatLineNumber(node.getLine()));
        return null;
    }

    @Override
    public Void visitInclude(IncludeNode node) {
        String attrs = "template=\"" + node.getTemplatePath() + "\"";
        if (node.isWithContext()) {
            attrs += " withContext=true";
        }
        attrs += " " + formatLineNumber(node.getLine());
        printNode("IncludeNode", attrs);
        return null;
    }
}
