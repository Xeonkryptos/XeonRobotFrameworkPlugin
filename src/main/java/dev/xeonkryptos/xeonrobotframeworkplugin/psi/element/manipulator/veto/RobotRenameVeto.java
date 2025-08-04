package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator.veto;

import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;

import java.util.Optional;

public class RobotRenameVeto implements Condition<PsiElement> {

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public boolean value(PsiElement element) {
        if (element instanceof RobotKeywordCallName keywordCallName) {
            PsiElement resolvedElement = keywordCallName.getReference().resolve();
            if (resolvedElement instanceof PyFunction pyFunction) {
                return Optional.ofNullable(pyFunction.getDecoratorList())
                               .map(pyDecoratorList -> pyDecoratorList.findDecorator("keyword"))
                               .map(decorator -> decorator.getArgument(0, "name", PyStringLiteralExpression.class))
                               .isPresent();
            }
        }
        return false;
    }
}
