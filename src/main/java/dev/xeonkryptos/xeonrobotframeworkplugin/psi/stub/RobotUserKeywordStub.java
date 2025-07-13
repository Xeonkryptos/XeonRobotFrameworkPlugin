package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.NamedStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import org.jetbrains.annotations.NotNull;

public interface RobotUserKeywordStub extends NamedStub<RobotUserKeywordStatement> {

    @NotNull
    @Override
    String getName();
}
