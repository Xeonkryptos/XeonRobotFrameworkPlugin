package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiElement;

public interface RobotImportGlobalSettingExpression extends PsiElement {

    RobotImportArgument getImportedFile();
}
