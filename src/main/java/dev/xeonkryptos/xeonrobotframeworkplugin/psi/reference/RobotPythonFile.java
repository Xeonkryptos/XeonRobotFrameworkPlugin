package dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.jetbrains.python.psi.PyFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RobotPythonFile implements KeywordFile {

    private final String library;
    private final PyFile pythonFile;
    private final ImportType importType;

    public RobotPythonFile(@Nullable String library, @NotNull PyFile pythonFile, @NotNull ImportType importType) {
        this.library = library;
        this.pythonFile = pythonFile;
        this.importType = importType;
    }

    @Override
    public Collection<DefinedVariable> findDefinedVariable(@NotNull String variableName) {
        if (importType == ImportType.VARIABLES) {
            return RobotKeywordFileResolver.findVariable(pythonFile, variableName);
        }
        return List.of();
    }

    @NotNull
    @Override
    public Collection<DefinedVariable> getDefinedVariables(Collection<KeywordFile> visitedFiles) {
        return getDefinedVariables();
    }

    @NotNull
    @Override
    public final Collection<DefinedVariable> getDefinedVariables() {
        if (importType == ImportType.VARIABLES) {
            return RobotKeywordFileResolver.resolveVariables(pythonFile);
        }
        return List.of();
    }

    @NotNull
    @Override
    public final ImportType getImportType() {
        return importType;
    }

    @NotNull
    @Override
    public final Collection<KeywordFile> getImportedFiles(boolean includeTransitive, ImportType... importTypes) {
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public final Collection<VirtualFile> getVirtualFiles(boolean includeTransitive) {
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RobotPythonFile that = (RobotPythonFile) obj;
        return Objects.equals(library, that.library) && pythonFile.equals(that.pythonFile);
    }

    @Override
    public int hashCode() {
        if (library != null) {
            return 31 * library.hashCode() + pythonFile.hashCode();
        }
        return pythonFile.hashCode();
    }

    @Override
    public String toString() {
        return pythonFile.toString();
    }

    @Override
    public VirtualFile getVirtualFile() {
        VirtualFile virtualFile = pythonFile.getVirtualFile();
        return virtualFile != null ? virtualFile : pythonFile.getOriginalFile().getVirtualFile();
    }

    @Override
    public final PsiFile getPsiFile() {
        return pythonFile;
    }

    @Nullable
    @Override
    public String getLibraryName() {
        return library;
    }
}
