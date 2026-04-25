package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.Collection;
import java.util.OptionalInt;

public interface RobotUserKeywordStatementExpression {

    Collection<DefinedParameter> getInputParameters();

    Collection<DefinedVariable> getDynamicGlobalVariables();

    OptionalInt computeKeywordsOnlyStartIndexFor();
}
