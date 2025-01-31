package com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Argument;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Import;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordStatement;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import java.util.LinkedHashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotArgumentReference extends PsiReferenceBase<Argument> implements PsiPolyVariantReference {

   public RobotArgumentReference(@NotNull Argument argument) {
      super(argument, false);
   }

   @Nullable
   @Override
   public PsiElement resolve() {
       PsiElement result = null;

       try {
           Argument argument = this.getElement();
           PsiElement parent = argument.getParent();
           if (argument.getContainingFile().isValid()) {
               if (parent instanceof Import) {
                   Import importElement = (Import) parent;
                   PsiElement[] children = parent.getChildren();
                   if (children.length > 0 && children[0] == argument) {
                       if (importElement.isResource()) {
                           result = RobotFileManager.findElement(argument.getPresentableText(), argument.getProject(), argument);
                       } else if (importElement.isLibrary() || importElement.isVariables()) {
                           result = RobotFileManager.findElementInContext(argument.getPresentableText(), argument.getProject(), argument);
                       }

                       if (result == null) {
                           ResolveResult[] resolveResults = this.multiResolve(false);
                           if (resolveResults.length == 1) {
                               result = resolveResults[0].getElement();
                           }
                       }
                   }
               } else if (parent instanceof KeywordStatement) {
                   result = ResolverUtils.findKeywordElement(argument.getPresentableText(), argument.getContainingFile());
               }
           }
       } catch (Throwable ignored) {
       }

       return result;
   }

   @NotNull
   @Override
   public Object @NotNull [] getVariants() {
      return EMPTY_ARRAY;
   }

   @NotNull
   @Override
   public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
       LinkedHashSet<ResolveResult> results = new LinkedHashSet<>();

       try {
           Argument argument = this.getElement();
           Project project = argument.getProject();
           PsiElement parent = argument.getParent();
           String presentableText = argument.getPresentableText();

           if (parent instanceof Import) {
               Import importElement = (Import) parent;

               if (importElement.isResource()) {
                   for (PsiFile file : RobotFileManager.findPsiFiles(presentableText, project)) {
                       results.add(new PsiElementResolveResult(file));
                   }
               } else if ((importElement.isLibrary() || importElement.isVariables()) && presentableText.endsWith(".py")) {
                   for (PsiFile file : RobotFileManager.findPsiFiles(presentableText, project)) {
                       results.add(new PsiElementResolveResult(file));
                   }
               }
           }
       } catch (Throwable ignored) {
       }

       return results.toArray(new ResolveResult[0]);
   }
}
