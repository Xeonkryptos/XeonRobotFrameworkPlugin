package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;

public interface RobotNamedElementExpression extends PsiNamedElement {

    @NotNull
    @Override
    String getName();
}
