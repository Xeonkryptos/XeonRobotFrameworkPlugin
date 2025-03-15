package com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.compilation;

import com.github.jnhyperion.hyperrobotframeworkplugin.RobotBundle;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.SimpleRobotInspection;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotStubTokenTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Import;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class RobotImportNotFound extends SimpleRobotInspection {
    @Nls
    @NotNull
    public String getDisplayName() {
        return RobotBundle.getMessage("INSP.NAME.import.undefined");
    }

    @Override
    public final boolean skip(PsiElement element) {
        if (element.getNode().getElementType() != RobotStubTokenTypes.ARGUMENT) {
            return true;
        } else {
            PsiElement parentElement = element.getParent();
            if (parentElement instanceof Import) {
                PsiElement[] children = parentElement.getChildren();
                if (children.length == 0 || children[0] != element) {
                    return true;
                } else {
                    PsiReference reference = element.getReference();
                    return reference != null && reference.resolve() != null;
                }
            } else {
                return true;
            }
        }
    }

    @Override
    public final String getMessage() {
        return RobotBundle.getMessage("INSP.import.undefined");
    }

    @NotNull
    @Override
    protected final String getGroupNameKey() {
        return "INSP.GROUP.compilation";
    }
}
