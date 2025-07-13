package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.NamedStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import org.jetbrains.annotations.NotNull;

public interface RobotKeywordCallStub extends NamedStub<RobotKeywordCall> {

    @NotNull
    @Override
    String getName();
}
