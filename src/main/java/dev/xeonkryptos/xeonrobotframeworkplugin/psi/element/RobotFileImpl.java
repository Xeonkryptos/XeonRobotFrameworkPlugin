package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider.Result;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.ParameterizedCachedValue;
import com.intellij.psi.util.PsiModificationTracker;
import com.jetbrains.python.psi.PyClass;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.PythonResolver;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotFileManager;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotPythonClass;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.VariableDefinitionNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.VariableScope;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotImportFilesCollector;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotSectionVariablesCollector;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotUsedFilesCollector;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RobotFileImpl extends PsiFileBase implements KeywordFile, RobotFile {

    private static final Key<ParameterizedCachedValue<Collection<VirtualFile>, Boolean>> IMPORTED_VIRTUAL_FILES_CACHE_KEY = Key.create(
            "IMPORTED_VIRTUAL_FILES_CACHE");
    private static final Key<CachedValue<Collection<DefinedVariable>>> TEST_SUITE_VARIABLES_CACHE_KEY = Key.create("TEST_SUITE_VARIABLES_CACHE");

    private final FileType fileType;

    private Collection<DefinedVariable> sectionVariables;

    public RobotFileImpl(FileViewProvider fileViewProvider) {
        super(fileViewProvider, RobotLanguage.INSTANCE);

        fileType = fileViewProvider.getFileType();
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        sectionVariables = null;
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return fileType;
    }

    @Override
    public Collection<DefinedVariable> findDefinedVariable(@NotNull String variableName) {
        if (getImportType() == ImportType.VARIABLES || getImportType() == ImportType.RESOURCE) {
            Project project = getProject();
            GlobalSearchScope currentFileSearchScope = GlobalSearchScope.fileScope(project, getVirtualFile());
            return VariableDefinitionNameIndex.getInstance()
                                              .getVariableDefinitions(variableName, project, currentFileSearchScope)
                                              .stream()
                                              .filter(variable -> variable.getScope() == VariableScope.Global || variable.getScope() == VariableScope.TestSuite)
                                              .map(DefinedVariable.class::cast)
                                              .toList();
        }
        return List.of();
    }

    @NotNull
    @Override
    public final Collection<DefinedVariable> getDefinedVariables() {
        return CachedValuesManager.getCachedValue(this,
                                                  TEST_SUITE_VARIABLES_CACHE_KEY,
                                                  () -> Result.createSingleDependency(getDefinedVariables(new LinkedHashSet<>()),
                                                                                      PsiModificationTracker.MODIFICATION_COUNT));
    }

    @NotNull
    @Override
    public Collection<DefinedVariable> getDefinedVariables(Collection<KeywordFile> visitedFiles) {
        Collection<DefinedVariable> sectionVariables = getSectionVariables();
        Collection<DefinedVariable> globalVariables = RobotFileManager.getGlobalVariables(getProject());
        Collection<KeywordFile> importedFiles = getImportedFiles(false);
        Set<DefinedVariable> importedVariables = new HashSet<>();
        for (KeywordFile keywordFile : importedFiles) {
            ProgressManager.checkCanceled();
            if (visitedFiles.add(keywordFile)) {
                Collection<DefinedVariable> subVariables = keywordFile.getDefinedVariables(visitedFiles);
                importedVariables.addAll(subVariables);
            }
        }

        Set<DefinedVariable> variables = new LinkedHashSet<>(sectionVariables.size() + globalVariables.size() + importedVariables.size());
        variables.addAll(sectionVariables);
        variables.addAll(globalVariables);
        variables.addAll(importedVariables);
        return variables;
    }

    private Collection<DefinedVariable> getSectionVariables() {
        if (sectionVariables == null) {
            RobotSectionVariablesCollector visitor = new RobotSectionVariablesCollector();
            acceptChildren(visitor);
            sectionVariables = visitor.getVariables();
        }
        return sectionVariables;
    }

    @NotNull
    @Override
    public final ImportType getImportType() {
        return ImportType.RESOURCE;
    }

    @NotNull
    @Override
    public final Collection<PsiFile> getFilesFromInvokedKeywordsAndVariables() {
        return CachedValuesManager.getCachedValue(this, () -> {
            RobotUsedFilesCollector robotUsedFilesCollector = new RobotUsedFilesCollector();
            acceptChildren(robotUsedFilesCollector);

            Collection<PsiFile> results = robotUsedFilesCollector.getReferences()
                                                                 .stream()
                                                                 .map(PsiReference::resolve)
                                                                 .filter(Objects::nonNull)
                                                                 .map(PsiElement::getContainingFile)
                                                                 .collect(Collectors.toCollection(ArrayList::new));
            Collection<KeywordFile> importedFiles = collectImportedFiles(false);
            Collection<VirtualFile> virtualFiles = getVirtualFiles(false);

            Set<PsiFile> psiFilesCopy = new HashSet<>(results);
            for (PsiFile psiFile : psiFilesCopy) {
                if (!virtualFiles.contains(psiFile.getVirtualFile())) {
                    for (KeywordFile importedFile : importedFiles) {
                        if (importedFile.getVirtualFiles(true).contains(psiFile.getVirtualFile())) {
                            results.add(importedFile.getPsiFile());
                            break;
                        }
                    }
                }
            }
            return new Result<>(results, PsiModificationTracker.MODIFICATION_COUNT);
        });
    }

    @NotNull
    @Override
    public final Collection<KeywordFile> collectImportedFiles(boolean includeTransitive) {
        Set<KeywordFile> results = new LinkedHashSet<>();
        addBuiltInImports(results);
        Collection<KeywordFile> importedFiles = getImportedFiles(includeTransitive);
        results.addAll(importedFiles);
        return results;
    }

    @NotNull
    @Override
    public Collection<VirtualFile> findImportedFilesWithLibraryName(@NotNull String libraryName) {
        return getImportedFiles(true).stream()
                                     .filter(importedFile -> libraryName.equals(importedFile.getLibraryName()))
                                     .map(KeywordFile::getVirtualFile)
                                     .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @NotNull
    @Override
    public Collection<KeywordFile> getImportedFiles(boolean includeTransitive) {
        if (!includeTransitive) {
            return collectImportFiles();
        }
        Set<KeywordFile> results = new LinkedHashSet<>();
        for (KeywordFile keywordFile : collectImportFiles()) {
            collectTransitiveKeywordFiles(keywordFile, results);
        }
        return results;
    }

    private Collection<KeywordFile> collectImportFiles() {
        // Don't implement caching here. The method is based on the result of some reference resolves. When we cache the results here, we might not retrieve
        // the latest reference resolves and thus missing some imports.
        RobotImportFilesCollector importFilesCollector = new RobotImportFilesCollector();
        acceptChildren(importFilesCollector);
        return importFilesCollector.getFiles();
    }

    private void collectTransitiveKeywordFiles(KeywordFile keywordFile, Collection<KeywordFile> results) {
        if (results.add(keywordFile) && keywordFile != this) {
            for (KeywordFile child : keywordFile.getImportedFiles(false)) {
                collectTransitiveKeywordFiles(child, results);
            }
        }
    }

    private void addBuiltInImports(@NotNull Collection<KeywordFile> files) {
        PyClass builtIn = PythonResolver.findClass(GlobalConstants.ROBOT_BUILT_IN, getProject());
        if (builtIn != null) {
            files.add(new RobotPythonClass(null, builtIn, ImportType.LIBRARY));
        }
    }

    @NotNull
    @Override
    public final Collection<VirtualFile> getVirtualFiles(boolean includeTransitive) {
        return CachedValuesManager.getManager(getProject()).getParameterizedCachedValue(this, IMPORTED_VIRTUAL_FILES_CACHE_KEY, transitive -> {
            Set<VirtualFile> files = new LinkedHashSet<>();
            for (KeywordFile keywordFile : collectImportedFiles(transitive)) {
                files.add(keywordFile.getVirtualFile());
            }
            return Result.createSingleDependency(files, PsiModificationTracker.MODIFICATION_COUNT);
        }, false, includeTransitive);
    }

    @Override
    public final PsiFile getPsiFile() {
        return this;
    }

    @Nullable
    @Override
    public String getLibraryName() {
        return null;
    }

    @Override
    public String toString() {
        return "Robot: " + getName();
    }
}
