package template.ast;

import template.visitor.TemplateVisitor;

public abstract class TemplateNode {

    private final int line;
    private final int column;

    protected TemplateNode() {
        this(0, 0);
    }

    protected TemplateNode(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public abstract <T> T accept(TemplateVisitor<T> visitor);
}