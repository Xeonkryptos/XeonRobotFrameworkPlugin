package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
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

    @Override
    public void visitParameter(@NotNull RobotParameter o) {
        super.visitParameter(o);
        visitArgument(o);
    }

    @Override
    public void visitPositionalArgument(@NotNull RobotPositionalArgument o) {
        super.visitPositionalArgument(o);
        visitArgument(o);
    }

    public Collection<RobotArgument> getArguments() {
        return arguments;
    }
}
