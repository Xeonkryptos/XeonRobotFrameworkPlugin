package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.NamedStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import org.jetbrains.annotations.NotNull;

public interface RobotTestCaseStatementStub extends NamedStub<RobotTestCaseStatement> {

    @NotNull
    @Override
    String getName();
}
