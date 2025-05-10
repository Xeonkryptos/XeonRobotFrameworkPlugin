package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

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
}
