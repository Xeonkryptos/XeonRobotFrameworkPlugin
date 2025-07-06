package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public final class RobotKeywordCallArgumentsCollector extends RobotVisitor {

    private final Set<RobotArgument> arguments = new LinkedHashSet<>();

    @Override
    public void visitArgument(@NotNull RobotArgument o) {
        arguments.add(o);
    }

    public Collection<RobotArgument> getArguments() {
        return arguments;
    }
}
