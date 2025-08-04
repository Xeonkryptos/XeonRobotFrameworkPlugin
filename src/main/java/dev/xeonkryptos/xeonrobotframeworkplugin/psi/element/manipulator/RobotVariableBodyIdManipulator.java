package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotVariableBodyIdManipulator extends AbstractElementManipulator<RobotVariableBodyId> {

    @Nullable
    @Override
    public RobotVariableBodyId handleContentChange(@NotNull RobotVariableBodyId variableBodyId, @NotNull TextRange textRange, String newText) throws
                                                                                                                                              IncorrectOperationException {
        String original = variableBodyId.getText();
        String newContent = textRange.replace(original, newText);
        String fileContent = """
                             *** Variables ***
                             ${%s}=  DUMMY
                             """.formatted(newContent);

        PsiFile psiFile = RobotElementGenerator.getInstance(variableBodyId.getProject()).createDummyPsiFile(fileContent);
        if (psiFile == null) {
            return null;
        }
        RobotVariableBodyFinder variableBodyFinder = new RobotVariableBodyFinder();
        psiFile.acceptChildren(variableBodyFinder);
        return (RobotVariableBodyId) variableBodyId.replace(variableBodyFinder.variableBodyId);
    }

    private static final class RobotVariableBodyFinder extends RobotVisitor {

        private RobotVariableBodyId variableBodyId;

        @Override
        public void visitElement(@NotNull PsiElement element) {
            super.visitElement(element);
            element.acceptChildren(this);
        }

        @Override
        public void visitVariableBodyId(@NotNull RobotVariableBodyId o) {
            super.visitVariableBodyId(o);
            variableBodyId = o;
        }
    }
}
