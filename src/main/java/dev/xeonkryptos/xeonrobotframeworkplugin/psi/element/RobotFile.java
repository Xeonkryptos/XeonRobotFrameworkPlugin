package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface RobotFile extends PsiFile {

    /**
     * @return all files that contain references to invoked keywords and used variables.
     */
    @NotNull Collection<PsiFile> getFilesFromInvokedKeywordsAndVariables();

    /**
     * Gets all the imported keyword files that are considered in scope for this file.  This
     * includes python libraries and robot resource files.
     *
     * @return a collection of keyword files that this files knows about.
     */
    @NotNull Collection<KeywordFile> collectImportedFiles(boolean includeTransitive, ImportType... importTypes);

    @NotNull Collection<VirtualFile> findImportedFilesWithLibraryName(@NotNull String libraryName);

    @NotNull Collection<DefinedVariable> getDefinedVariables();
}
