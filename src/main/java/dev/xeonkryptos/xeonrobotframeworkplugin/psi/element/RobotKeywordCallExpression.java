package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.Collection;
import java.util.OptionalInt;

public interface RobotKeywordCallExpression {

    default boolean allRequiredParametersArePresent() {
        return computeMissingRequiredParameters().isEmpty();
    }

    Collection<String> computeMissingRequiredParameters();

    Collection<DefinedParameter> computeMissingParameters();

    Collection<DefinedParameter> getAvailableParameters();

    Collection<RobotArgument> getAllCallArguments();

    OptionalInt getStartOfKeywordsOnlyIndex();
}
