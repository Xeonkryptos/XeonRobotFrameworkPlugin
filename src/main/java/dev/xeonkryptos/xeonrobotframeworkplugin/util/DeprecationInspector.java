package dev.xeonkryptos.xeonrobotframeworkplugin.util;

import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyFunction;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.PyElementDeprecatedVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.PyElementParentTraversalVisitor;

import java.util.List;

public final class DeprecationInspector {

    public static boolean isDeprecated(PsiElement element) {
        if (element instanceof PyFunction pyFunction) {
            return isPyFunctionDeprecated(pyFunction);
        } else if (element instanceof RobotUserKeywordStatement userKeywordStatement) {
            return isUserKeywordDeprecated(userKeywordStatement);
        }
        return false;
    }

    private static boolean isPyFunctionDeprecated(PyFunction pyFunction) {
        PyElementDeprecatedVisitor deprecationVisitor = new PyElementDeprecatedVisitor(pyFunction);
        PyElementVisitor pyElementParentTraversalVisitor = new PyElementParentTraversalVisitor(deprecationVisitor);
        pyFunction.accept(pyElementParentTraversalVisitor);
        return deprecationVisitor.isDeprecated();
    }

    private static boolean isUserKeywordDeprecated(RobotUserKeywordStatement userKeywordStatement) {
        for (RobotLocalSetting robotLocalSetting : userKeywordStatement.getLocalSettingList()) {
            if ("Documentation".equalsIgnoreCase(robotLocalSetting.getSettingName())) {
                List<RobotPositionalArgument> positionalArgumentList = robotLocalSetting.getPositionalArgumentList();
                if (!positionalArgumentList.isEmpty()) {
                    RobotPositionalArgument documentationArgument = positionalArgumentList.getFirst();
                    String documentation = documentationArgument.getText();
                    if (documentation.startsWith(RobotNames.DEPRECATED_PREFIX)) {
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }
}
