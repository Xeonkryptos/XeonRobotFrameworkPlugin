package dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons.Nodes;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootModificationTracker;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.CachedValueProvider.Result;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.ParameterizedCachedValue;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.stubs.PyClassNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.completion.CompletionKeys;
import dev.xeonkryptos.xeonrobotframeworkplugin.completion.RobotLookupContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.completion.RobotLookupScope;
import dev.xeonkryptos.xeonrobotframeworkplugin.completion.service.BuiltInImportCompletionService;
import dev.xeonkryptos.xeonrobotframeworkplugin.icons.RobotIcons;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportGlobalSettingExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotResourceImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotPyUtil;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RobotImportArgumentReference extends PsiPolyVariantReferenceBase<RobotImportArgument> {

    private static final Key<ParameterizedCachedValue<Object[], RobotImportArgument>> IMPORT_LOOKUPS_KEY = Key.create("IMPORT_LOOKUPS_KEY");

    public RobotImportArgumentReference(@NotNull RobotImportArgument importArgument) {
        super(importArgument);
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        return ResolveCache.getInstance(getElement().getProject()).resolveWithCaching(this, (resolver, incompCode) -> multiResolve(getElement()), false, incompleteCode);
    }

    private static ResolveResult @NotNull [] multiResolve(RobotImportArgument importArgument) {
        Project project = importArgument.getProject();
        PsiElement parent = importArgument.getParent();
        String argumentValue = importArgument.getText();

        Set<ResolveResult> results = new LinkedHashSet<>();
        RobotVisitor visitor = new RobotVisitor() {
            @Override
            public void visitResourceImportGlobalSetting(@NotNull RobotResourceImportGlobalSetting o) {
                PsiFile containingFile = o.getContainingFile();
                PsiFile resourceFile = ResourceFileImportFinder.getInstance(project).findFileInFileSystem(argumentValue, containingFile, RobotResourceFileType.getInstance());
                if (resourceFile != null) {
                    results.add(new PsiElementResolveResult(resourceFile));
                }
            }

            @Override
            public void visitLibraryImportGlobalSetting(@NotNull RobotLibraryImportGlobalSetting o) {
                handleLibraryImports(o);
            }

            @Override
            public void visitVariablesImportGlobalSetting(@NotNull RobotVariablesImportGlobalSetting o) {
                handleLibraryImports(o);
            }

            private void handleLibraryImports(PsiElement element) {
                PsiFile containingFile = element.getContainingFile();
                PsiFile resourceFile = ResourceFileImportFinder.getInstance(project).findFileInFileSystem(argumentValue, containingFile);
                if (resourceFile != null) {
                    results.add(new PsiElementResolveResult(resourceFile));
                } else {
                    // File not directly found in file system. Try to find it in module search path (e.g. for classes or modules)
                    PsiElement result = PythonResolver.resolveElement(argumentValue, project);
                    if (result != null) {
                        results.add(new PsiElementResolveResult(result));
                    }
                }
            }
        };
        parent.accept(visitor);
        return results.toArray(ResolveResult.EMPTY_ARRAY);
    }

    @Override
    public Object @NotNull [] getVariants() {
        RobotImportArgument element = getElement();
        return CachedValuesManager.getManager(element.getProject()).getParameterizedCachedValue(element, IMPORT_LOOKUPS_KEY, elem -> {
            RobotImportGlobalSettingExpression importElement = PsiTreeUtil.getParentOfType(elem, RobotImportGlobalSettingExpression.class, RobotVariablesImportGlobalSetting.class);
            List<LookupElement> lookupElements = new ArrayList<>();
            RobotVisitor visitor = new RobotVisitor() {
                @Override
                public void visitLibraryImportGlobalSetting(@NotNull RobotLibraryImportGlobalSetting o) {
                    Project project = o.getProject();
                    addLibraryImportPaths(project, lookupElements);
                }

                @Override
                public void visitResourceImportGlobalSetting(@NotNull RobotResourceImportGlobalSetting o) {
                    addFilePaths(Set.of("resource"), elem.getContainingFile(), lookupElements);
                }

                @Override
                public void visitVariablesImportGlobalSetting(@NotNull RobotVariablesImportGlobalSetting o) {
                    Project project = o.getProject();
                    addFilePaths(Set.of("yaml", "yml", "json"), elem.getContainingFile(), lookupElements);
                    addLibraryImportPaths(project, lookupElements);
                }
            };
            if (importElement != null) {
                importElement.accept(visitor);
            }
            return Result.createSingleDependency(lookupElements.toArray(), ProjectRootModificationTracker.getInstance(elem.getProject()));
        }, false, element);
    }

    private void addPythonClasses(@NotNull List<LookupElement> result, Set<String> pyClassNames, Project project, GlobalSearchScope searchScope, RobotLookupScope lookupScope) {
        Set<LookupElement> collectedElements = new LinkedHashSet<>();
        for (String pyClassName : pyClassNames) {
            Collection<PyClass> pyClasses = PyClassNameIndex.find(pyClassName, project, searchScope);
            pyClasses.removeIf(RobotPyUtil::isSystemLibrary);
            Collection<LookupElement> wrappedElements = wrapPythonClassCompletions(pyClasses, pyClassName, lookupScope);
            collectedElements.addAll(wrappedElements);
        }
        result.addAll(collectedElements);
    }

    private void addLibraryImportPaths(Project project, List<LookupElement> result) {
        List<LookupElement> builtInLibraryCompletions = BuiltInImportCompletionService.getInstance(project).getBuiltInLibraryCompletions();
        result.addAll(builtInLibraryCompletions);

        GlobalSearchScope projectScope = GlobalSearchScope.projectScope(project);
        GlobalSearchScope projectExcludedScope = GlobalSearchScope.notScope(projectScope);

        Set<String> pyClassNames = new LinkedHashSet<>();
        Processor<String> processor = classNameKey -> {
            pyClassNames.add(classNameKey);
            return true;
        };
        StubIndex.getInstance().processAllKeys(PyClassNameIndex.KEY, processor, projectScope);
        addPythonClasses(result, pyClassNames, project, projectScope, RobotLookupScope.PROJECT_SCOPE);

        pyClassNames.clear();
        StubIndex.getInstance().processAllKeys(PyClassNameIndex.KEY, processor, projectExcludedScope);
        addPythonClasses(result, pyClassNames, project, projectExcludedScope, RobotLookupScope.LIBRARY_SCOPE);
    }

    private void addFilePaths(Set<String> fileExtensions, PsiFile file, List<LookupElement> resultSet) {
        Project project = file.getProject();
        Module moduleForFile = ModuleUtilCore.findModuleForPsiElement(file);
        VirtualFile contentRoot = RobotFileManager.findContentRootForFile(file);
        if (moduleForFile != null && contentRoot != null) {
            @SuppressWarnings("UnstableApiUsage")
            Set<LookupElementBuilder> elements = fileExtensions.stream()
                                                               .parallel()
                                                               .flatMap(fileExtension -> ReadAction.computeCancellable(() -> FilenameIndex.getAllFilesByExt(project,
                                                                                                                                                            fileExtension,
                                                                                                                                                            moduleForFile.getModuleContentScope()))
                                                                                                   .stream())
                                                               .filter(resourceFile -> VfsUtil.isAncestor(contentRoot, resourceFile, true))
                                                               .map(virtualFile -> VfsUtil.getRelativePath(virtualFile, contentRoot))
                                                               .filter(Objects::nonNull)
                                                               .map(relativePath -> {
                                                                   String[] lookupStrings = { relativePath, WordUtils.capitalize(relativePath), relativePath.toLowerCase() };
                                                                   return LookupElementBuilder.create(relativePath)
                                                                                              .withIcon(RobotIcons.RESOURCE)
                                                                                              .withLookupStrings(Arrays.asList(lookupStrings))
                                                                                              .withCaseSensitivity(true)
                                                                                              .withPresentableText(relativePath);
                                                               })
                                                               .collect(Collectors.toCollection(LinkedHashSet::new));
            resultSet.addAll(elements);
        }
    }

    private Collection<LookupElement> wrapPythonClassCompletions(Collection<PyClass> pyClasses, String classNameKey, RobotLookupScope lookupScope) {
        if (classNameKey.startsWith("_")) { // Excluding as protected/private indicated classes
            return Collections.emptyList();
        }
        return pyClasses.stream().filter(this::isTopLevelClass).map(pyClass -> pyClass.getQualifiedName() != null ? pyClass.getQualifiedName() : classNameKey).distinct().map(className -> {
            LookupElementBuilder element = LookupElementBuilder.create(className).withIcon(Nodes.Class).withCaseSensitivity(true);
            element.putUserData(CompletionKeys.ROBOT_LOOKUP_CONTEXT, RobotLookupContext.IMPORT);
            element.putUserData(CompletionKeys.ROBOT_LOOKUP_SCOPE, lookupScope);
            return element;
        }).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private boolean isTopLevelClass(PyClass pyClass) {
        PyFile pyFile = (PyFile) pyClass.getContainingFile();
        return pyFile.getTopLevelClasses().contains(pyClass);
    }
}
