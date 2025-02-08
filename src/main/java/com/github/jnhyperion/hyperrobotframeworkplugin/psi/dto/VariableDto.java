package com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedVariable;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.PatternUtil;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.ReservedVariableScope;
import com.intellij.psi.PsiElement;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VariableDto implements DefinedVariable {
   private final PsiElement reference;
   private final String name;
   private final ReservedVariableScope scope;
   private Pattern pattern;

   public VariableDto(@NotNull PsiElement reference, @NotNull String name, @Nullable ReservedVariableScope scope) {
      this.reference = reference;
      this.name = name.trim();
      this.scope = scope;
   }

   @Override
   public final boolean matches(@Nullable String text) {
      if (text == null) {
         return false;
      } else {
         try {
            Pattern pattern = this.pattern;
            if (this.pattern == null) {
               pattern = Pattern.compile(PatternUtil.getVariablePattern(this.name), Pattern.CASE_INSENSITIVE);
               this.pattern = pattern;
            }

            return pattern.matcher(text).matches();
         } catch (Throwable var3) {
            return false;
         }
      }
   }

   @Override
   public final boolean isInScope(@Nullable PsiElement position) {
      return this.scope == null || position == null || this.scope.isInScope(position);
   }

   @Nullable
   @Override
   public final PsiElement reference() {
      return this.reference;
   }

   @Nullable
   @Override
   public final String getLookup() {
      return this.scope == null ? this.reference.getText() : this.name;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         VariableDto variable = (VariableDto) o;
         return this.name.equals(variable.name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.name.hashCode();
   }

   @Override
   public String toString() {
      return name;
   }
}
