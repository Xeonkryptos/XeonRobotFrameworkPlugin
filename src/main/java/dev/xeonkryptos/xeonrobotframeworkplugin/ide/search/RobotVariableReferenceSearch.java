package dev.xeonkryptos.xeonrobotframeworkplugin.ide.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch.SearchParameters;
import com.intellij.util.Processor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotPsiImplUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
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
        if (!element.isValid()) {
            return;
        }
        Project project = queryParameters.getProject();

        GlobalSearchScope globalSearchScope = QueryExecutorUtil.convertToGlobalSearchScope(queryParameters.getEffectiveSearchScope(), project);
        if (element instanceof RobotVariableDefinition variableDefinition) {
            String variableName = variableDefinition.getName();
            if (variableName == null) {
                return;
            }
            searchForVariablesInIndex(variableDefinition, variableName, project, globalSearchScope, consumer);
        }
    }

    private static void searchForVariablesInIndex(RobotVariableDefinition variableDefinition,
                                                  String variableName,
                                                  Project project,
                                                  @Nullable GlobalSearchScope globalSearchScope,
                                                  @NotNull Processor<? super PsiReference> consumer) {
        VariableNameIndex variableNameIndex = VariableNameIndex.getInstance();
        Collection<RobotVariable> variables = ReadAction.compute(() -> variableNameIndex.getVariables(variableName, project, globalSearchScope));
        for (RobotVariable variable : variables) {
            RobotVariableBodyId variableBodyId = RobotPsiImplUtil.getVariableBodyId(variable);
            if (variableBodyId != null && !(variable.getParent() instanceof RobotVariableDefinition)) {
                PsiReference reference = variableBodyId.getReference();
                if (reference.isReferenceTo(variableDefinition)) {
                    if (!consumer.process(reference)) {
                        break;
                    }
                }
            }
        }
    }
}
