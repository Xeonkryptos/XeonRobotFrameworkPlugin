package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.Collection;

public interface RobotKeywordCallExpression {

    default boolean allRequiredParametersArePresent() {
        return computeMissingRequiredParameters().isEmpty();
    }

    Collection<String> computeMissingRequiredParameters();

    Collection<DefinedParameter> getAvailableParameters();

    Collection<RobotArgument> getAllCallArguments();

    String getSimpleKeywordName();
}
