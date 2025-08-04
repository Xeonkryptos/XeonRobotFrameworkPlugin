package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotPositionalArgumentManipulator extends AbstractElementManipulator<RobotPositionalArgument> {

    @Nullable
    @Override
    public RobotPositionalArgument handleContentChange(@NotNull RobotPositionalArgument positionalArgument, @NotNull TextRange textRange, String newText) throws
                                                                                                                                                          IncorrectOperationException {
        String original = positionalArgument.getText();
        String newContent = textRange.replace(original, newText);
        String fileContent = """
                             *** Test Case ***
                             Dummy
                                 Keyword  %s
                             """.formatted(newContent);

        PsiFile psiFile = RobotElementGenerator.getInstance(positionalArgument.getProject()).createDummyPsiFile(fileContent);
        if (psiFile == null) {
            return null;
        }
        RobotPositionalArgumentFinder positionalArgumentFinder = new RobotPositionalArgumentFinder();
        psiFile.acceptChildren(positionalArgumentFinder);
        return (RobotPositionalArgument) positionalArgument.replace(positionalArgumentFinder.positionalArgument);
    }

    private static final class RobotPositionalArgumentFinder extends RobotVisitor {

        private RobotPositionalArgument positionalArgument;

        @Override
        public void visitElement(@NotNull PsiElement element) {
            super.visitElement(element);
            element.acceptChildren(this);
        }

        @Override
        public void visitPositionalArgument(@NotNull RobotPositionalArgument o) {
            super.visitPositionalArgument(o);
            positionalArgument = o;
        }
    }
}
