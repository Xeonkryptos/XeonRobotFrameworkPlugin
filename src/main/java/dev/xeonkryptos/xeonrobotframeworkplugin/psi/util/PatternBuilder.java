package dev.xeonkryptos.xeonrobotframeworkplugin.psi.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public class PatternBuilder {

   private static final Pattern PATTERN = Pattern.compile("(.*?)(\\$\\{.*?})(.*)");

   private static final String ANY = ".*?";
   private static final String DOT = ".";

   @NotNull
   public static String parseNamespace(@NotNull String namespace, @NotNull String keyword) {
      String result = "";
      if (!namespace.isEmpty()) {
         result = "(" + Pattern.quote(namespace + DOT) + ")?";
      }
      return result + parseFunction(keyword);
   }

   @NotNull
   private static String parseFunction(@NotNull String keyword) {
      Matcher matcher = PATTERN.matcher(keyword);
      String result = "";
      if (matcher.matches()) {
         keyword = matcher.group(1);
         String end = parseFunction(matcher.group(3));

         if (!keyword.isEmpty()) {
            result = Pattern.quote(keyword);
         }

         result = result + ANY;
         if (!end.isEmpty()) {
            result = result + end;
         }
      } else {
         result = !keyword.isEmpty() ? Pattern.quote(keyword) : keyword;
      }
      return result;
   }
}
