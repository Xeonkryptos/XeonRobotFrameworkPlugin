package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotVariableReferenceSearcher;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class RobotVariableReference extends PsiPolyVariantReferenceBase<RobotVariableId> {

    public RobotVariableReference(@NotNull RobotVariableId element) {
        super(element, false);
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        RobotVariableId variableId = getElement();
        ResolveCache resolveCache = ResolveCache.getInstance(variableId.getProject());
        return resolveCache.resolveWithCaching(this, (robotVariableReference, incompCode) -> {
            String variableName = variableId.getName();
            if (variableName == null || variableName.isBlank()) { // e.g. ${}, thus empty representation of a variable. There can be no reference.
                return ResolveResult.EMPTY_ARRAY;
            }

            Collection<PsiElement> foundElements = List.of();
            RobotRoot rootElement = PsiTreeUtil.getParentOfType(variableId, RobotRoot.class);
            if (rootElement != null) {
                RobotVariableReferenceSearcher variableReferenceSearcher = new RobotVariableReferenceSearcher(variableId);
                rootElement.accept(variableReferenceSearcher);
                foundElements = variableReferenceSearcher.getFoundElements();
            }
            if (foundElements.isEmpty()) {
                PsiFile containingFile = variableId.getContainingFile();
                PsiElement foundElement = ResolverUtils.findVariableElement(variableName, containingFile);
                if (foundElement != null) {
                    foundElements = List.of(foundElement);
                }
            }
            if (foundElements.isEmpty()) {
                return ResolveResult.EMPTY_ARRAY;
            }
            return foundElements.stream().map(PsiElementResolveResult::new).toArray(ResolveResult[]::new);
        }, true, false);
    }
}
