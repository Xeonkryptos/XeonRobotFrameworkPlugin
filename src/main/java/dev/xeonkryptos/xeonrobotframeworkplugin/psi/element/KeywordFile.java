package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface KeywordFile {

   @NotNull
   Collection<DefinedKeyword> getDefinedKeywords();

   @NotNull
   Collection<DefinedVariable> getDefinedVariables();

   @NotNull
   ImportType getImportType();

   @NotNull
   Collection<KeywordFile> getImportedFiles(boolean includeTransitive);

   @NotNull
   Collection<VirtualFile> getVirtualFiles(boolean includeTransitive);

   VirtualFile getVirtualFile();

   PsiFile getPsiFile();

   boolean isDifferentNamespace();

   boolean isValid();
}
