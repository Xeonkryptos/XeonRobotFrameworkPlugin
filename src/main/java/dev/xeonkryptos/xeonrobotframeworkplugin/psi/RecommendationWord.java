package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.codeInsight.TailType;

public record RecommendationWord(String presentation, String lookup, TailType tailType) {

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
