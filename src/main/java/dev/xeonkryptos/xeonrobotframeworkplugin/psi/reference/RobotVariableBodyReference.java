package dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.VariableDefinitionNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.VariableScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class RobotVariableBodyReference extends PsiPolyVariantReferenceBase<RobotVariableBodyId> {

    private static final Set<VariableScope> EASY_SCOPES = Set.of(VariableScope.Global, VariableScope.TestSuite);

    public RobotVariableBodyReference(@NotNull RobotVariableBodyId element) {
        super(element, false);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return Arrays.stream(resolveResults)
                     .filter(result -> result.isValidResult() && result.getElement() instanceof RobotVariableDefinition)
                     .findFirst()
                     .map(ResolveResult::getElement)
                     .orElse(null);
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        RobotVariableBodyId variableBodyId = getElement();
        RobotVariable variable = PsiTreeUtil.getParentOfType(variableBodyId, RobotVariable.class);
        if (variable == null) {
            return ResolveResult.EMPTY_ARRAY;
        }

        ResolveCache resolveCache = ResolveCache.getInstance(variableBodyId.getProject());
        return resolveCache.resolveWithCaching(this, (robotVariableReference, incompCode) -> {
            String variableName = variable.getVariableName();
            if (variableName == null) { // e.g. ${}, thus empty representation of a variable. There can be no reference.
                return ResolveResult.EMPTY_ARRAY;
            }

            Collection<PsiElement> foundElements = new LinkedHashSet<>();
            VariableDefinitionNameIndex.getInstance()
                                       .getVariableDefinitions(variableName,
                                                               variableBodyId.getProject(),
                                                               GlobalSearchScope.fileScope(variableBodyId.getContainingFile()))
                                       .stream()
                                       .filter(variableDefinition -> variableDefinition.isInScope(variable))
                                       .forEach(foundElements::add);
            Collection<PsiElement> otherElements = findVariableElementsOutsideOfCurrentFile(variable, variableName);
            foundElements.addAll(otherElements);
            if (foundElements.isEmpty()) {
                return ResolveResult.EMPTY_ARRAY;
            }
            return foundElements.stream().map(PsiElementResolveResult::new).toArray(ResolveResult[]::new);
        }, true, false);
    }

    @NotNull
    private Collection<PsiElement> findVariableElementsOutsideOfCurrentFile(RobotVariable variable, String variableName) {
        RobotFile robotFile = (RobotFile) variable.getContainingFile();
        return robotFile.collectImportedFiles(true, ImportType.VARIABLES, ImportType.RESOURCE)
                        .stream()
                        .flatMap(file -> file.findDefinedVariable(variableName).stream())
                        .filter(foundVar -> foundVar.matches(variableName) && (EASY_SCOPES.contains(foundVar.getScope()) || foundVar.isInScope(variable)))
                        .map(DefinedVariable::reference)
                        .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
