package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.NamedStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RobotKeywordCallStub extends NamedStub<RobotKeywordCall> {

    @Nullable
    String getLibraryName();

    @NotNull
    @Override
    String getName();
}
