package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface Heading extends RobotStatement {

   boolean isSettings();

   default boolean isGlobalVariablesProvider() {
      return containsVariables() && getContainingFile().getVirtualFile().getFileType() == RobotResourceFileType.getInstance();
   }

   boolean containsVariables();

   boolean containsTestCases();

   boolean containsTasks();

   boolean containsKeywordDefinitions();

   @NotNull
   Collection<RobotStatement> getMetadataStatements();

   @NotNull
   Collection<KeywordFile> collectImportFiles();

   @NotNull
   Collection<DefinedKeyword> collectDefinedKeywords();

   @NotNull
   Collection<KeywordDefinition> getTestCases();

   @NotNull
   Collection<PsiFile> getFilesFromInvokedKeywordsAndVariables();

   @NotNull
   Collection<DefinedVariable> getDefinedVariables();

   @NotNull
   Collection<VariableDefinition> getVariableDefinitions();

   void importsChanged();
}
