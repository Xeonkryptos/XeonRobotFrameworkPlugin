package dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.icons.RobotIcons;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Import;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotFileManager;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

class ImportCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        Import importElement = PsiTreeUtil.getParentOfType(parameters.getPosition(), Import.class);
        if (importElement != null && importElement.isLibrary()) {
            addBuiltinLibraryCompletions(result, parameters.getOriginalFile());
            if (importElement.getChildren().length > 1) {
                for (LookupElement lookupElement : CompletionProviderUtils.computeAdditionalSyntaxLookups(RobotTokenTypes.SYNTAX_MARKER)) {
                    if ("AS".equals(lookupElement.getLookupString())) {
                        result.addElement(lookupElement);
                    }
                }
            }
        }

        if (importElement != null && importElement.isResource()) {
            addResourceFilePaths(result, parameters.getOriginalFile());
        }
    }

    private void addBuiltinLibraryCompletions(CompletionResultSet resultSet, PsiFile file) {
        Map<String, ?> cachedFiles = RobotFileManager.getCachedRobotSystemFiles(file.getProject());
        for (String libraryName : cachedFiles.keySet()) {
            String[] lookupStrings = { libraryName, WordUtils.capitalize(libraryName), libraryName.toLowerCase() };
            LookupElementBuilder elementBuilder = LookupElementBuilder.create(libraryName)
                                                                      .withPresentableText(libraryName)
                                                                      .withLookupStrings(Arrays.asList(lookupStrings))
                                                                      .withCaseSensitivity(true)
                                                                      .withIcon(AllIcons.Nodes.Package)
                                                                      .withTypeText("robot.libraries.Builtin");
            resultSet.addElement(elementBuilder);
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
}
