package com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedKeyword;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.PatternBuilder;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.PatternUtil;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyParameter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public class KeywordDto implements DefinedKeyword {

   private final PsiElement reference;
   private final String name;
   private final boolean args;
   private final Pattern namePattern;
   private final List<PyParameter> parameters;

   public KeywordDto(@NotNull PsiElement reference, @NotNull String namespace, @NotNull String name) {
      this(reference, namespace, name, false, null);
   }

   public KeywordDto(@NotNull PsiElement reference, @NotNull String namespace, @NotNull String name, boolean args, List<PyParameter> parameters) {
      super();
      this.reference = reference;
      this.name = PatternUtil.functionToKeyword(name).trim();
      this.namePattern = Pattern.compile(PatternBuilder.parseNamespace(namespace, PatternUtil.keywordToFunction(this.name)), Pattern.CASE_INSENSITIVE);
      this.args = args;
      this.parameters = parameters;
   }

   public final List<PyParameter> getParameters() {
      return this.parameters == null ? new ArrayList<>() : this.parameters;
   }

   @Override
   public final String getKeywordName() {
      return this.name;
   }

   @Override
   public final boolean hasArguments() {
      return this.args;
   }

   @Override
   public final boolean matches(String text) {
      return text != null && this.namePattern.matcher(PatternUtil.keywordToFunction(text).trim()).matches();
   }

   @Override
   public final PsiElement reference() {
      return this.reference;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         return this.name.equals(((KeywordDto) o).name);
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
      return this.name;
   }
}
