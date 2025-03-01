package com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.compilation;

import com.github.jnhyperion.hyperrobotframeworkplugin.RobotBundle;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.SimpleRobotInspection;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotTokenTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordInvokable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class RobotKeywordNotFound extends SimpleRobotInspection {
    @Nls
    @NotNull
    public String getDisplayName() {
        return RobotBundle.getMessage("INSP.NAME.keyword.undefined");
    }

    @Override
    public final boolean skip(PsiElement element) {
        if (element instanceof KeywordInvokable) {
            PsiReference reference = element.getReference();
            boolean isResolved = reference != null && reference.resolve() != null;

            if (!isResolved && element.getNode().getElementType() == RobotTokenTypes.SYNTAX_MARKER) {
                return true;
            }

            return isResolved;
        }
        return true;
    }

    @Override
    public final String getMessage() {
        return RobotBundle.getMessage("INSP.keyword.undefined");
    }

    @NotNull
    @Override
    protected final String getGroupNameKey() {
        return "INSP.GROUP.compilation";
    }
}
