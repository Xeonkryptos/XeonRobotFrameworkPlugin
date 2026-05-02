package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiElement;

import java.util.Collection;

public interface RobotCallArgumentsContainer {

    Collection<String> getDefinedParameterNames();

    Collection<RobotArgument> getAllCallArguments();

    default boolean allRequiredParametersArePresent() {
        return computeMissingRequiredParameters().isEmpty();
    }

    Collection<String> computeMissingRequiredParameters();

    default Collection<DefinedParameter> computeMissingParameters() {
        return computeMissingParameters(null);
    }

    Collection<DefinedParameter> computeMissingParameters(PsiElement ignorableElement);
}
