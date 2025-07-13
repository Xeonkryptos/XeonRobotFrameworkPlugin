package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DefinedParameter extends LookupElementMarker {

   @Nullable
   String getLookup();

   default boolean hasDefaultValue() {
      return getDefaultValue() != null;
   }

   @Nullable
   String getDefaultValue();

   boolean isKeywordContainer();

   boolean matches(@NotNull String parameterName);

   @NotNull
   PsiElement reference();
}
