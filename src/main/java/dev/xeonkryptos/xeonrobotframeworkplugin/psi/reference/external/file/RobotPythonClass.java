package dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.external.file;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.types.TypeEvalContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class RobotPythonClass implements KeywordFile {

    private final String library;
    private final PyClass pythonClass;
    private final ImportType importType;

    public RobotPythonClass(@Nullable String library, @NotNull PyClass pythonClass, @NotNull ImportType importType) {
        this.library = library;
        this.pythonClass = pythonClass;
        this.importType = importType;
    }

    @Override
    public Collection<DefinedVariable> findDefinedVariable(@NotNull String variableName) {
        if (importType == ImportType.VARIABLES) {
            return RobotKeywordFileResolver.findVariable(pythonClass, variableName);
        }
        return List.of();
    }

    @Override
    public Collection<DefinedVariable> getLocallyDefinedVariables() {
        if (importType == ImportType.VARIABLES) {
            return RobotKeywordFileResolver.resolveVariables(pythonClass);
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
    public Collection<VirtualFile> getVirtualFiles() {
        PsiFile psiFile = getPsiFile();
        VirtualFile virtualFile = psiFile.getVirtualFile();
        Set<VirtualFile> virtualFiles = new LinkedHashSet<>();
        if (virtualFile != null) {
            virtualFiles.add(virtualFile);
        } else {
            virtualFiles.add(psiFile.getOriginalFile().getVirtualFile());
        }
        TypeEvalContext typeEvalContext = TypeEvalContext.codeAnalysis(psiFile.getProject(), psiFile);
        pythonClass.getAncestorClasses(typeEvalContext)
                   .stream()
                   .map(pyClass -> pyClass.getContainingFile().getOriginalFile().getVirtualFile())
                   .filter(Objects::nonNull)
                   .distinct()
                   .forEach(virtualFiles::add);
        return virtualFiles;
    }

    @Override
    public final PsiFile getPsiFile() {
        return pythonClass.getContainingFile();
    }

    @Nullable
    @Override
    public String getLibraryName() {
        return library;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RobotPythonClass that = (RobotPythonClass) o;
        return Objects.equals(this.library, that.library) && this.pythonClass.equals(that.pythonClass);
    }

    @Override
    public int hashCode() {
        if (library != null) {
            int result = library.hashCode();
            return 31 * result + pythonClass.hashCode();
        }
        return pythonClass.hashCode();
    }

    @Override
    public String toString() {
        return pythonClass.toString();
    }
}
