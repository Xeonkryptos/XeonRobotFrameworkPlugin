package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator.veto;

import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;

public class RobotRenameVeto implements Condition<PsiElement> {

    @Override
    public boolean value(PsiElement element) {
        return element instanceof RobotPositionalArgument;
    }
}
