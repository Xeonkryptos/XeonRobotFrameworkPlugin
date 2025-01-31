package com.github.jnhyperion.hyperrobotframeworkplugin.psi;

import com.intellij.codeInsight.TailType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RecommendationWord {

   private final String presentation;
   private final String lookup;
   private final TailType tailType;

   public RecommendationWord(@NotNull String presentation, @NotNull String lookup, @Nullable TailType var3) {
      this.presentation = presentation;
      this.lookup = lookup;
      this.tailType = var3;
   }

   @NotNull
   public final String getPresentation() {
      return this.presentation;
   }

   @NotNull
   public final String getLookup() {
      return this.lookup;
   }

   @Nullable
   public final TailType getTailType() {
      return this.tailType;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      RecommendationWord that = (RecommendationWord) o;

      if (!this.lookup.equals(that.lookup)) return false;
      return this.presentation.equals(that.presentation);
   }

   @Override
   public int hashCode() {
      int result = this.presentation.hashCode();
      result = 31 * result + this.lookup.hashCode();
      return result;
   }
}
