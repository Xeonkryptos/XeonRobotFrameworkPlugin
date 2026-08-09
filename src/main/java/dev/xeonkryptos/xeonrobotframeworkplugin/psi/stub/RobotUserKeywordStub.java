package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.NamedStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RobotUserKeywordStub extends NamedStub<RobotUserKeywordStatement> {

    @NotNull
    @Override
    String getName();

    @Nullable
    String getEmbeddedKeywordPatternString();
}
