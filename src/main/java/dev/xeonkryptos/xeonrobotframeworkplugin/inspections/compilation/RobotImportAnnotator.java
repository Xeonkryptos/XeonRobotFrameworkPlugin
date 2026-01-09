package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.compilation;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.config.RobotHighlighter;
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.RobotAnnotator;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotResourceImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesImportGlobalSetting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotImportAnnotator extends RobotAnnotator {

    @Override
    public void visitVariablesImportGlobalSetting(@NotNull RobotVariablesImportGlobalSetting o) {
        RobotPositionalArgument positionalArgument = o.getImportedFile();
        evaluateImport(positionalArgument);
    }

    @Override
    public void visitLibraryImportGlobalSetting(@NotNull RobotLibraryImportGlobalSetting o) {
        RobotPositionalArgument positionalArgument = o.getImportedFile();
        evaluateImport(positionalArgument);
    }

    @Override
    public void visitResourceImportGlobalSetting(@NotNull RobotResourceImportGlobalSetting o) {
        RobotPositionalArgument positionalArgument = o.getImportedFile();
        evaluateImport(positionalArgument);
    }

    private void evaluateImport(@Nullable RobotPositionalArgument positionalArgument) {
        if (positionalArgument != null) {
            PsiReference reference = positionalArgument.getReference();
            if (reference.resolve() == null) {
                getHolder().newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.import.not-found"))
                           .highlightType(ProblemHighlightType.ERROR)
                           .range(positionalArgument)
                           .create();
            } else {
                getHolder().newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).textAttributes(RobotHighlighter.IMPORT_ARGUMENT).range(positionalArgument).create();
            }
        }
    }
}
