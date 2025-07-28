package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.NamedStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import org.jetbrains.annotations.NotNull;

public interface RobotTaskStatementStub extends NamedStub<RobotTaskStatement> {

    @NotNull
    @Override
    String getName();
}
