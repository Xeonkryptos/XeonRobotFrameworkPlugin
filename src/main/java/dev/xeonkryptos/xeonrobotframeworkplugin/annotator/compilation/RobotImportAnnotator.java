package dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.RobotAnnotator;
import dev.xeonkryptos.xeonrobotframeworkplugin.config.RobotHighlighter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotResourceImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesImportGlobalSetting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotImportAnnotator extends RobotAnnotator {

    @Override
    public void visitVariablesImportGlobalSetting(@NotNull RobotVariablesImportGlobalSetting o) {
        RobotImportArgument importArgument = o.getImportedFile();
        evaluateImport(importArgument);
    }

    @Override
    public void visitLibraryImportGlobalSetting(@NotNull RobotLibraryImportGlobalSetting o) {
        RobotImportArgument importArgument = o.getImportedFile();
        evaluateImport(importArgument);
    }

    @Override
    public void visitResourceImportGlobalSetting(@NotNull RobotResourceImportGlobalSetting o) {
        RobotImportArgument importArgument = o.getImportedFile();
        evaluateImport(importArgument);
    }

    private void evaluateImport(@Nullable RobotImportArgument importArgument) {
        if (importArgument != null) {
            PsiReference reference = importArgument.getReference();
            if (reference.resolve() == null) {
                getHolder().newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.import.not-found")).highlightType(ProblemHighlightType.ERROR).range(importArgument).create();
            } else {
                getHolder().newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).textAttributes(RobotHighlighter.IMPORT_ARGUMENT).range(importArgument).create();
            }
        }
    }
}
