package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiQualifiedNamedElement;
import org.jetbrains.annotations.NotNull;

public interface RobotQualifiedNameOwner extends PsiQualifiedNamedElement {

    @NotNull
    String getQualifiedName();
}
