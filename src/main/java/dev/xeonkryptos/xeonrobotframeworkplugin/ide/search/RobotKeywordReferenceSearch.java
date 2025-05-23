package dev.xeonkryptos.xeonrobotframeworkplugin.ide.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch.SearchParameters;
import com.intellij.util.Processor;
import com.jetbrains.python.psi.PyDecoratorList;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import com.jetbrains.python.psi.StringLiteralExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordInvokable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.KeywordStatementNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.PatternUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class RobotKeywordReferenceSearch extends QueryExecutorBase<PsiReference, SearchParameters> {

    public RobotKeywordReferenceSearch() {
        super(true);
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void processQuery(@NotNull SearchParameters queryParameters, @NotNull Processor<? super PsiReference> consumer) {
        PsiElement element = queryParameters.getElementToSearch();
        Project project = queryParameters.getProject();

        GlobalSearchScope globalSearchScope = QueryExecutorUtil.convertToGlobalSearchScope(queryParameters.getEffectiveSearchScope(), project);
        if (element instanceof PyFunction pyFunction) {
            String possibleKeywordName = PatternUtil.functionToKeyword(pyFunction.getName());
            if (possibleKeywordName != null && searchForKeywordsInIndex(possibleKeywordName, project, globalSearchScope, consumer)) {
                return;
            }
            PyDecoratorList decoratorList = pyFunction.getDecoratorList();
            Optional<String> customKeywordNameOpt = Optional.ofNullable(decoratorList)
                                                            .map(decorators -> decorators.findDecorator("keyword"))
                                                            .map(decorator -> decorator.getArgument(0, "name", PyExpression.class))
                                                            .map(keywordNameExp -> (PyStringLiteralExpression) keywordNameExp)
                                                            .map(StringLiteralExpression::getStringValue);
            customKeywordNameOpt.ifPresent(customKeywordName -> searchForKeywordsInIndex(customKeywordName, project, globalSearchScope, consumer));
        } else if (element instanceof KeywordDefinition keywordDefinition) {
            String keywordName = keywordDefinition.getName();
            if (keywordName != null) {
                searchForKeywordsInIndex(keywordName, project, globalSearchScope, consumer);
            }
        }
    }

    private static boolean searchForKeywordsInIndex(String keywordName,
                                                    Project project,
                                                    GlobalSearchScope globalSearchScope,
                                                    @NotNull Processor<? super PsiReference> consumer) {
        KeywordStatementNameIndex keywordStatementNameIndex = KeywordStatementNameIndex.getInstance();
        for (KeywordStatement keywordStatement : keywordStatementNameIndex.getKeywordStatements(keywordName, project, globalSearchScope)) {
            if (keywordStatement.isValid()) {
                KeywordInvokable invokable = keywordStatement.getInvokable();
                if (!consumer.process(invokable.getReference())) {
                    return true;
                }
            }
        }
        return false;
    }
}
