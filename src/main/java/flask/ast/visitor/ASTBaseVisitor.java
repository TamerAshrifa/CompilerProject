package flask.ast.visitor;

import flask.ast.nodes.Expression;
import flask.ast.nodes.Statement;
import flask.ast.nodes.expressions.access.AttributeAccessNode;
import flask.ast.nodes.expressions.access.FunctionCallNode;
import flask.ast.nodes.expressions.access.SubscriptNode;
import flask.ast.nodes.expressions.comprehensions.ComprehensionNode;
import flask.ast.nodes.expressions.comprehensions.DictComprehensionNode;
import flask.ast.nodes.expressions.comprehensions.GeneratorExpressionNode;
import flask.ast.nodes.expressions.comprehensions.ListComprehensionNode;
import flask.ast.nodes.expressions.comprehensions.SetComprehensionNode;
import flask.ast.nodes.expressions.atoms.DictNode;
import flask.ast.nodes.expressions.atoms.IdentifierNode;
import flask.ast.nodes.expressions.atoms.LiteralNode;
import flask.ast.nodes.expressions.atoms.ListNode;
import flask.ast.nodes.expressions.atoms.SetNode;
import flask.ast.nodes.expressions.atoms.TupleNode;
import flask.ast.nodes.expressions.operations.BinaryOpNode;
import flask.ast.nodes.expressions.operations.CompareNode;
import flask.ast.nodes.expressions.operations.LambdaNode;
import flask.ast.nodes.expressions.operations.UnaryOpNode;
import flask.ast.nodes.helpers.Decorator;
import flask.ast.nodes.helpers.ExceptClause;
import flask.ast.nodes.helpers.Parameter;
import flask.ast.nodes.helpers.WithItem;
import flask.ast.nodes.statements.ProgramNode;
import flask.ast.nodes.statements.compound.ClassDefNode;
import flask.ast.nodes.statements.compound.ForStatementNode;
import flask.ast.nodes.statements.compound.FunctionDefNode;
import flask.ast.nodes.statements.compound.IfStatementNode;
import flask.ast.nodes.statements.compound.TryStatementNode;
import flask.ast.nodes.statements.compound.WhileStatementNode;
import flask.ast.nodes.statements.compound.WithStatementNode;
import flask.ast.nodes.statements.imports.FromImportNode;
import flask.ast.nodes.statements.imports.ImportNode;
import flask.ast.nodes.statements.simple.*;

public abstract class ASTBaseVisitor<T> implements ASTVisitor<T> {

    // ========================================
    // Helper Methods
    // ========================================

    /**
     * Default return value (can be overridden)
     * By default returns null
     */
    protected T defaultResult() {
        return null;
    }

    /**
     * Combine results from multiple visits
     * Useful for aggregating results from child nodes
     * By default returns the next result
     */
    protected T aggregateResult(T aggregate, T nextResult) {
        return nextResult;
    }

    // ========================================
    // Program
    // ========================================

    @Override
    public T visitProgram(ProgramNode node) {
        T result = defaultResult();
        for (Statement stmt : node.getStatements()) {
            T stmtResult = stmt.accept(this);
            result = aggregateResult(result, stmtResult);
        }
        return result;
    }

    // ========================================
    // Simple Statements
    // ========================================

    @Override
    public T visitAssignment(AssignmentNode node) {
        node.getTarget().accept(this);
        node.getValue().accept(this);
        return defaultResult();
    }

    @Override
    public T visitExpressionStatement(ExpressionStatementNode node) {
        node.getExpression().accept(this);
        return defaultResult();
    }

    @Override
    public T visitReturn(ReturnNode node) {
        for (Expression value : node.getValues()) {
            value.accept(this);
        }
        return defaultResult();
    }

    @Override
    public T visitPass(PassNode node) {
        return defaultResult();
    }

    @Override
    public T visitBreak(BreakNode node) {
        return defaultResult();
    }

    @Override
    public T visitContinue(ContinueNode node) {
        return defaultResult();
    }

    @Override
    public T visitDel(DelNode node) {
        for (Expression target : node.getTargets()) {
            target.accept(this);
        }
        return defaultResult();
    }

    @Override
    public T visitAssert(AssertNode node) {
        node.getTest().accept(this);
        if (node.hasMessage()) {
            node.getMessage().accept(this);
        }
        return defaultResult();
    }

    @Override
    public T visitGlobal(GlobalNode node) {
        // Global names are just strings, nothing to visit
        return defaultResult();
    }

    @Override
    public T visitNonlocal(NonlocalNode node) {
        // Nonlocal names are just strings, nothing to visit
        return defaultResult();
    }

    @Override
    public T visitRaise(RaiseNode node) {
        if (!node.isBareRaise()) {
            node.getException().accept(this);
            if (node.hasCause()) {
                node.getCause().accept(this);
            }
        }
        return defaultResult();
    }

    // ========================================
    // Import Statements
    // ========================================

    @Override
    public T visitImport(ImportNode node) {
        // Import names are strings, nothing to visit
        return defaultResult();
    }

    @Override
    public T visitFromImport(FromImportNode node) {
        // Import names are strings, nothing to visit
        return defaultResult();
    }

    // ========================================
    // Compound Statements
    // ========================================

    @Override
    public T visitFunctionDef(FunctionDefNode node) {
        // Visit decorators
        for (Decorator decorator : node.getDecorators()) {
            decorator.getName().accept(this);
            for (Expression arg : decorator.getArgs()) {
                arg.accept(this);
            }
            for (Expression kwarg : decorator.getKwargs().values()) {
                kwarg.accept(this);
            }
        }

        // Visit parameters (default values)
        for (Parameter param : node.getParameters()) {
            if (param.hasDefault()) {
                param.getDefaultValue().accept(this);
            }
            if (param.hasTypeHint()) {
                param.getTypeHint().accept(this);
            }
        }

        // Visit return type hint
        if (node.hasReturnType()) {
            node.getReturnType().accept(this);
        }

        // Visit body
        T result = defaultResult();
        for (Statement stmt : node.getBody()) {
            T stmtResult = stmt.accept(this);
            result = aggregateResult(result, stmtResult);
        }

        return result;
    }

    @Override
    public T visitIfStatement(IfStatementNode node) {
        // Visit main condition
        node.getCondition().accept(this);

        // Visit then body
        T result = defaultResult();
        for (Statement stmt : node.getThenBody()) {
            T stmtResult = stmt.accept(this);
            result = aggregateResult(result, stmtResult);
        }

        // Visit elif clauses
        for (IfStatementNode.ElifClause elifClause : node.getElifClauses()) {
            elifClause.getCondition().accept(this);
            for (Statement stmt : elifClause.getBody()) {
                T stmtResult = stmt.accept(this);
                result = aggregateResult(result, stmtResult);
            }
        }

        // Visit else body
        if (node.hasElse()) {
            for (Statement stmt : node.getElseBody()) {
                T stmtResult = stmt.accept(this);
                result = aggregateResult(result, stmtResult);
            }
        }

        return result;
    }

    @Override
    public T visitForStatement(ForStatementNode node) {
        // Visit target and iterable
        node.getTarget().accept(this);
        node.getIterable().accept(this);

        // Visit body
        T result = defaultResult();
        for (Statement stmt : node.getBody()) {
            T stmtResult = stmt.accept(this);
            result = aggregateResult(result, stmtResult);
        }

        // Visit else body
        if (node.hasElse()) {
            for (Statement stmt : node.getElseBody()) {
                T stmtResult = stmt.accept(this);
                result = aggregateResult(result, stmtResult);
            }
        }

        return result;
    }

    @Override
    public T visitWhileStatement(WhileStatementNode node) {
        // Visit condition
        node.getCondition().accept(this);

        // Visit body
        T result = defaultResult();
        for (Statement stmt : node.getBody()) {
            T stmtResult = stmt.accept(this);
            result = aggregateResult(result, stmtResult);
        }

        // Visit else body
        if (node.hasElse()) {
            for (Statement stmt : node.getElseBody()) {
                T stmtResult = stmt.accept(this);
                result = aggregateResult(result, stmtResult);
            }
        }

        return result;
    }

    @Override
    public T visitWithStatement(WithStatementNode node) {
        // Visit with items
        for (WithItem item : node.getItems()) {
            item.getContextExpr().accept(this);
            if (item.hasAsName()) {
                item.getAsName().accept(this);
            }
        }

        // Visit body
        T result = defaultResult();
        for (Statement stmt : node.getBody()) {
            T stmtResult = stmt.accept(this);
            result = aggregateResult(result, stmtResult);
        }

        return result;
    }

    @Override
    public T visitTryStatement(TryStatementNode node) {
        T result = defaultResult();

        // Visit try body
        for (Statement stmt : node.getTryBody()) {
            T stmtResult = stmt.accept(this);
            result = aggregateResult(result, stmtResult);
        }

        // Visit except clauses
        for (ExceptClause exceptClause : node.getExceptClauses()) {
            if (!exceptClause.isBareExcept()) {
                exceptClause.getExceptionType().accept(this);
            }
            for (Statement stmt : exceptClause.getBody()) {
                T stmtResult = stmt.accept(this);
                result = aggregateResult(result, stmtResult);
            }
        }

        // Visit else body
        if (node.hasElse()) {
            for (Statement stmt : node.getElseBody()) {
                T stmtResult = stmt.accept(this);
                result = aggregateResult(result, stmtResult);
            }
        }

        // Visit finally body
        if (node.hasFinally()) {
            for (Statement stmt : node.getFinallyBody()) {
                T stmtResult = stmt.accept(this);
                result = aggregateResult(result, stmtResult);
            }
        }

        return result;
    }

    @Override
    public T visitClassDef(ClassDefNode node) {
        // Visit decorators
        for (Decorator decorator : node.getDecorators()) {
            decorator.getName().accept(this);
            for (Expression arg : decorator.getArgs()) {
                arg.accept(this);
            }
            for (Expression kwarg : decorator.getKwargs().values()) {
                kwarg.accept(this);
            }
        }

        // Visit base classes
        for (Expression base : node.getBases()) {
            base.accept(this);
        }

        // Visit body
        T result = defaultResult();
        for (Statement stmt : node.getBody()) {
            T stmtResult = stmt.accept(this);
            result = aggregateResult(result, stmtResult);
        }

        return result;
    }

    // ========================================
    // Expression Operations
    // ========================================

    @Override
    public T visitBinaryOp(BinaryOpNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        return defaultResult();
    }
    @Override
    public T visitCompare(CompareNode node) {
        // Visit left expression
        node.getLeft().accept(this);

        // Visit all comparators
        for (Expression comparator : node.getComparators()) {
            comparator.accept(this);
        }

        return defaultResult();
    }

    @Override
    public T visitLambda(LambdaNode node) {
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }
        return defaultResult();
    }

    @Override
    public T visitComprehension(ComprehensionNode node) {
        T result = defaultResult();
        if (node instanceof ListComprehensionNode listComprehensionNode) {
            result = aggregateResult(result, listComprehensionNode.getElement().accept(this));
        } else if (node instanceof SetComprehensionNode setComprehensionNode) {
            result = aggregateResult(result, setComprehensionNode.getElement().accept(this));
        } else if (node instanceof GeneratorExpressionNode generatorExpressionNode) {
            result = aggregateResult(result, generatorExpressionNode.getElement().accept(this));
        } else if (node instanceof DictComprehensionNode dictComprehensionNode) {
            result = aggregateResult(result, dictComprehensionNode.getKey().accept(this));
            result = aggregateResult(result, dictComprehensionNode.getValue().accept(this));
        }
        for (ComprehensionNode.ForClause clause : node.getClauses()) {
            result = aggregateResult(result, clause.getTarget().accept(this));
            result = aggregateResult(result, clause.getIterable().accept(this));
            for (Expression condition : clause.getConditions()) {
                result = aggregateResult(result, condition.accept(this));
            }
        }
        return result;
    }


    @Override
    public T visitUnaryOp(UnaryOpNode node) {
        node.getOperand().accept(this);
        return defaultResult();
    }

    // ========================================
    // Expression Atoms
    // ========================================

    @Override
    public T visitIdentifier(IdentifierNode node) {
        // Leaf node, nothing to visit
        return defaultResult();
    }

    @Override
    public T visitLiteral(LiteralNode node) {
        // Leaf node, nothing to visit
        return defaultResult();
    }

    @Override
    public T visitList(ListNode node) {
        for (Expression elem : node.getElements()) {
            elem.accept(this);
        }
        return defaultResult();
    }

    @Override
    public T visitDict(DictNode node) {
        for (DictNode.DictItem item : node.getItems()) {
            item.getKey().accept(this);
            item.getValue().accept(this);
        }
        return defaultResult();
    }

    @Override
    public T visitTuple(TupleNode node) {
        for (Expression elem : node.getElements()) {
            elem.accept(this);
        }
        return defaultResult();
    }

    @Override
    public T visitSet(SetNode node) {
        for (Expression elem : node.getElements()) {
            elem.accept(this);
        }
        return defaultResult();
    }

    // ========================================
    // Expression Access
    // ========================================

    @Override
    public T visitAttributeAccess(AttributeAccessNode node) {
        node.getObject().accept(this);
        return defaultResult();
    }

    @Override
    public T visitFunctionCall(FunctionCallNode node) {
        node.getFunction().accept(this);

        // Visit positional arguments
        for (Expression arg : node.getArgs()) {
            arg.accept(this);
        }

        // Visit keyword arguments
        for (Expression kwarg : node.getKwargs().values()) {
            kwarg.accept(this);
        }

        return defaultResult();
    }

    @Override
    public T visitSubscript(SubscriptNode node) {
        node.getObject().accept(this);
        node.getIndex().accept(this);
        return defaultResult();
    }
}