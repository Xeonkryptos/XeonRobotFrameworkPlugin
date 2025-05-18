package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface DefinedKeyword {

   String getKeywordName();

   boolean hasParameters();

   Collection<DefinedParameter> getParameters();

   boolean matches(String text);

   @NotNull
   PsiElement reference();

   String getArgumentsDisplayable();

   boolean isDeprecated();
}
