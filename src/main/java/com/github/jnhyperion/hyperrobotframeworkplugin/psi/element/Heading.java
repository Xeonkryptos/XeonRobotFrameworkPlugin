package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiFile;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
   Collection<DefinedKeyword> getTestCases();

   @NotNull
   Collection<PsiFile> getFilesFromInvokedKeywordsAndVariables();

   @NotNull
   Collection<DefinedVariable> getDefinedVariables();

   @NotNull
   Collection<VariableDefinition> getVariableDefinitions();

   void importsChanged();

   @NotNull
   Collection<KeywordInvokable> getInvokableKeywords(@Nullable KeywordDefinition var1);
}
