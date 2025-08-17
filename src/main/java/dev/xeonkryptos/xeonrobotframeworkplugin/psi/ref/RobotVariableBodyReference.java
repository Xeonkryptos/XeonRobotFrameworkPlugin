package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotVariableReferenceSearcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class RobotVariableBodyReference extends PsiPolyVariantReferenceBase<RobotVariableBodyId> {

    public RobotVariableBodyReference(@NotNull RobotVariableBodyId element) {
        super(element, false);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length >= 1 ? resolveResults[0].getElement() : null;
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        RobotVariableBodyId variableBodyId = getElement();
        ResolveCache resolveCache = ResolveCache.getInstance(variableBodyId.getProject());
        return resolveCache.resolveWithCaching(this, (robotVariableReference, incompCode) -> {
            String variableName = variableBodyId.getText();
            if (variableName.isBlank()) { // e.g. ${}, thus empty representation of a variable. There can be no reference.
                return ResolveResult.EMPTY_ARRAY;
            }

            Collection<PsiElement> foundElements = List.of();
            RobotRoot rootElement = PsiTreeUtil.getParentOfType(variableBodyId, RobotRoot.class);
            if (rootElement != null) {
                RobotVariableReferenceSearcher variableReferenceSearcher = new RobotVariableReferenceSearcher(variableBodyId);
                rootElement.accept(variableReferenceSearcher);
                foundElements = variableReferenceSearcher.getFoundElements();
            }
            if (foundElements.isEmpty()) {
                PsiFile containingFile = variableBodyId.getContainingFile();
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
