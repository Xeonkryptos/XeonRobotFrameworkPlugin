package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValueProvider.Result;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.ParameterizedCachedValue;
import com.intellij.psi.util.PsiModificationTracker;
import com.jetbrains.python.psi.PyClass;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.PythonResolver;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.RobotPythonClass;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.VariableDefinitionNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.VariableScope;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotImportFilesCollector;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotUsedFilesCollector;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.DisposableSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RobotFileImpl extends PsiFileBase implements KeywordFile, RobotFile {

    private static final Key<ParameterizedCachedValue<Collection<VirtualFile>, Boolean>> IMPORTED_VIRTUAL_FILES_CACHE_KEY = Key.create("IMPORTED_VIRTUAL_FILES_CACHE");

    private final FileType fileType;

    private Collection<DefinedVariable> sectionVariables;
    private Map<ImportType, Set<DisposableSupplier<KeywordFile>>> importedKeywordFiles;

    public RobotFileImpl(FileViewProvider fileViewProvider) {
        super(fileViewProvider, RobotLanguage.INSTANCE);

        fileType = fileViewProvider.getFileType();
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        sectionVariables = null;
        if (importedKeywordFiles != null) {
            importedKeywordFiles.values().stream().flatMap(Collection::stream).forEach(Disposable::dispose);
        }
        importedKeywordFiles = null;
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

    @Override
    public Collection<DefinedVariable> getLocallyDefinedVariables() {
        if (sectionVariables == null) {
            Project project = getProject();
            GlobalSearchScope fileScope = GlobalSearchScope.fileScope(getContainingFile().getOriginalFile());
            sectionVariables = VariableDefinitionNameIndex.getInstance()
                                                          .getVariableDefinitions(project, fileScope)
                                                          .stream()
                                                          .map(DefinedVariable.class::cast)
                                                          .collect(Collectors.toCollection(LinkedHashSet::new));
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

            Collection<PsiFile> results = robotUsedFilesCollector.getReferences().stream().flatMap(reference -> {
                if (reference instanceof PsiPolyVariantReference polyRef) {
                    ResolveResult[] resolveResults = polyRef.multiResolve(false);
                    return Arrays.stream(resolveResults).map(ResolveResult::getElement);
                }
                return Stream.of(reference.resolve());
            }).filter(Objects::nonNull).map(PsiElement::getContainingFile).collect(Collectors.toCollection(ArrayList::new));
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
    public final Collection<KeywordFile> collectImportedFiles(boolean includeTransitive, ImportType... importTypes) {
        Set<KeywordFile> results = new LinkedHashSet<>();
        PyClass builtInImportClass = PythonResolver.getBuiltInClass(this);
        if (builtInImportClass != null) {
            results.add(new RobotPythonClass(null, builtInImportClass, ImportType.LIBRARY));
        }
        Collection<KeywordFile> importedFiles = getImportedFiles(includeTransitive, importTypes);
        results.addAll(importedFiles);
        return results;
    }

    @NotNull
    @Override
    public Collection<VirtualFile> findImportedFilesWithLibraryName(@NotNull String libraryName) {
        return getImportedFiles(true, ImportType.LIBRARY).stream()
                                                         .filter(importedFile -> libraryName.equals(importedFile.getLibraryName()))
                                                         .map(KeywordFile::getVirtualFile)
                                                         .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @NotNull
    @Override
    public Collection<KeywordFile> getImportedFiles(boolean includeTransitive, ImportType... importTypes) {
        if (importTypes == null || importTypes.length == 0) {
            importTypes = ImportType.values();
        }
        if (!includeTransitive) {
            return collectImportFiles(importTypes);
        }
        Set<KeywordFile> results = new LinkedHashSet<>();
        for (KeywordFile keywordFile : collectImportFiles(importTypes)) {
            collectTransitiveKeywordFiles(keywordFile, results, importTypes);
        }
        return results;
    }

    private Collection<KeywordFile> collectImportFiles(ImportType[] importTypes) {
        // Don't implement caching here. The method is based on the result of some reference resolves. When we cache the results here, we might not retrieve
        // the latest reference resolves and thus missing some imports.
        if (importedKeywordFiles == null) {
            RobotImportFilesCollector importFilesCollector = new RobotImportFilesCollector();
            acceptChildren(importFilesCollector);
            importedKeywordFiles = importFilesCollector.getKeywordFileSuppliers();
        }
        Stream<DisposableSupplier<KeywordFile>> resourceImports = importedKeywordFiles.getOrDefault(ImportType.RESOURCE, Set.of()).stream();
        Stream<Supplier<KeywordFile>> imports = Arrays.stream(importTypes).flatMap(importType -> importedKeywordFiles.getOrDefault(importType, Set.of()).stream());
        return Stream.concat(resourceImports, imports).distinct().map(Supplier::get).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void collectTransitiveKeywordFiles(KeywordFile keywordFile, Collection<KeywordFile> results, ImportType[] importTypes) {
        if (results.add(keywordFile) && keywordFile != this) {
            for (KeywordFile child : keywordFile.getImportedFiles(false, importTypes)) {
                collectTransitiveKeywordFiles(child, results, importTypes);
            }
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
