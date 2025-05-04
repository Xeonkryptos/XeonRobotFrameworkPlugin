package com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref;

import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyDecorator;
import com.jetbrains.python.psi.PyDecoratorList;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyFunction;
import org.jetbrains.annotations.NotNull;

public class PyElementDeprecatedVisitor extends PyElementVisitor {

    private boolean deprecated = false;

    @Override
    public void visitPyClass(@NotNull PyClass node) {
        node.acceptChildren(this);
    }

    @Override
    public void visitPyFunction(@NotNull PyFunction node) {
        node.acceptChildren(this);
    }

    @Override
    public void visitPyDecoratorList(@NotNull PyDecoratorList node) {
        node.acceptChildren(this);
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void visitPyDecorator(@NotNull PyDecorator node) {
        String decoratorName = node.getName();
        if ("deprecated".equals(decoratorName)) {
            deprecated = true;
        }
    }

    public boolean isDeprecated() {
        return deprecated;
    }
}
