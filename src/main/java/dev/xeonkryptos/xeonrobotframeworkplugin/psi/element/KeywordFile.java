package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface KeywordFile {

    Collection<DefinedVariable> findDefinedVariable(@NotNull String variableName);

    @NotNull Collection<DefinedVariable> getDefinedVariables();

    @NotNull Collection<DefinedVariable> getDefinedVariables(Collection<KeywordFile> visitedFiles);

    @NotNull ImportType getImportType();

    @NotNull Collection<KeywordFile> getImportedFiles(boolean includeTransitive);

    @NotNull Collection<VirtualFile> getVirtualFiles(boolean includeTransitive);

    VirtualFile getVirtualFile();

    PsiFile getPsiFile();

    @Nullable String getLibraryName();
}
