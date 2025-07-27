package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.CachedValueProvider.Result;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.ParameterizedCachedValue;
import com.jetbrains.python.psi.PyClass;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.KeywordFileWithDependentsWrapper;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.PythonResolver;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotFileManager;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotPythonClass;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotImportFilesCollector;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotSectionVariablesCollector;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotUsedFilesCollector;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotUserKeywordsCollector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RobotFileImpl extends PsiFileBase implements KeywordFile, RobotFile {

    private static final String ROBOT_BUILT_IN = "robot.libraries.BuiltIn";

    private static final Key<ParameterizedCachedValue<Collection<KeywordFile>, Boolean>> IMPORTED_FILES_CACHE_KEY = Key.create("IMPORTED_FILES_CACHE");
    private static final Key<ParameterizedCachedValue<Collection<VirtualFile>, Boolean>> IMPORTED_VIRTUAL_FILES_CACHE_KEY = Key.create(
            "IMPORTED_VIRTUAL_FILES_CACHE");

    private final FileType fileType;

    private Collection<DefinedVariable> robotInitVariables;

    public RobotFileImpl(FileViewProvider fileViewProvider) {
        super(fileViewProvider, RobotLanguage.INSTANCE);

        fileType = fileViewProvider.getFileType();
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return fileType;
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        reset();
    }

    @NotNull
    @Override
    public final Collection<DefinedVariable> getDefinedVariables() {
        Collection<DefinedVariable> sectionVariables = getSectionVariables();
        Collection<DefinedVariable> globalVariables = RobotFileManager.getGlobalVariables(getProject());
        Collection<DefinedVariable> definedVariables = collectRobotInitVariables();
        boolean transitiveImports = RobotOptionsProvider.getInstance(getProject()).allowTransitiveImports();
        Stream<DefinedVariable> importedVariablesStream = getImportedFiles(transitiveImports).stream()
                                                                                             .flatMap(keywordFile -> keywordFile.getDefinedVariables()
                                                                                                                                .stream());
        return Stream.concat(sectionVariables.stream(),
                             Stream.concat(globalVariables.stream(), Stream.concat(definedVariables.stream(), importedVariablesStream)))
                     .collect(Collectors.toSet());
    }

    @NotNull
    public final Collection<DefinedVariable> collectRobotInitVariables() {
        Collection<DefinedVariable> results = this.robotInitVariables;
        if (results == null) {
            Set<VirtualFile> virtualFiles = new LinkedHashSet<>();
            results = new LinkedHashSet<>();
            for (KeywordFile importedFile : collectImportedFiles(true)) {
                if (importedFile instanceof RobotFile robotFile) {
                    VirtualFile virtualRobotFile = robotFile.getVirtualFile();
                    VirtualFile virtualRobotFileDir = virtualRobotFile.getParent();
                    VirtualFile initFile = virtualRobotFileDir.findChild("__init__.robot");
                    if (initFile != null) {
                        virtualFiles.add(initFile);
                    }
                }
            }

            Project project = getProject();
            PsiManager psiManager = PsiManager.getInstance(project);
            for (VirtualFile virtualFile : virtualFiles) {
                RobotFileImpl robotFile = (RobotFileImpl) psiManager.findFile(virtualFile);
                if (robotFile != null) {
                    Collection<DefinedVariable> definedVariables = robotFile.getSectionVariables();
                    results.addAll(definedVariables);
                }
            }
            this.robotInitVariables = results;
        }
        return results;
    }

    private Collection<DefinedVariable> getSectionVariables() {
        RobotSectionVariablesCollector visitor = new RobotSectionVariablesCollector();
        acceptChildren(visitor);
        return new LinkedHashSet<>(visitor.getVariables());
    }

    @NotNull
    @Override
    public final ImportType getImportType() {
        return ImportType.RESOURCE;
    }

    @NotNull
    @Override
    public final Collection<DefinedKeyword> getDefinedKeywords() {
        RobotUserKeywordsCollector userKeywordsCollector = new RobotUserKeywordsCollector();
        acceptChildren(userKeywordsCollector);
        return userKeywordsCollector.getKeywords();
    }

    @Override
    public final void reset() {
        robotInitVariables = null;
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
            return new Result<>(results, Stream.concat(results.stream(), Stream.of(this)).toArray());
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
    public Collection<KeywordFile> getImportedFiles(boolean includeTransitive) {
        return CachedValuesManager.getManager(getProject()).getParameterizedCachedValue(this, IMPORTED_FILES_CACHE_KEY, transitive -> {
            Set<KeywordFile> results = new LinkedHashSet<>();
            for (KeywordFile keywordFile : collectImportFiles()) {
                collectTransitiveKeywordFiles(results, keywordFile, transitive);
            }
            Object[] dependents = Stream.concat(results.stream().map(KeywordFile::getPsiFile), Stream.of(this)).toArray();
            return new Result<>(results, dependents);
        }, false, includeTransitive);
    }

    @NotNull
    @Override
    public Collection<KeywordFileWithDependentsWrapper> getImportedFilesWithDependents(boolean includeTransitive) {
        Set<KeywordFileWithParentWrapper> results = new LinkedHashSet<>();
        for (KeywordFile keywordFile : collectImportFiles()) {
            collectTransitiveKeywordFilesWithDependencyTracking(results, this, keywordFile, includeTransitive);
        }
        Map<KeywordFile, Set<KeywordFile>> childParentIndex = results.stream()
                                                                     .collect(Collectors.groupingBy(wrapper -> wrapper.keywordFile,
                                                                                                    Collectors.mapping(wrapper -> wrapper.parent,
                                                                                                                       Collectors.toCollection(LinkedHashSet::new))));
        return results.stream().map(KeywordFileWithParentWrapper::keywordFile).map(keywordFile -> {
            Set<KeywordFile> detectedParents = childParentIndex.get(keywordFile);
            Set<KeywordFile> parents = new LinkedHashSet<>();
            for (KeywordFile detectedParent : detectedParents) {
                collectCompleteImportParentTree(detectedParent, childParentIndex, parents);
            }
            return new KeywordFileWithDependentsWrapper(keywordFile, parents);
        }).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Collection<KeywordFile> collectImportFiles() {
        RobotImportFilesCollector importFilesCollector = new RobotImportFilesCollector();
        acceptChildren(importFilesCollector);
        return importFilesCollector.getFiles();
    }

    private void collectCompleteImportParentTree(KeywordFile parent, Map<KeywordFile, Set<KeywordFile>> childParentsIndex, Collection<KeywordFile> parents) {
        if (parents.add(parent)) {
            Set<KeywordFile> foundParents = childParentsIndex.get(parent);
            if (foundParents != null) {
                for (KeywordFile foundParent : foundParents) {
                    collectCompleteImportParentTree(foundParent, childParentsIndex, parents);
                }
            }
        }
    }

    private void addBuiltInImports(@NotNull Collection<KeywordFile> files) {
        PyClass builtIn = PythonResolver.findClass(ROBOT_BUILT_IN, getProject());
        if (builtIn != null) {
            files.add(new RobotPythonClass(ROBOT_BUILT_IN, builtIn, ImportType.LIBRARY, false));
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
            Object[] dependents = Stream.concat(files.stream(), Stream.of(this)).toArray();
            return new Result<>(files, dependents);
        }, false, includeTransitive);
    }

    @Override
    public final PsiFile getPsiFile() {
        return this;
    }

    @Override
    public final boolean isDifferentNamespace() {
        return false;
    }

    private void collectTransitiveKeywordFiles(Collection<KeywordFile> results, KeywordFile keywordFile, boolean includeTransitive) {
        if (results.add(keywordFile) && includeTransitive && keywordFile != this) {
            for (KeywordFile child : keywordFile.getImportedFiles(false)) {
                collectTransitiveKeywordFiles(results, child, true);
            }
        }
    }

    private void collectTransitiveKeywordFilesWithDependencyTracking(Collection<KeywordFileWithParentWrapper> results,
                                                                     KeywordFile parentFile,
                                                                     KeywordFile keywordFile,
                                                                     boolean includeTransitive) {
        if (results.add(new KeywordFileWithParentWrapper(keywordFile, parentFile)) && includeTransitive) {
            for (KeywordFile child : keywordFile.getImportedFiles(false)) {
                collectTransitiveKeywordFilesWithDependencyTracking(results, keywordFile, child, true);
            }
        }
    }

    private record KeywordFileWithParentWrapper(KeywordFile keywordFile, KeywordFile parent) {}

    @Override
    public String toString() {
        return "Robot: " + getName();
    }
}
