package com.github.jnhyperion.hyperrobotframeworkplugin.ide.completion;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.icons.RobotIcons;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotTokenTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Import;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotFile;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref.RobotFileManager;
import com.intellij.codeInsight.TailTypes;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.TailTypeDecorator;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class ImportCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        Import importElement = PsiTreeUtil.getParentOfType(parameters.getPosition(), Import.class);
        if (importElement != null && importElement.isLibrary()) {
            addBuiltinLibraryCompletions(result, parameters.getOriginalFile());
            if (importElement.getChildren().length > 1) {
                for (LookupElement lookupElement : CompletionProviderUtils.addSyntaxLookup(RobotTokenTypes.SYNTAX_MARKER)) {
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
        if (!(file instanceof RobotFile)) {
            return;
        }
        Map<String, ?> cachedFiles = RobotFileManager.getCachedFiles(file.getProject());
        for (String libraryName : cachedFiles.keySet()) {
            String[] lookupStrings = { libraryName, WordUtils.capitalize(libraryName), libraryName.toLowerCase() };
            LookupElementBuilder elementBuilder = LookupElementBuilder.create(libraryName)
                                                                      .withPresentableText(libraryName)
                                                                      .withLookupStrings(Arrays.asList(lookupStrings))
                                                                      .withCaseSensitivity(true)
                                                                      .withIcon(AllIcons.Nodes.Package)
                                                                      .withTypeText("robot.libraries.Builtin");
            resultSet.addElement(TailTypeDecorator.withTail(elementBuilder, TailTypes.noneType()));
        }
    }

    private void addResourceFilePaths(CompletionResultSet resultSet, PsiFile file) {
        if (file instanceof RobotFile robotFile) {
            String basePath = robotFile.getProject().getBasePath();
            if (basePath != null) {
                for (String filePath : collectFilePaths(new File(basePath))) {
                    if (filePath.endsWith(".resource")) {
                        String relativePath = FileUtil.getRelativePath(new File(robotFile.getContainingDirectory().getVirtualFile().getPath()),
                                                                       new File(filePath));
                        if (relativePath != null) {
                            if (SystemUtils.IS_OS_WINDOWS) {
                                relativePath = relativePath.replace("\\", "/");
                            }

                            String[] lookupStrings = { relativePath, WordUtils.capitalize(relativePath), relativePath.toLowerCase() };
                            LookupElementBuilder elementBuilder = LookupElementBuilder.create(relativePath)
                                                                                      .withPresentableText(relativePath)
                                                                                      .withLookupStrings(Arrays.asList(lookupStrings))
                                                                                      .withCaseSensitivity(true)
                                                                                      .withIcon(RobotIcons.RESOURCE);
                            resultSet.addElement(TailTypeDecorator.withTail(elementBuilder, TailTypes.noneType()));
                        }
                    }
                }
            }
        }
    }

    private List<String> collectFilePaths(File directory) {
        List<String> filePaths = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    filePaths.addAll(collectFilePaths(file));
                } else {
                    filePaths.add(file.getPath());
                }
            }
        }
        return filePaths;
    }
}
