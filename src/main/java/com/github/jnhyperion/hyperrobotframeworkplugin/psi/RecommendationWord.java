package com.github.jnhyperion.hyperrobotframeworkplugin.psi;

import com.intellij.codeInsight.TailType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record RecommendationWord(String presentation, String lookup, TailType tailType) {

    public RecommendationWord(@NotNull String presentation, @NotNull String lookup, @Nullable TailType tailType) {
        this.presentation = presentation;
        this.lookup = lookup;
        this.tailType = tailType;
    }

    @Override
    @NotNull
    public String presentation() {
        return presentation;
    }

    @Override
    @NotNull
    public String lookup() {
        return lookup;
    }

    @Override
    @Nullable
    public TailType tailType() {
        return tailType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RecommendationWord that = (RecommendationWord) o;

        if (!this.lookup.equals(that.lookup)) {
            return false;
        }
        return this.presentation.equals(that.presentation);
    }

    @Override
    public int hashCode() {
        int result = presentation.hashCode();
        result = 31 * result + lookup.hashCode();
        return result;
    }
}
