package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiElement;

import java.util.Collection;
import java.util.OptionalInt;

public interface RobotKeywordCallExpression {

    Collection<DefinedParameter> getAvailableParameters();

    boolean hasPositionalArgumentsContainer();

    OptionalInt getStartOfKeywordsOnlyIndex();

    OptionalInt getPositionalArgumentsOnlyEndIndex();

    PsiElement findParameterReference(String parameterName);
}
