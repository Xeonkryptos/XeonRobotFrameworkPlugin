package dev.xeonkryptos.xeonrobotframeworkplugin.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch.SearchParameters;
import com.intellij.util.Processor;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.StringLiteralExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.KeywordCallNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotPyUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        switch (element) {
            case PyFunction pyFunction -> {
                String functionName = pyFunction.getName();
                if (functionName == null || searchForKeywordsInIndex(functionName, project, globalSearchScope, pyFunction, consumer)) {
                    return;
                }
                Optional<String> customKeywordNameOpt = RobotPyUtil.findCustomKeywordNameDecoratorExpression(pyFunction).map(StringLiteralExpression::getStringValue);
                customKeywordNameOpt.ifPresent(customKeywordName -> searchForKeywordsInIndex(customKeywordName, project, globalSearchScope, pyFunction, consumer));
            }
            case RobotUserKeywordStatement userKeywordStatement -> {
                String keywordName = userKeywordStatement.getName();
                searchForKeywordsInIndex(keywordName, project, globalSearchScope, userKeywordStatement, consumer);
            }
            // Special case for rename-refactor where the rename-refactor process got started within a Robot file on a keyword call referencing a python function.
            case RobotKeywordCall keywordCall -> {
                String keywordName = keywordCall.getName();
                PsiElement referencedElement = keywordCall.getKeywordCallName().getReference().resolve();
                if (referencedElement != null) {
                    searchForKeywordsInIndex(keywordName, project, globalSearchScope, referencedElement, consumer);
                }
            }
            default -> {
            }
        }
    }

    private static boolean searchForKeywordsInIndex(String keywordName,
                                                    Project project,
                                                    GlobalSearchScope globalSearchScope,
                                                    PsiElement referencedSourceElement,
                                                    @NotNull Processor<? super PsiReference> consumer) {
        KeywordCallNameIndex keywordCallNameIndex = KeywordCallNameIndex.getInstance();
        Collection<RobotKeywordCall> keywordCalls = keywordCallNameIndex.getKeywordCalls(keywordName, project, globalSearchScope);
        var groupedKeywordCalls = keywordCalls.stream()
                                              .collect(Collectors.groupingBy(PsiElement::getContainingFile,
                                                                             Collectors.groupingBy(robotKeywordCall -> KeywordUtil.normalizeKeywordName(robotKeywordCall.getName()))));
        boolean matchFound = false;
        for (Map<@NotNull String, List<RobotKeywordCall>> KeywordCallsGroupedByNameMap : groupedKeywordCalls.values()) {
            for (List<RobotKeywordCall> sameNameKeywordCalls : KeywordCallsGroupedByNameMap.values()) {
                RobotKeywordCall firstKeywordCall = sameNameKeywordCalls.getFirst();
                PsiReference reference = firstKeywordCall.getKeywordCallName().getReference();
                if (reference.isReferenceTo(referencedSourceElement)) {
                    if (!consumeAllKeywordCalls(referencedSourceElement, sameNameKeywordCalls, consumer)) {
                        return true; // Signal to stop consuming anything more, but still tell that a match was found
                    }
                    matchFound = true;
                }
            }
        }
        return matchFound;
    }

    private static boolean consumeAllKeywordCalls(PsiElement referencedElement, List<RobotKeywordCall> sameNameKeywordCalls, @NotNull Processor<? super PsiReference> consumer) {
        for (RobotKeywordCall keywordCall : sameNameKeywordCalls) {
            RobotKeywordCallName keywordCallName = keywordCall.getKeywordCallName();
            PsiReference optimizedRef = new PsiReferenceBase<>(keywordCallName) {
                @NotNull
                @Override
                public PsiElement resolve() {
                    return referencedElement;
                }
            };
            if (!consumer.process(optimizedRef)) {
                return false; // Signal to stop consuming anything more
            }
        }
        return true;
    }
}
