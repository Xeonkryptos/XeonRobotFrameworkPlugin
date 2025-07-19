package dev.xeonkryptos.xeonrobotframeworkplugin.ide.actionhandler;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotKeywordCallEnterActionHandler extends AbstractRobotSmartMultilineEnterActionHandler<RobotKeywordCall> {

    public RobotKeywordCallEnterActionHandler() {
        super(RobotKeywordCall.class);
    }
}
