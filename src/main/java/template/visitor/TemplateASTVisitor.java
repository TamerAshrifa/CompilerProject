package template.visitor;

/**
 * AST Visitor interface for Template nodes
 * Extends TemplateVisitor to provide an explicit AST naming convention
 * 
 * This interface is equivalent to Flask's ASTVisitor
 * Implementations can extend TemplateBaseVisitor for default behavior
 */
public interface TemplateASTVisitor<T> extends TemplateVisitor<T> {
    // All visitor methods are inherited from TemplateVisitor
}
