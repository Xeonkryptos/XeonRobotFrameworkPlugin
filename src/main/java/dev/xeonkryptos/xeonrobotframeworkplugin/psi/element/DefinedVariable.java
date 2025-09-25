package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.VariableScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DefinedVariable extends LookupElementMarker {

   boolean matches(@Nullable String text);

   boolean isInScope(@NotNull PsiElement position);

   VariableScope getScope();
}
