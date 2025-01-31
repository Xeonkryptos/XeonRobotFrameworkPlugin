package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiFile;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RobotFile extends PsiFile {

   /**
    * @return locally defined keywords.
    */
   @NotNull
   Collection<DefinedKeyword> getDefinedKeywords();

   void reset();

   /**
    * @return all files that contain references to invoked keywords and used variables.
    */
   @NotNull
   Collection<PsiFile> getFilesFromInvokedKeywordsAndVariables();

   /**
    * Gets all the imported keyword files that are considered in scope for this file.  This
    * includes python libraries and robot resource files.
    *
    * @return a collection of keyword files that this files knows about.
    */
   @NotNull
   Collection<KeywordFile> getImportedFiles(boolean includeTransitive);

   @NotNull
   Collection<DefinedVariable> getDefinedVariables();

   void importsChanged();

   @NotNull
   Collection<KeywordInvokable> getKeywordReferences(@Nullable KeywordDefinition var1);
}
