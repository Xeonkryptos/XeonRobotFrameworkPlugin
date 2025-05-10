package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

public class SimpleInspectionVisitor extends PsiElementVisitor {

   private final ProblemsHolder holder;
   private final SimpleInspection context;

   public SimpleInspectionVisitor(ProblemsHolder holder, SimpleInspection context) {
      this.holder = holder;
      this.context = context;
   }

   @Override
   public void visitElement(@NotNull PsiElement element) {
      if (!this.context.skip(element)) {
         this.holder.registerProblem(element, this.context.getMessage());
      }
   }
}
