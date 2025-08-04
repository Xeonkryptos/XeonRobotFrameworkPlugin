package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotKeywordCallNameElementManipulator extends AbstractElementManipulator<RobotKeywordCallName> {

    @Nullable
    @Override
    public RobotKeywordCallName handleContentChange(@NotNull RobotKeywordCallName keywordCallName, @NotNull TextRange textRange, String newText) throws
                                                                                                                                                 IncorrectOperationException {
        String original = keywordCallName.getText();
        String newContent = textRange.replace(original, newText);
        String fileContent = """
                             *** Test Case ***
                             Dummy
                                 %s
                             """.formatted(newContent);

        PsiFile psiFile = RobotElementGenerator.getInstance(keywordCallName.getProject()).createDummyPsiFile(fileContent);
        if (psiFile == null) {
            return null;
        }
        RobotKeywordCallNameFinder finder = new RobotKeywordCallNameFinder();
        psiFile.acceptChildren(finder);
        return (RobotKeywordCallName) keywordCallName.replace(finder.keywordCallName);
    }

    private static final class RobotKeywordCallNameFinder extends RobotVisitor {

        private RobotKeywordCallName keywordCallName;

        @Override
        public void visitElement(@NotNull PsiElement element) {
            super.visitElement(element);
            element.acceptChildren(this);
        }

        @Override
        public void visitKeywordCallName(@NotNull RobotKeywordCallName o) {
            super.visitKeywordCallName(o);
            keywordCallName = o;
        }
    }
}
