package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiNameIdentifierOwner;

public interface VariableDefinition extends RobotStatement, PsiNameIdentifierOwner, DefinedVariable {

   String getVariableName();

   boolean isNested();
}
