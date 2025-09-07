package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.Collection;

public interface RobotCallArgumentsContainer {

    Collection<String> getDefinedParameterNames();

    Collection<RobotArgument> getAllCallArguments();

    default boolean allRequiredParametersArePresent() {
        return computeMissingRequiredParameters().isEmpty();
    }

    Collection<String> computeMissingRequiredParameters();

    Collection<DefinedParameter> computeMissingParameters();
}
