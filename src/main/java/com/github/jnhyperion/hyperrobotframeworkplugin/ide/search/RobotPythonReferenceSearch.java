package com.github.jnhyperion.hyperrobotframeworkplugin.ide.search;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotFeatureFileType;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordDefinition;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordInvokable;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotFile;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotStatement;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.psi.search.searches.ReferencesSearch.SearchParameters;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RobotPythonReferenceSearch extends QueryExecutorBase<PsiReference, SearchParameters> {

    public RobotPythonReferenceSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull SearchParameters queryParameters, @NotNull Processor<? super PsiReference> consumer) {
        PsiElement elementToSearch = queryParameters.getElementToSearch();
        if (elementToSearch instanceof KeywordDefinition keywordDefinition) {
            Project project = keywordDefinition.getProject();
            SearchScope searchScope = queryParameters.getEffectiveSearchScope();
            if (searchScope instanceof GlobalSearchScope globalSearchScope) {
                Collection<VirtualFile> virtualFiles = FileTypeIndex.getFiles(RobotFeatureFileType.getInstance(), globalSearchScope);
                processKeywordReferences(keywordDefinition, consumer, project, virtualFiles);
            }
        } else if (elementToSearch instanceof RobotStatement statement) {
            SearchScope searchScope = queryParameters.getEffectiveSearchScope();
            searchWord(statement, queryParameters, searchScope);
        }
    }

    private static void searchWord(@NotNull RobotStatement statement, @NotNull SearchParameters queryParameters, @NotNull SearchScope scope) {
        String text = statement.getPresentableText();
        queryParameters.getOptimizer().searchWord(text, scope, UsageSearchContext.ANY, false, statement);
    }

    private static void processKeywordReferences(@NotNull KeywordDefinition keywordDefinition,
                                                 @NotNull Processor<? super PsiReference> processor,
                                                 @NotNull Project project,
                                                 @NotNull Collection<VirtualFile> virtualFiles) {
        boolean continueProcessing = true;
        for (VirtualFile virtualFile : virtualFiles) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            if (psiFile instanceof RobotFile) {
                for (KeywordInvokable keywordInvokable : ((RobotFile) psiFile).getKeywordReferences(keywordDefinition)) {
                    PsiReference reference = keywordInvokable.getReference();
                    if (reference != null && reference.isReferenceTo(keywordDefinition)) {
                        continueProcessing = processor.process(reference);
                    }

                    if (!continueProcessing) {
                        break;
                    }
                }
            }

            if (!continueProcessing) {
                break;
            }
        }
    }
}
