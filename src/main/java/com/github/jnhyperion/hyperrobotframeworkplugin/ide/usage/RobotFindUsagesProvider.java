package com.github.jnhyperion.hyperrobotframeworkplugin.ide.usage;

import com.github.jnhyperion.hyperrobotframeworkplugin.RobotBundle;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Argument;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Import;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordDefinition;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotFile;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.VariableDefinition;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotFindUsagesProvider implements FindUsagesProvider {

   @Nullable
   @Override
   public WordsScanner getWordsScanner() {
      return new RobotWordScanner();
   }

   @Override
   public boolean canFindUsagesFor(@NotNull PsiElement var1) {
      return !(var1 instanceof Argument) || !(var1.getParent() instanceof Import) ? var1 instanceof PsiNamedElement : var1 == var1.getParent().getFirstChild();
   }

   @Nullable
   @Override
   public String getHelpId(@NotNull PsiElement var1) {
      return null;
   }

   @NotNull
   @Override
   public String getType(@NotNull PsiElement var1) {
       return RobotBundle.getMessage("usage.declaration");
   }

   @NotNull
   @Override
   public String getDescriptiveName(@NotNull PsiElement var1) {
      if (var1 instanceof KeywordDefinition) {
          return RobotBundle.getMessage("usage.descriptive.keyword");
      } else if (var1 instanceof VariableDefinition) {
          return RobotBundle.getMessage("usage.descriptive.variable");
      } else if (var1 instanceof RobotFile) {
          return RobotBundle.getMessage("usage.descriptive.import");
      } else if (var1 instanceof Argument) {
          return RobotBundle.getMessage("usage.descriptive.argument");
      } else {
         return "";
      }
   }

   @NotNull
   @Override
   public String getNodeText(@NotNull PsiElement var1, boolean var2) {
      return "";
   }
}
