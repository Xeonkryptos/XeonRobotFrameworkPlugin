package dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference;

import com.intellij.openapi.project.Project;
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
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableContent;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.VariableDefinitionNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.VariableScope;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class RobotVariableContentReference extends PsiPolyVariantReferenceBase<RobotVariableContent> {

    private static final Set<VariableScope> EASY_SCOPES = Set.of(VariableScope.Global, VariableScope.TestSuite);

    public RobotVariableContentReference(@NotNull RobotVariableContent element) {
        super(element, false);
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        RobotVariable variable = PsiTreeUtil.getParentOfType(getElement(), RobotVariable.class);
        if (variable == null) {
            return ResolveResult.EMPTY_ARRAY;
        }

        Project project = getElement().getProject();
        ResolveCache resolveCache = ResolveCache.getInstance(project);
        return resolveCache.resolveWithCaching(this, (robotVariableReference, incompCode) -> {
            String variableName = variable.getVariableName();
            if (variableName == null) {
                return ResolveResult.EMPTY_ARRAY;
            }

            Collection<PsiElement> foundElements = findVariableDefinitions(variable, variableName);
            if (foundElements.isEmpty()) {
                return ResolveResult.EMPTY_ARRAY;
            }
            return foundElements.stream().map(PsiElementResolveResult::new).toArray(ResolveResult[]::new);
        }, true, false);
    }

    /**
     * Finds all definitions of the given variable name visible from the given variable usage. Used for plain variables and for the base variable of an extended variable access.
     */
    @NotNull
    public static Collection<PsiElement> findVariableDefinitions(@NotNull RobotVariable variable, @NotNull String variableName) {
        Project project = variable.getProject();
        Collection<PsiElement> foundElements = new LinkedHashSet<>();
        VariableDefinitionNameIndex.getInstance()
                                   .getVariableDefinitions(variableName, project, GlobalSearchScope.fileScope(variable.getContainingFile().getOriginalFile()))
                                   .stream()
                                   .filter(variableDefinition -> variableDefinition.isInScope(variable))
                                   .forEach(foundElements::add);
        foundElements.addAll(findVariableElementsOutsideOfCurrentFile(variable, variableName));
        return foundElements;
    }

    @NotNull
    private static Collection<PsiElement> findVariableElementsOutsideOfCurrentFile(RobotVariable variable, String variableName) {
        RobotFile robotFile = (RobotFile) variable.getContainingFile();
        return robotFile.collectImportedFiles(true, ImportType.VARIABLES, ImportType.RESOURCE)
                        .stream()
                        .flatMap(file -> file.findDefinedVariable(variableName).stream())
                        .filter(foundVar -> foundVar.matches(variableName) && (EASY_SCOPES.contains(foundVar.getScope()) || foundVar.isInScope(variable)))
                        .map(DefinedVariable::reference)
                        .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
