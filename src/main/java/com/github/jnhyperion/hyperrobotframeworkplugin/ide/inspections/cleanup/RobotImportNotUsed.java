package com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.cleanup;

import com.github.jnhyperion.hyperrobotframeworkplugin.RobotBundle;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.SimpleRobotInspection;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.PositionalArgument;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Import;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class RobotImportNotUsed extends SimpleRobotInspection {

   @Nls
   @NotNull
   @Override
   public String getDisplayName() {
      return RobotBundle.getMessage("INSP.NAME.import.unused");
   }

   @Override
   public final boolean skip(PsiElement element) {
       try {
           PsiElement parentElement;
           PsiReference elementReference;
           PsiElement resolvedElement;

           if (element instanceof PositionalArgument && (parentElement = element.getParent()) instanceof Import && ((Import) parentElement).isResource()
               && (elementReference = element.getReference()) != null && (resolvedElement = elementReference.resolve()) instanceof RobotFile) {

               Collection<Import> importElements = PsiTreeUtil.findChildrenOfType(element.getContainingFile(), Import.class);
               List<String> importIdentifiers = new ArrayList<>();

               for (Import importElement : importElements) {
                   importIdentifiers.add(importElement.getImportText());
               }

               int firstOccurrenceIndex = importIdentifiers.indexOf(((Import) parentElement).getImportText());
               int lastOccurrenceIndex = importIdentifiers.lastIndexOf(((Import) parentElement).getImportText());

               if (firstOccurrenceIndex != lastOccurrenceIndex && new ArrayList<>(importElements).indexOf(parentElement) != firstOccurrenceIndex) {
                   return false;
               }

               return ((RobotFile) element.getContainingFile()).getFilesFromInvokedKeywordsAndVariables().contains(resolvedElement.getContainingFile());
           }
       } catch (Throwable ignored) {
       }
       return true;
   }

   @Override
   public final String getMessage() {
      return RobotBundle.getMessage("INSP.import.unused");
   }

   @NotNull
   @Override
   protected final String getGroupNameKey() {
      return "INSP.GROUP.cleanup";
   }
}
