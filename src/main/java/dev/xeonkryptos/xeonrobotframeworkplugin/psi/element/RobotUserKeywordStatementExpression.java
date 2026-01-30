package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.Collection;

public interface RobotUserKeywordStatementExpression {

    Collection<DefinedParameter> getInputParameters();

    Collection<DefinedVariable> getDynamicGlobalVariables();
}
