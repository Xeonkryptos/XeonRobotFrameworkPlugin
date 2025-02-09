package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public interface DefinedParameter extends LookupElementMarker {

   @Nullable
   String getLookup();

   default boolean hasDefaultValue() {
      return getDefaultValue() != null;
   }

   @Nullable
   String getDefaultValue();

   @Nullable
   PsiElement reference();
}
