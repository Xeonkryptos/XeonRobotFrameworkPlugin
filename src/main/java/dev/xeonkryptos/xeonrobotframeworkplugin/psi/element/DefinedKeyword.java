package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface DefinedKeyword {

   @Nullable
   String getLibraryName();

   String getKeywordName();

   boolean hasParameters();

   Collection<DefinedParameter> getParameters();

   boolean matches(String text);

   @NotNull
   PsiElement reference();

   String getArgumentsDisplayable();

   boolean isDeprecated();
}
