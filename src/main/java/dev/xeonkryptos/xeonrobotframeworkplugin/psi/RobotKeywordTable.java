package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.codeInsight.TailType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RobotKeywordTable {

   private final Map<IElementType, Set<RecommendationWord>> recommendationsByType = new HashMap<>();

   public final void addRecommendation(@NotNull IElementType type, @NotNull String keyword, @NotNull String lookup, @Nullable TailType tailType) {
       Set<RecommendationWord> keywords = this.recommendationsByType.computeIfAbsent(type, k -> new HashSet<>());
       keywords.add(new RecommendationWord(keyword, lookup, tailType));
   }

   @NotNull
   public final Set<RecommendationWord> getRecommendationsForType(IElementType type) {
      Set<RecommendationWord> results = this.recommendationsByType.get(type);
      return results == null ? Collections.emptySet() : results;
   }

   public final void clearRecommendations() {
      this.recommendationsByType.clear();
   }
}
