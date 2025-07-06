package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.codeInsight.TailType;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotKeywordTable {

   private final Map<IElementType, Set<String>> syntaxByType = new HashMap<>();
   private final Map<IElementType, Set<RecommendationWord>> recommendationsByType = new HashMap<>();

   public final void addSyntax(IElementType type, String keyword) {
      Set<String> keywords = this.syntaxByType.get(type);
      if (keywords == null) {
         if (type == RobotKeywordProvider.GHERKIN) {
            // this allows syntax for WHEN vs When vs when
            keywords = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
         } else {
            keywords = new HashSet<>();
         }
         this.syntaxByType.put(type, keywords);
      }
      keywords.add(keyword);
   }

   public final void addRecommendation(@NotNull IElementType type, @NotNull String keyword, @NotNull String lookup, @Nullable TailType tailType) {
       Set<RecommendationWord> keywords = this.recommendationsByType.computeIfAbsent(type, k -> new HashSet<>());
       keywords.add(new RecommendationWord(keyword, lookup, tailType));
   }

   @NotNull
   public final Set<String> getSyntaxOfType(IElementType type) {
      Set<String> results = this.syntaxByType.get(type);
      return results == null ? Collections.emptySet() : results;
   }

   @NotNull
   public final Set<RecommendationWord> getRecommendationsForType(IElementType type) {
      Set<RecommendationWord> results = this.recommendationsByType.get(type);
      return results == null ? Collections.emptySet() : results;
   }
}
