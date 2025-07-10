package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.IStubFileElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.KeywordFileWithDependentsWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface RobotFile extends PsiFile {

    IFileElementType ROBOT_FILE = new IStubFileElementType<>("ROBOT_FILE", RobotLanguage.INSTANCE);

    /**
     * @return locally defined keywords.
     */
    @NotNull Collection<DefinedKeyword> getDefinedKeywords();

    void reset();

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
    @NotNull
    Collection<KeywordFile> collectImportedFiles(boolean includeTransitive);

    /**
     * Gets all the imported keyword files that are considered in scope for this file. The result consists of a pair of files. The first parameter is the
     * parent this file is imported from (e.g. the file that contains the import statement) and the second parameter is the imported file itself.
     *
     * @param includeTransitive if files that are imported by the imported files should be included as well
     * @return a collection of keyword files that this file knows about
     */
    @NotNull
    Collection<KeywordFileWithDependentsWrapper> getImportedFilesWithDependents(boolean includeTransitive);

    @NotNull
    Collection<DefinedVariable> getDefinedVariables();
}
