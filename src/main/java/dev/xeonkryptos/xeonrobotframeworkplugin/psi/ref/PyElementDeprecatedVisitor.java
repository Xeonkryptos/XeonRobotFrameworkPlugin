package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyDecorator;
import com.jetbrains.python.psi.PyDecoratorList;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyExpressionStatement;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyStatementList;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames;
import org.jetbrains.annotations.NotNull;

public class PyElementDeprecatedVisitor extends PyElementVisitor {

    private final PyFunction pyFunction;

    private boolean deprecated = false;

    public PyElementDeprecatedVisitor(PyFunction pyFunction) {
        this.pyFunction = pyFunction;
    }

    @Override
    public void visitPyClass(@NotNull PyClass node) {
        super.visitPyClass(node);
        node.acceptChildren(this);
    }

    @Override
    public void visitPyFunction(@NotNull PyFunction node) {
        super.visitPyFunction(node);
        if (node == pyFunction) {
            node.acceptChildren(this);
        }
    }

    @Override
    public void visitPyDecoratorList(@NotNull PyDecoratorList node) {
        super.visitPyDecoratorList(node);
        node.acceptChildren(this);
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void visitPyDecorator(@NotNull PyDecorator node) {
        super.visitPyDecorator(node);
        String decoratorName = node.getName();
        if ("deprecated".equalsIgnoreCase(decoratorName)) {
            deprecated = true;
        }
    }

    @Override
    public void visitPyStatementList(@NotNull PyStatementList node) {
        super.visitPyStatementList(node);
        node.acceptChildren(this);
    }

    @Override
    public void visitPyExpressionStatement(@NotNull PyExpressionStatement node) {
        super.visitPyExpressionStatement(node);
        node.acceptChildren(this);
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void visitPyStringLiteralExpression(@NotNull PyStringLiteralExpression node) {
        super.visitPyStringLiteralExpression(node);
        deprecated |= node.getStringValue().startsWith(RobotNames.DEPRECATED_PREFIX);
    }

    public boolean isDeprecated() {
        return deprecated;
    }
}
