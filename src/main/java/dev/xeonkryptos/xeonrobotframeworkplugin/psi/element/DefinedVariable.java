package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DefinedVariable extends LookupElementMarker {

   boolean matches(@Nullable String text);

   boolean isInScope(@NotNull PsiElement position);

   @Nullable
   PsiElement reference();

   @Nullable
   String getLookup();
}
