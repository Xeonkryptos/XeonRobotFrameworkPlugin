package dev.xeonkryptos.xeonrobotframeworkplugin.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons.Nodes;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.intellij.util.Processor;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.stubs.PyClassNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.completion.service.BuiltInImportCompletionService;
import dev.xeonkryptos.xeonrobotframeworkplugin.icons.RobotIcons;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportGlobalSettingExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotResourceImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.RobotFileManager;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotPyUtil;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

class ImportCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        RobotImportGlobalSettingExpression importElement = PsiTreeUtil.getParentOfType(parameters.getPosition(), RobotImportGlobalSettingExpression.class);
        if (importElement instanceof RobotLibraryImportGlobalSetting) {
            Project project = importElement.getProject();
            List<LookupElement> builtInLibraryCompletions = BuiltInImportCompletionService.getInstance(project).getBuiltInLibraryCompletions();
            result.addAllElements(builtInLibraryCompletions);

            GlobalSearchScope projectScope = GlobalSearchScope.projectScope(project);
            GlobalSearchScope projectExcludedScope = GlobalSearchScope.notScope(projectScope);

            Set<String> pyClassNames = new LinkedHashSet<>();
            PrefixMatcher prefixMatcher = result.getPrefixMatcher();
            Processor<String> processor = classNameKey -> {
                if (prefixMatcher.prefixMatches(classNameKey)) {
                    pyClassNames.add(classNameKey);
                }
                return true;
            };
            StubIndex.getInstance().processAllKeys(PyClassNameIndex.KEY, processor, projectScope);
            addPythonClasses(result, pyClassNames, project, projectScope, RobotLookupScope.PROJECT_SCOPE);

            pyClassNames.clear();
            StubIndex.getInstance().processAllKeys(PyClassNameIndex.KEY, processor, projectExcludedScope);
            addPythonClasses(result, pyClassNames, project, projectExcludedScope, RobotLookupScope.LIBRARY_SCOPE);
        } else if (importElement instanceof RobotResourceImportGlobalSetting) {
            addResourceFilePaths(result, parameters.getOriginalFile());
        }
    }

    private void addPythonClasses(@NotNull CompletionResultSet result, Set<String> pyClassNames, Project project, GlobalSearchScope searchScope, RobotLookupScope lookupScope) {
        Set<LookupElement> collectedElements = new LinkedHashSet<>();
        for (String pyClassName : pyClassNames) {
            Collection<PyClass> pyClasses = PyClassNameIndex.find(pyClassName, project, searchScope);
            pyClasses.removeIf(RobotPyUtil::isSystemLibrary);
            Collection<LookupElement> wrappedElements = wrapPythonClassCompletions(pyClasses, pyClassName, lookupScope);
            collectedElements.addAll(wrappedElements);
        }
        result.addAllElements(collectedElements);
    }

    private void addResourceFilePaths(CompletionResultSet resultSet, PsiFile file) {
        Project project = file.getProject();
        Module moduleForFile = ModuleUtilCore.findModuleForFile(file);
        VirtualFile contentRoot = RobotFileManager.findContentRootForFile(file);
        if (moduleForFile != null && contentRoot != null) {
            Collection<VirtualFile> resourceFiles = FilenameIndex.getAllFilesByExt(project, "resource", moduleForFile.getModuleContentScope());
            Collection<LookupElement> elements = resourceFiles.stream()
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
            resultSet.addAllElements(elements);
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
