package dev.xeonkryptos.xeonrobotframeworkplugin.ide.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch.SearchParameters;
import com.intellij.util.Processor;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.StringLiteralExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.KeywordCallNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.KeywordUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotPyUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public class RobotKeywordReferenceSearch extends QueryExecutorBase<PsiReference, SearchParameters> {

    public RobotKeywordReferenceSearch() {
        super(true);
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void processQuery(@NotNull SearchParameters queryParameters, @NotNull Processor<? super PsiReference> consumer) {
        PsiElement element = queryParameters.getElementToSearch();
        if (!element.isValid()) {
            return;
        }
        Project project = queryParameters.getProject();

        GlobalSearchScope globalSearchScope = QueryExecutorUtil.convertToGlobalSearchScope(queryParameters.getEffectiveSearchScope(), project);
        if (element instanceof PyFunction pyFunction) {
            String functionName = pyFunction.getName();
            String possibleKeywordName = null;
            if (functionName != null) {
                possibleKeywordName = KeywordUtil.getInstance(project).functionToKeyword(functionName);
            }
            if (pyFunction.isValid() && searchForKeywordsInIndex(possibleKeywordName, project, globalSearchScope, consumer)) {
                return;
            }
            Optional<String> customKeywordNameOpt = RobotPyUtil.findCustomKeywordNameDecoratorExpression(pyFunction)
                                                               .filter(PsiElement::isValid)
                                                               .map(StringLiteralExpression::getStringValue);
            customKeywordNameOpt.ifPresent(customKeywordName -> searchForKeywordsInIndex(customKeywordName, project, globalSearchScope, consumer));
        } else if (element instanceof RobotUserKeywordStatement userKeywordStatement) {
            if (userKeywordStatement.isValid()) {
                String keywordName = userKeywordStatement.getName();
                searchForKeywordsInIndex(keywordName, project, globalSearchScope, consumer);
            }
        }
    }

    private static boolean searchForKeywordsInIndex(String keywordName,
                                                    Project project,
                                                    GlobalSearchScope globalSearchScope,
                                                    @NotNull Processor<? super PsiReference> consumer) {
        KeywordCallNameIndex keywordCallNameIndex = KeywordCallNameIndex.getInstance();
        Collection<RobotKeywordCall> keywordStatements = keywordCallNameIndex.getKeywordCalls(keywordName, project, globalSearchScope);
        for (RobotKeywordCall keywordStatement : keywordStatements) {
            if (keywordStatement.isValid()) {
                RobotKeywordCallName keywordCallName = keywordStatement.getKeywordCallName();
                if (!consumer.process(keywordCallName.getReference())) {
                    return true;
                }
            }
        }
        return false;
    }
}
