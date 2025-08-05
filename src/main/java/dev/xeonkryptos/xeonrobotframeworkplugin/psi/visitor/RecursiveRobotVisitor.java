package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

public abstract class RecursiveRobotVisitor extends RobotVisitor {

    @Override
    public void visitElement(@NotNull PsiElement element) {
        super.visitElement(element);
        element.acceptChildren(this);
    }
}
