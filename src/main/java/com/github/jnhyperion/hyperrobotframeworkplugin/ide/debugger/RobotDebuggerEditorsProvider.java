package com.github.jnhyperion.hyperrobotframeworkplugin.ide.debugger;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotFeatureFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProviderBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is a weird thing in the Intellij framework... apparently it's needed
 * to provide a view of the document when editing breakpoints?!?
 * <p>
 * Why it doesn't use the default language configurations is a bit of a
 * mystery...
 */
public class RobotDebuggerEditorsProvider extends XDebuggerEditorsProviderBase {

    @NotNull
    @Override
    public FileType getFileType() {
        return RobotFeatureFileType.getInstance();
    }

    @Override
    protected PsiFile createExpressionCodeFragment(@NotNull Project project, @NotNull String text, @Nullable PsiElement context, boolean isPhysical) {
        final String fileName = context != null ? context.getContainingFile().getName() : "dummy.robot" ;
        return PsiFileFactory.getInstance(project).createFileFromText(fileName, RobotFeatureFileType.getInstance(), text);
    }
}
