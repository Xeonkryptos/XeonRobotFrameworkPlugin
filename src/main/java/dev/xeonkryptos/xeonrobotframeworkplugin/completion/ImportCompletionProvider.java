package dev.xeonkryptos.xeonrobotframeworkplugin.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.icons.AllIcons.Nodes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.stubs.PyClassNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.icons.RobotIcons;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotKeywordProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportGlobalSettingExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotResourceImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotFileManager;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotPyUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

class ImportCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        Collection<LookupElement> lookupElements = new LinkedList<>();
        RobotImportGlobalSettingExpression importElement = PsiTreeUtil.getParentOfType(parameters.getPosition(), RobotImportGlobalSettingExpression.class);
        if (importElement instanceof RobotLibraryImportGlobalSetting) {
            addBuiltinLibraryCompletions(lookupElements, parameters.getOriginalFile());
            if (importElement.getChildren().length > 1) {
                for (LookupElement lookupElement : CompletionProviderUtils.computeAdditionalSyntaxLookups(RobotKeywordProvider.SYNTAX_MARKER)) {
                    if ("AS".equals(lookupElement.getLookupString())) {
                        lookupElements.add(lookupElement);
                    }
                }
            }

            Project project = importElement.getProject();
            Collection<String> classNameKeys = PyClassNameIndex.allKeys(project);

            GlobalSearchScope projectScope = GlobalSearchScope.projectScope(project);
            GlobalSearchScope projectExcludedScope = GlobalSearchScope.notScope(projectScope);
            for (String classNameKey : classNameKeys) {
                Collection<PyClass> projectPyClasses = PyClassNameIndex.find(classNameKey, project, projectScope);
                addPythonClassCompletions(projectPyClasses, classNameKey, RobotLookupScope.PROJECT_SCOPE, lookupElements);

                Collection<PyClass> pyClasses = PyClassNameIndex.find(classNameKey, project, projectExcludedScope);
                pyClasses.removeIf(RobotPyUtil::isSystemLibrary);
                addPythonClassCompletions(pyClasses, classNameKey, RobotLookupScope.LIBRARY_SCOPE, lookupElements);
            }
        } else if (importElement instanceof RobotResourceImportGlobalSetting) {
            addResourceFilePaths(result, parameters.getOriginalFile());
        }
        result.addAllElements(lookupElements);
    }

    private void addBuiltinLibraryCompletions(Collection<LookupElement> lookupElements, PsiFile file) {
        Map<String, ?> cachedFiles = RobotFileManager.getCachedRobotSystemFiles(file.getProject());
        for (String libraryName : cachedFiles.keySet()) {
            String[] lookupStrings = { libraryName, WordUtils.capitalize(libraryName), libraryName.toLowerCase() };
            LookupElementBuilder elementBuilder = LookupElementBuilder.create(libraryName)
                                                                      .withPresentableText(libraryName)
                                                                      .withLookupStrings(Arrays.asList(lookupStrings))
                                                                      .withCaseSensitivity(true)
                                                                      .withIcon(AllIcons.Nodes.Package)
                                                                      .withTypeText(GlobalConstants.ROBOT_BUILT_IN);
            lookupElements.add(elementBuilder);
        }
    }

    private void addResourceFilePaths(CompletionResultSet resultSet, PsiFile file) {
        Project project = file.getProject();
        VirtualFile sourceFile = file.getVirtualFile();
        Collection<VirtualFile> resourceFiles = FilenameIndex.getAllFilesByExt(project, "resource", GlobalSearchScope.projectScope(project));
        resourceFiles.stream().filter(resourceFile -> !resourceFile.equals(sourceFile)).map(virtualFile -> {
            VirtualFile commonAncestor = VfsUtil.getCommonAncestor(virtualFile, sourceFile);
            if (commonAncestor == null) {
                return null;
            }
            String relativePath = VfsUtil.getRelativePath(virtualFile, commonAncestor);
            assert relativePath != null;
            String[] lookupStrings = { relativePath, WordUtils.capitalize(relativePath), relativePath.toLowerCase() };
            return LookupElementBuilder.create(relativePath)
                                       .withIcon(RobotIcons.RESOURCE)
                                       .withLookupStrings(Arrays.asList(lookupStrings))
                                       .withCaseSensitivity(true)
                                       .withPresentableText(relativePath);
        }).filter(Objects::nonNull).forEach(resultSet::addElement);
    }

    private void addPythonClassCompletions(Collection<PyClass> pyClasses,
                                           String classNameKey,
                                           RobotLookupScope lookupScope,
                                           Collection<LookupElement> lookupElements) {
        if (classNameKey.startsWith("_")) { // Excluding as private indicated classes
            return;
        }
        pyClasses.stream()
                 .filter(this::isTopLevelClass)
                 .map(pyClass -> pyClass.getQualifiedName() != null ? pyClass.getQualifiedName() : classNameKey)
                 .distinct()
                 .map(className -> LookupElementBuilder.create(className).withIcon(Nodes.Class).withCaseSensitivity(true))
                 .forEach(element -> {
                     element.putUserData(CompletionKeys.ROBOT_LOOKUP_CONTEXT, RobotLookupContext.IMPORT);
                     element.putUserData(CompletionKeys.ROBOT_LOOKUP_SCOPE, lookupScope);
                     lookupElements.add(element);
                 });
    }

    private boolean isTopLevelClass(PyClass pyClass) {
        PyFile pyFile = (PyFile) pyClass.getContainingFile();
        return pyFile.getTopLevelClasses().contains(pyClass);
    }
}
