package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.python.PythonLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotVariableBodyIdManipulator extends AbstractElementManipulator<RobotVariableBodyId> {

    @Nullable
    @Override
    public RobotVariableBodyId handleContentChange(@NotNull RobotVariableBodyId variableBodyId, @NotNull TextRange textRange, String newText) throws
                                                                                                                                              IncorrectOperationException {
        String originalVariableName = variableBodyId.getText();
        String newVariableName = textRange.replace(originalVariableName, newText);
        String variableDefinition = """
                                    *** Variables ***
                                    ${%s}=  DUMMY
                                    """.formatted(newVariableName);

        PsiFileFactory factory = PsiFileFactory.getInstance(variableBodyId.getProject());

        LightVirtualFile virtualFile = new LightVirtualFile("dummy.robot", RobotFeatureFileType.getInstance(), variableDefinition);
        PsiFile psiFile = ((PsiFileFactoryImpl) factory).trySetupPsiForFile(virtualFile, PythonLanguage.getInstance(), false, true);
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
