package com.github.jnhyperion.hyperrobotframeworkplugin.ide.search;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordInvokable;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordStatement;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.stub.index.KeywordStatementNameIndex;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.PatternUtil;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch.SearchParameters;
import com.intellij.util.Processor;
import com.jetbrains.python.psi.PyDecoratorList;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import com.jetbrains.python.psi.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class RobotPythonReferenceSearch extends QueryExecutorBase<PsiReference, SearchParameters> {

    public RobotPythonReferenceSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull SearchParameters queryParameters, @NotNull Processor<? super PsiReference> consumer) {
        PsiElement element = queryParameters.getElementToSearch();
        Project project = queryParameters.getProject();

        if (element instanceof PyFunction pyFunction) {
            KeywordStatementNameIndex keywordStatementNameIndex = KeywordStatementNameIndex.getInstance();
            String possibleKeywordName = PatternUtil.functionToKeyword(pyFunction.getName());
            GlobalSearchScope globalSearchScope = convertToGlobalSearchScope(queryParameters.getEffectiveSearchScope(), project);
            if (possibleKeywordName != null) {
                for (KeywordStatement keywordStatement : keywordStatementNameIndex.getKeywordStatement(possibleKeywordName, project, globalSearchScope)) {
                    KeywordInvokable invokable = keywordStatement.getInvokable();
                    if (invokable != null && !consumer.process(invokable.getReference())) {
                        return;
                    }
                }
            }
            PyDecoratorList decoratorList = pyFunction.getDecoratorList();
            Optional<String> customKeywordNameOpt = Optional.ofNullable(decoratorList)
                                                            .map(decorators -> decorators.findDecorator("keyword"))
                                                            .map(decorator -> decorator.getArgument(0, "name", PyExpression.class))
                                                            .map(keywordNameExp -> (PyStringLiteralExpression) keywordNameExp)
                                                            .map(StringLiteralExpression::getStringValue);
            if (customKeywordNameOpt.isPresent()) {
                String customKeywordName = customKeywordNameOpt.get();
                for (KeywordStatement keywordStatement : keywordStatementNameIndex.getKeywordStatement(customKeywordName, project, globalSearchScope)) {
                    KeywordInvokable invokable = keywordStatement.getInvokable();
                    if (invokable != null && !consumer.process(invokable.getReference())) {
                        return;
                    }
                }
            }
        }
    }

    public static GlobalSearchScope convertToGlobalSearchScope(@NotNull SearchScope scope, @NotNull Project project) {
        if (scope instanceof GlobalSearchScope) {
            return (GlobalSearchScope) scope;
        }
        else if (scope instanceof LocalSearchScope localScope) {
            // If the local scope is empty, return empty global scope
            if (localScope == LocalSearchScope.EMPTY) {
                return GlobalSearchScope.EMPTY_SCOPE;
            }

            // Convert LocalSearchScope to GlobalSearchScope
            // This creates a global scope that contains all files in the local scope
            return GlobalSearchScope.filesScope(project,
                                                Arrays.stream(localScope.getScope())
                                                      .map(PsiElement::getContainingFile)
                                                      .filter(Objects::nonNull)
                                                      .map(PsiFile::getVirtualFile)
                                                      .filter(Objects::nonNull)
                                                      .collect(Collectors.toList()));
        }

        // Fallback to project scope if the type is unknown
        return GlobalSearchScope.projectScope(project);
    }
}
