package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiNamedElement;

public interface VariableDefinition extends RobotStatement, PsiNamedElement, DefinedVariable {

   String getVariableName();

   boolean isNested();
}
