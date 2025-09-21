package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubFileElementType;
import org.jetbrains.annotations.NotNull;

public class TaskNameIndex extends StringStubIndexExtension<RobotTaskStatement> {

    public static final StubIndexKey<String, RobotTaskStatement> KEY = StubIndexKey.createIndexKey("robot.task");

    @NotNull
    @Override
    public StubIndexKey<String, RobotTaskStatement> getKey() {
        return KEY;
    }

    @Override
    public int getVersion() {
        return RobotStubFileElementType.STUB_FILE_VERSION + super.getVersion() + 1;
    }
}
