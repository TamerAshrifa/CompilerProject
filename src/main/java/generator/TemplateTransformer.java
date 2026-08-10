package generator;

import java.util.ArrayList;
import java.util.List;

// Template AST imports
import template.ast.TemplateProgramNode;
import template.ast.html.HtmlNode;
import template.ast.html.HtmlElementNode;
import template.ast.html.HtmlAttributeNode;
import template.ast.html.HtmlTextNode;
import template.ast.html.HtmlCommentNode;

// Jinja2 AST imports
import template.ast.jinja.JinjaNode;
import template.ast.jinja.JinjaProgramNode;
import template.ast.jinja.JinjaVariableNode;
import template.ast.jinja.JinjaExpressionNode;
import template.ast.jinja.JinjaFilterNode;
import template.ast.jinja.JinjaIfNode;
import template.ast.jinja.JinjaElifNode;
import template.ast.jinja.JinjaElseNode;
import template.ast.jinja.JinjaForNode;
import template.ast.jinja.JinjaBlockNode;
import template.ast.jinja.JinjaMacroNode;
import template.ast.jinja.JinjaCommentNode;
import template.ast.jinja.JinjaExtendsNode;
import template.ast.jinja.JinjaIncludeNode;
import template.ast.jinja.LiteralNode;

// Visitor imports
import template.visitor.TemplateBaseVisitor;

/**
 * Transforms Template AST by substituting Jinja2 variables with their runtime values.
 * 
 * This is the core transformation performed by the Generator phase.
 * It walks the Template AST and replaces JinjaVariableNode instances
 * with LiteralNode instances using values from the Context.
 * 
 * Transformation example:
 *   Template AST before:  TemplateProgramNode
 *                           → JinjaProgramNode
 *                             → JinjaVariableNode("name") 
 *   
 *   Template AST after:   TemplateProgramNode
 *                           → JinjaProgramNode
 *                             → LiteralNode("Ali")
 * 
 * Only performs AST transformation, does NOT generate HTML.
 */
public class TemplateTransformer extends TemplateBaseVisitor {
    
    private final Context context;
    private TemplateProgramNode transformedProgram;
    
    /**
     * Create a transformer with a context.
     * 
     * @param context The runtime context with variable bindings
     */
    public TemplateTransformer(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.context = context;
    }
    
    /**
     * Transform a Template AST using the context.
     * 
     * @param program The Template AST to transform
     * @return Transformed Template AST with variables substituted
     */
    public TemplateProgramNode transform(TemplateProgramNode program) {
        if (program == null) {
            return null;
        }
        
        // Transform HTML elements
        List<HtmlNode> transformedHtmlElements = new ArrayList<>();
        for (HtmlNode htmlNode : program.getHtmlElements()) {
            HtmlNode transformed = transformHtmlNode(htmlNode);
            if (transformed != null) {
                transformedHtmlElements.add(transformed);
            }
        }
        
        // Transform Jinja2 elements
        List<JinjaNode> transformedJinjaElements = new ArrayList<>();
        for (JinjaNode jinjaNode : program.getJinjaElements()) {
            JinjaNode transformed = transformJinjaNode(jinjaNode);
            if (transformed != null) {
                transformedJinjaElements.add(transformed);
            }
        }
        
        // Create new program with transformed elements
        TemplateProgramNode result = new TemplateProgramNode();
        
        for (HtmlNode htmlNode : transformedHtmlElements) {
            result.addHtmlElement(htmlNode);
        }
        
        for (JinjaNode jinjaNode : transformedJinjaElements) {
            result.addJinjaElement(jinjaNode);
        }
        
        return result;
    }
    
    /**
     * Transform an HTML node.
     * 
     * Recursively transforms all child nodes.
     */
    private HtmlNode transformHtmlNode(HtmlNode node) {
        if (node instanceof HtmlElementNode) {
            HtmlElementNode elemNode = (HtmlElementNode) node;
            
            // Transform attributes
            List<HtmlAttributeNode> transformedAttrs = new ArrayList<>();
            for (HtmlAttributeNode attr : elemNode.getAttributes()) {
                transformedAttrs.add(attr);
            }
            
            // Transform children
            List<HtmlNode> transformedChildren = new ArrayList<>();
            for (HtmlNode child : elemNode.getChildren()) {
                HtmlNode transformed = transformHtmlNode(child);
                if (transformed != null) {
                    transformedChildren.add(transformed);
                }
            }
            
            return new HtmlElementNode(
                elemNode.getTagName(),
                transformedAttrs,
                transformedChildren,
                elemNode.isSelfClosing(),
                elemNode.getLine(),
                elemNode.getColumn()
            );
        }
        
        // For text, comment, and attribute nodes, return as-is
        return node;
    }
    
    /**
     * Transform a Jinja2 node.
     * 
     * Core logic: JinjaVariableNode → LiteralNode if variable exists in context
     * Recursively transforms nested nodes.
     */
    private JinjaNode transformJinjaNode(JinjaNode node) {
        if (node instanceof JinjaVariableNode) {
            return transformJinjaVariable((JinjaVariableNode) node);
        }
        
        if (node instanceof JinjaIfNode) {
            return transformJinjaIf((JinjaIfNode) node);
        }
        
        if (node instanceof JinjaForNode) {
            return transformJinjaFor((JinjaForNode) node);
        }
        
        if (node instanceof JinjaBlockNode) {
            return transformJinjaBlock((JinjaBlockNode) node);
        }
        
        if (node instanceof JinjaMacroNode) {
            return transformJinjaMacro((JinjaMacroNode) node);
        }
        
        if (node instanceof JinjaProgramNode) {
            return transformJinjaProgram((JinjaProgramNode) node);
        }
        
        // For other node types (expressions, extends, include, comment), return as-is
        return node;
    }
    
    /**
     * Transform JinjaVariableNode.
     * 
     * If variable exists in context, replace with LiteralNode.
     * Otherwise, keep the variable node unchanged.
     */
    private JinjaNode transformJinjaVariable(JinjaVariableNode node) {
        String varName = node.getVariableName();
        
        // Look up variable in context
        if (context.has(varName)) {
            Object value = context.get(varName);
            return new LiteralNode(value, node.getLine(), node.getColumn());
        }
        
        // Variable not in context, keep as-is
        return node;
    }
    
    /**
     * Transform JinjaIfNode.
     * 
     * Recursively transforms all branches.
     */
    private JinjaNode transformJinjaIf(JinjaIfNode node) {
        // Transform then branch
        List<JinjaNode> thenBranch = new ArrayList<>();
        for (JinjaNode child : node.getThenBody()) {
            JinjaNode transformed = transformJinjaNode(child);
            if (transformed != null) {
                thenBranch.add(transformed);
            }
        }

        List<JinjaElifNode> elifBranches = new ArrayList<>();
        for (JinjaElifNode elif : node.getElifNodes()) {
            List<JinjaNode> elifBody = new ArrayList<>();
            for (JinjaNode child : elif.getBody()) {
                JinjaNode transformed = transformJinjaNode(child);
                if (transformed != null) {
                    elifBody.add(transformed);
                }
            }
            elifBranches.add(new JinjaElifNode(elif.getCondition(), elifBody, elif.getLine(), elif.getColumn()));
        }

        JinjaElseNode elseBranch = null;
        if (node.hasElse()) {
            List<JinjaNode> elseBody = new ArrayList<>();
            for (JinjaNode child : node.getElseNode().getBody()) {
                JinjaNode transformed = transformJinjaNode(child);
                if (transformed != null) {
                    elseBody.add(transformed);
                }
            }
            elseBranch = new JinjaElseNode(elseBody, node.getElseNode().getLine(), node.getElseNode().getColumn());
        }

        return new JinjaIfNode(node.getCondition(), thenBranch, elifBranches, elseBranch, node.getLine(), node.getColumn());
    }
    
    /**
     * Transform JinjaForNode.
     * 
     * Recursively transforms loop body and else clause.
     */
    private JinjaNode transformJinjaFor(JinjaForNode node) {
        // Transform loop body
        List<JinjaNode> body = new ArrayList<>();
        for (JinjaNode child : node.getBody()) {
            JinjaNode transformed = transformJinjaNode(child);
            if (transformed != null) {
                body.add(transformed);
            }
        }
        
        // Transform else clause if present
        JinjaNode elseBranch = null;
        if (node.hasElse()) {
            elseBranch = transformJinjaNode(node.getElseBody().get(0));
        }
        
        JinjaForNode result = new JinjaForNode(node.getLoopVariable(), node.getIterable(), body, node.getLine(), node.getColumn());
        if (elseBranch != null) {
            // Note: Current API may need refactoring to support else clause
        }
        
        return result;
    }
    
    /**
     * Transform JinjaBlockNode.
     * 
     * Recursively transforms block content.
     */
    private JinjaNode transformJinjaBlock(JinjaBlockNode node) {
        List<JinjaNode> content = new ArrayList<>();
        for (JinjaNode child : node.getBody()) {
            JinjaNode transformed = transformJinjaNode(child);
            if (transformed != null) {
                content.add(transformed);
            }
        }
        
        return new JinjaBlockNode(node.getBlockName(), content, node.getLine(), node.getColumn());
    }
    
    /**
     * Transform JinjaMacroNode.
     * 
     * Recursively transforms macro body.
     */
    private JinjaNode transformJinjaMacro(JinjaMacroNode node) {
        List<JinjaNode> body = new ArrayList<>();
        for (JinjaNode child : node.getBody()) {
            JinjaNode transformed = transformJinjaNode(child);
            if (transformed != null) {
                body.add(transformed);
            }
        }
        
        return new JinjaMacroNode(node.getMacroName(), node.getParameters(), body, node.getLine(), node.getColumn());
    }
    
    /**
     * Transform JinjaProgramNode.
     * 
     * Recursively transforms all elements.
     */
    private JinjaNode transformJinjaProgram(JinjaProgramNode node) {
        List<JinjaNode> elements = new ArrayList<>();
        for (JinjaNode child : node.getElements()) {
            JinjaNode transformed = transformJinjaNode(child);
            if (transformed != null) {
                elements.add(transformed);
            }
        }
        
        return new JinjaProgramNode(elements);
    }
    
    /**
     * Get the context used for transformation.
     * 
     * @return The transformation context
     */
    public Context getContext() {
        return context;
    }
}
