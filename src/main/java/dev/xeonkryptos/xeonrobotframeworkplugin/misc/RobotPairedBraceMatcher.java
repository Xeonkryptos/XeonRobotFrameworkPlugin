package dev.xeonkryptos.xeonrobotframeworkplugin.misc;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotPairedBraceMatcher implements PairedBraceMatcher {

    private static final BracePair[] BRACE_PAIRS = new BracePair[] { new BracePair(RobotTypes.VARIABLE_LBRACE, RobotTypes.VARIABLE_RBRACE, true),
                                                                     new BracePair(RobotTypes.VARIABLE_ACCESS_START, RobotTypes.VARIABLE_ACCESS_END, true),
                                                                     new BracePair(RobotTypes.LOCAL_SETTING_START, RobotTypes.LOCAL_SETTING_END, true),
                                                                     new BracePair(RobotTypes.PYTHON_EXPRESSION_START, RobotTypes.PYTHON_EXPRESSION_END, true),
                                                                     new BracePair(RobotTypes.FOR, RobotTypes.END, true),
                                                                     new BracePair(RobotTypes.WHILE, RobotTypes.END, true),
                                                                     new BracePair(RobotTypes.GROUP, RobotTypes.END, true),
                                                                     new BracePair(RobotTypes.IF, RobotTypes.END, true),
                                                                     new BracePair(RobotTypes.TRY, RobotTypes.END, true) };

    @Override
    public BracePair @NotNull [] getPairs() {
        return BRACE_PAIRS;
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
        return true;
    }

    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
