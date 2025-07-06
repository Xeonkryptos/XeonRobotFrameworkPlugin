package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotDictVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotEnvironmentVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotListVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotScalarVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotKeywordReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotVariableReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotPsiImplUtil {

    @Nullable
    public static PsiElement getNameIdentifier(RobotVariable variable) {
        return PsiTreeUtil.findChildOfType(variable, RobotVariableId.class);
    }

    @Nullable
    public static String getName(RobotVariable variable) {
        PsiElement nameIdentifier = getNameIdentifier(variable);
        return nameIdentifier != null ? nameIdentifier.getText() : null;
    }

    @NotNull
    public static PsiReference getReference(RobotVariable variable) {
        return new RobotVariableReference(variable);
    }

    @NotNull
    public static PsiReference getReference(RobotKeywordCallId keywordCallId) {
        return new RobotKeywordReference(keywordCallId);
    }
}
