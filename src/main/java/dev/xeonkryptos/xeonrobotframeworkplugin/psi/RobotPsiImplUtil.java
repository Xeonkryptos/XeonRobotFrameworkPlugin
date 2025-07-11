package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameterId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotKeywordReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotParameterReference;
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
    public static PsiReference getReference(RobotVariableId variableId) {
        return new RobotVariableReference(variableId);
    }

    @NotNull
    public static PsiReference getReference(RobotKeywordCallId keywordCallId) {
        return new RobotKeywordReference(keywordCallId);
    }

    @NotNull
    public static PsiReference getReference(RobotParameterId parameterId) {
        return new RobotParameterReference(parameterId);
    }
}
