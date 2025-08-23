package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.Collection;
import java.util.Optional;

public interface RobotKeywordCallExpression {

    default boolean allRequiredParametersArePresent() {
        return computeMissingRequiredParameters().isEmpty();
    }

    Collection<String> computeMissingRequiredParameters();

    Collection<DefinedParameter> getAvailableParameters();

    Collection<RobotArgument> getAllCallArguments();

    Optional<Integer> getStartOfKeywordsOnlyIndex();
}
