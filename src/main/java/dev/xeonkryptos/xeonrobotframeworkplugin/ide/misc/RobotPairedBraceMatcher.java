package dev.xeonkryptos.xeonrobotframeworkplugin.ide.misc;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotPairedBraceMatcher implements PairedBraceMatcher {

    private static final BracePair[] BRACE_PAIRS = new BracePair[] { new BracePair(RobotTypes.SCALAR_VARIABLE_START, RobotTypes.VARIABLE_END, false),
                                                                     new BracePair(RobotTypes.LIST_VARIABLE_START, RobotTypes.VARIABLE_END, false),
                                                                     new BracePair(RobotTypes.DICT_VARIABLE_START, RobotTypes.VARIABLE_END, false),
                                                                     new BracePair(RobotTypes.ENV_VARIABLE_START, RobotTypes.VARIABLE_END, false),
                                                                     new BracePair(RobotTypes.PYTHON_EXPRESSION_START,
                                                                                   RobotTypes.PYTHON_EXPRESSION_END,
                                                                                   false) };

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
