package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
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
    public static RobotVariableId getNameIdentifier(RobotVariable variable) {
        return PsiTreeUtil.findChildOfType(variable, RobotVariableId.class);
    }

    @Nullable
    public static String getName(RobotVariable variable) {
        RobotVariableId nameIdentifier = getNameIdentifier(variable);
        return nameIdentifier != null ? nameIdentifier.getName() : null;
    }

    @Nullable
    public static String getName(RobotVariableId variableId) {
        PsiElement contentElement = variableId.getContent();
        if (contentElement == null) {
            return null;
        }
        String nameContent = contentElement.getText();
        for (int i = 0; i < nameContent.length(); i++) {
            char c = nameContent.charAt(i);
            if (c == '.' || c == '[' || c == ':' || c == '+' || c == '-' || c == '*' || c == '/') {
                return nameContent.substring(0, i);
            }
        }
        return nameContent;
    }

    @NotNull
    public static String getName(RobotParameter parameter) {
        RobotParameterId nameIdentifier = parameter.getNameIdentifier();
        return nameIdentifier.getName();
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
