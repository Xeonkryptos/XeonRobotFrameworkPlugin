package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiNamedElement;

import java.util.Collection;

public interface BracketSetting extends RobotStatement, PsiNamedElement {

   /**
    * Determines if the current element is an '[Arguments]' element.
    *
    * @return true if this is an argument element; false otherwise.
    */
   boolean isArguments();

   Collection<DefinedParameter> getArguments();

   /**
    * Determines if the current element is a '[Teardown]' element.
    *
    * @return true if this is a teardown element; false otherwise.
    */
   boolean isTeardown();
}
