package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotVariableReferenceSearcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotVariableReference extends PsiReferenceBase<RobotVariable> {

    public RobotVariableReference(@NotNull RobotVariable element) {
        super(element, false);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        RobotVariable variable = getElement();
        ResolveCache resolveCache = ResolveCache.getInstance(variable.getProject());
        return resolveCache.resolveWithCaching(this, (robotVariableReference, incompleteCode) -> {
            String variableName = variable.getName();
            if (variableName == null || variableName.isBlank()) { // e.g. ${}, thus empty representation of a variable. There can be no reference.
                return null;
            }

            RobotVariableReferenceSearcher variableReferenceSearcher = new RobotVariableReferenceSearcher(variable);
            variable.accept(variableReferenceSearcher);
            PsiElement foundElement = variableReferenceSearcher.getFoundElement();
            if (foundElement == null) {
                PsiFile containingFile = variable.getContainingFile();
                foundElement = ResolverUtils.findVariableElement(variableName, containingFile);
            }
            return foundElement;
        }, true, false);
    }
}
