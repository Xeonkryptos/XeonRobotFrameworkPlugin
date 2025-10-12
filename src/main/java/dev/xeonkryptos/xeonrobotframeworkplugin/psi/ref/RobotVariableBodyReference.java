package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.VariableDefinitionNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.VariableNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.VariableScope;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotVariableReferenceSearcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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
        assert variable != null;
        ResolveCache resolveCache = ResolveCache.getInstance(variableBodyId.getProject());
        return resolveCache.resolveWithCaching(this, (robotVariableReference, incompCode) -> {
            String variableName = variable.getVariableName();
            if (variableName == null) { // e.g. ${}, thus empty representation of a variable. There can be no reference.
                return ResolveResult.EMPTY_ARRAY;
            }

            Collection<PsiElement> foundElements = new LinkedHashSet<>();
            RobotRoot rootElement = PsiTreeUtil.getParentOfType(variableBodyId, RobotRoot.class);
            RobotVariableReferenceSearcher variableReferenceSearcher = new RobotVariableReferenceSearcher(variable, variableName);
            if (rootElement != null) {
                rootElement.acceptChildren(variableReferenceSearcher);
                Collection<PsiElement> resolvedElements = variableReferenceSearcher.getFoundElements();
                foundElements.addAll(resolvedElements);
            }
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
        Project project = variable.getProject();
        GlobalSearchScope globalSearchScope = GlobalSearchScope.projectScope(project);
        Collection<RobotVariableDefinition> matchingVariableDefinitions = VariableDefinitionNameIndex.getInstance()
                                                                                                     .getVariableDefinitions(variableName,
                                                                                                                             project,
                                                                                                                             globalSearchScope);
        Collection<RobotVariable> matchingVariables = VariableNameIndex.getInstance().getVariables(variableName, project, globalSearchScope);

        Map<RobotFile, Collection<PsiElement>> fileToElements = new HashMap<>();
        for (RobotVariableDefinition variableDefinition : matchingVariableDefinitions) {
            PsiFile containingFile = variableDefinition.getContainingFile();
            if (containingFile.getFileType() == RobotFeatureFileType.getInstance() || containingFile.getFileType() == RobotResourceFileType.getInstance()) {
                fileToElements.computeIfAbsent((RobotFile) containingFile, k -> new LinkedHashSet<>()).add(variableDefinition);
            }
        }
        for (RobotVariable matchingVariable : matchingVariables) {
            PsiFile containingFile = matchingVariable.getContainingFile();
            if (containingFile.getFileType() == RobotFeatureFileType.getInstance() || containingFile.getFileType() == RobotResourceFileType.getInstance()) {
                fileToElements.computeIfAbsent((RobotFile) containingFile, k -> new LinkedHashSet<>()).add(matchingVariable);
            }
        }

        Set<PsiElement> inScopeElements = new LinkedHashSet<>();
        RobotFile robotFile = (RobotFile) variable.getContainingFile();
        for (KeywordFile importedFile : robotFile.collectImportedFiles(true)) {
            PsiFile importedPsiFile = importedFile.getPsiFile();
            if ((importedPsiFile.getFileType() == RobotFeatureFileType.getInstance() || importedPsiFile.getFileType() == RobotResourceFileType.getInstance())
                && fileToElements.containsKey((RobotFile) importedPsiFile)) {
                Collection<PsiElement> psiElements = fileToElements.get(((RobotFile) importedPsiFile));
                for (PsiElement psiElement : psiElements) {
                    if (psiElement instanceof RobotVariableDefinition definition) {
                        if (EASY_SCOPES.contains(definition.getScope()) || definition.isInScope(variable)) {
                            inScopeElements.add(definition);
                        }
                    } else {
                        inScopeElements.add(psiElement);
                    }
                }
            }
        }
        return inScopeElements;
    }
}
