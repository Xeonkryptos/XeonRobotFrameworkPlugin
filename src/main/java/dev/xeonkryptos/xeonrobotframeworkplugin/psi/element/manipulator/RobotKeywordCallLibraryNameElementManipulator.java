package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallLibraryName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotKeywordCallLibraryNameElementManipulator extends AbstractElementManipulator<RobotKeywordCallLibraryName> {

    @Nullable
    @Override
    public RobotKeywordCallLibraryName handleContentChange(@NotNull RobotKeywordCallLibraryName keywordCallLibraryName,
                                                           @NotNull TextRange textRange,
                                                           String newText) throws IncorrectOperationException {
        String original = keywordCallLibraryName.getText();
        String newContent = textRange.replace(original, newText);
        String fileContent = """
                             *** Test Case ***
                             Dummy
                                 %s.dummy
                             """.formatted(newContent);

        PsiFile psiFile = RobotElementGenerator.getInstance(keywordCallLibraryName.getProject()).createDummyPsiFile(fileContent);
        if (psiFile == null) {
            return null;
        }
        RobotKeywordCallLibraryNameFinder finder = new RobotKeywordCallLibraryNameFinder();
        psiFile.acceptChildren(finder);
        return (RobotKeywordCallLibraryName) keywordCallLibraryName.replace(finder.keywordCallLibraryName);
    }

    private static final class RobotKeywordCallLibraryNameFinder extends RobotVisitor {

        private RobotKeywordCallLibraryName keywordCallLibraryName;

        @Override
        public void visitElement(@NotNull PsiElement element) {
            super.visitElement(element);
            element.acceptChildren(this);
        }

        @Override
        public void visitKeywordCallLibraryName(@NotNull RobotKeywordCallLibraryName o) {
            super.visitKeywordCallLibraryName(o);
            keywordCallLibraryName = o;
        }
    }
}
