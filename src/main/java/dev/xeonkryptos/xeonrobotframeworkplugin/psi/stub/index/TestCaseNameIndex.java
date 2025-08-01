package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import org.jetbrains.annotations.NotNull;

public class TestCaseNameIndex extends StringStubIndexExtension<RobotTestCaseStatement> {

    public static final StubIndexKey<String, RobotTestCaseStatement> KEY = StubIndexKey.createIndexKey("robot.testcase");

    @NotNull
    @Override
    public StubIndexKey<String, RobotTestCaseStatement> getKey() {
        return KEY;
    }
}
