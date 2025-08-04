package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameterId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotParameterIdManipulator extends AbstractElementManipulator<RobotParameterId> {

    @Nullable
    @Override
    public RobotParameterId handleContentChange(@NotNull RobotParameterId parameterId, @NotNull TextRange textRange, String newText) throws
                                                                                                                                     IncorrectOperationException {
        String original = parameterId.getText();
        String newContent = textRange.replace(original, newText);
        String fileContent = """
                             *** Test Case ***
                             Dummy
                                 Keyword  %s=Dummy
                             """.formatted(newContent);

        PsiFile psiFile = RobotElementGenerator.getInstance(parameterId.getProject()).createDummyPsiFile(fileContent);
        if (psiFile == null) {
            return null;
        }
        RobotParameterIdFinder parameterIdFinder = new RobotParameterIdFinder();
        psiFile.acceptChildren(parameterIdFinder);
        return (RobotParameterId) parameterId.replace(parameterIdFinder.parameterId);
    }

    private static final class RobotParameterIdFinder extends RobotVisitor {

        private RobotParameterId parameterId;

        @Override
        public void visitElement(@NotNull PsiElement element) {
            super.visitElement(element);
            element.acceptChildren(this);
        }

        @Override
        public void visitParameterId(@NotNull RobotParameterId o) {
            super.visitParameterId(o);
            parameterId = o;
        }
    }
}
