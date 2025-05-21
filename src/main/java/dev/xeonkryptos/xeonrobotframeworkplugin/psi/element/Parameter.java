package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.NotNull;

public interface Parameter extends Argument, PsiNameIdentifierOwner {

    String getParameterName();

    @NotNull
    @Override
    ParameterId getNameIdentifier();
}
