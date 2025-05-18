package dev.xeonkryptos.xeonrobotframeworkplugin.ide.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch.SearchParameters;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Heading;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Variable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinitionId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.VariableNameIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class RobotVariableReferenceSearch extends QueryExecutorBase<PsiReference, SearchParameters> {

    public RobotVariableReferenceSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull SearchParameters queryParameters, @NotNull Processor<? super PsiReference> consumer) {
        PsiElement element = queryParameters.getElementToSearch();
        Project project = queryParameters.getProject();

        GlobalSearchScope globalSearchScope = QueryExecutorUtil.convertToGlobalSearchScope(queryParameters.getEffectiveSearchScope(), project);
        if (element instanceof VariableDefinitionId variableDefinitionId) {
            String unwrappedName = variableDefinitionId.getUnwrappedName();
            if (unwrappedName.isBlank()) { // Cannot search with an empty variable name
                return;
            }

            KeywordDefinition keywordDefinition = PsiTreeUtil.getParentOfType(variableDefinitionId, KeywordDefinition.class);
            Heading heading = PsiTreeUtil.getParentOfType(variableDefinitionId, Heading.class);
            if (keywordDefinition != null || heading == null || !heading.isGlobalVariablesProvider()) {
                globalSearchScope = GlobalSearchScope.fileScope(variableDefinitionId.getContainingFile());
            }

            VariableDefinition variableDefinition = PsiTreeUtil.getParentOfType(variableDefinitionId, VariableDefinition.class);
            assert variableDefinition != null;
            searchForVariablesInIndex(variableDefinition, unwrappedName, project, globalSearchScope, consumer);
        }
    }

    private static void searchForVariablesInIndex(VariableDefinition variableDefinition,
                                                  String unwrappedVariableName,
                                                  Project project,
                                                  @Nullable GlobalSearchScope globalSearchScope,
                                                  @NotNull Processor<? super PsiReference> consumer) {
        VariableNameIndex variableNameIndex = VariableNameIndex.getInstance();
        Collection<Variable> variables = variableNameIndex.getVariables(unwrappedVariableName, project, globalSearchScope);
        for (Variable variable : variables) {
            if (variableDefinition.isInScope(variable)) {
                PsiReference reference = variable.getReference();
                if (!consumer.process(reference)) {
                    break;
                }
            }
        }
    }
}
