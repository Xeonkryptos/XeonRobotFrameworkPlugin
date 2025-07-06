package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.Collection;

public interface RobotKeywordCallExpression {

    boolean allRequiredParametersArePresent();

    Collection<DefinedParameter> getAvailableParameters();

    Collection<RobotArgument> getAllCallArguments();
}
