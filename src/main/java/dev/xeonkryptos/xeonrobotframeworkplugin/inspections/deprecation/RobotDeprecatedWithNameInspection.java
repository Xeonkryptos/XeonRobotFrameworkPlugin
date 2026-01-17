package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.deprecation;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.RobotVersionBasedInspection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotVersionProvider.RobotVersion;
import org.jetbrains.annotations.NotNull;

public class RobotDeprecatedWithNameInspection extends RobotVersionBasedInspection implements DumbAware {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new RobotVisitor() {
            @Override
            public void visitLibraryImportGlobalSetting(@NotNull RobotLibraryImportGlobalSetting o) {
                PsiElement libraryNameElement = o.getLibraryNameElement();
                if (libraryNameElement != null && RobotNames.WITH_NAME_RESERVED_NAME.equals(libraryNameElement.getText())) {
                    holder.registerProblem(libraryNameElement, RobotBundle.message("INSP.library.import.with-name.deprecated"), ProblemHighlightType.LIKE_DEPRECATED);
                }
            }
        };
    }

    @Override
    protected RobotVersion getMinimumRobotVersion() {
        return new RobotVersion(6, 0, 0);
    }
}
