package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotVariableReferenceSearcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotVariableReference extends PsiReferenceBase<RobotVariableId> {

    public RobotVariableReference(@NotNull RobotVariableId element) {
        super(element, false);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        RobotVariableId variableId = getElement();
        ResolveCache resolveCache = ResolveCache.getInstance(variableId.getProject());
        return resolveCache.resolveWithCaching(this, (robotVariableReference, incompleteCode) -> {
            String variableName = variableId.getName();
            if (variableName == null || variableName.isBlank()) { // e.g. ${}, thus empty representation of a variable. There can be no reference.
                return null;
            }

            PsiElement foundElement = null;
            RobotRoot rootElement = PsiTreeUtil.getParentOfType(variableId, RobotRoot.class);
            if (rootElement != null) {
                RobotVariableReferenceSearcher variableReferenceSearcher = new RobotVariableReferenceSearcher(variableId);
                rootElement.accept(variableReferenceSearcher);
                foundElement = variableReferenceSearcher.getFoundElement();
            }
            if (foundElement == null) {
                PsiFile containingFile = variableId.getContainingFile();
                foundElement = ResolverUtils.findVariableElement(variableName, containingFile);
            }
            return foundElement;
        }, true, false);
    }
}
