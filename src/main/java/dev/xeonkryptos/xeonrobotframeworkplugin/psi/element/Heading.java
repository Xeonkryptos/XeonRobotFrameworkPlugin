package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface Heading extends RobotStatement {

   boolean isSettings();

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
