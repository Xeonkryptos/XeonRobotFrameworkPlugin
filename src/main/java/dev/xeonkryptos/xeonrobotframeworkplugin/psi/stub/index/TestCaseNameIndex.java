package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.util.Processor;
import com.intellij.util.indexing.IdFilter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubFileElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class TestCaseNameIndex extends StringStubIndexExtension<RobotTestCaseStatement> {

    public static final StubIndexKey<String, RobotTestCaseStatement> KEY = StubIndexKey.createIndexKey("robot.testcase");

    public static Collection<RobotTestCaseStatement> find(String name, Project project, GlobalSearchScope scope) {
        return StubIndex.getElements(KEY, name, project, scope, RobotTestCaseStatement.class);
    }

    public static boolean processAllKeys(@NotNull GlobalSearchScope scope, @Nullable IdFilter filter, @NotNull Processor<? super String> processor) {
        return StubIndex.getInstance().processAllKeys(KEY, processor, scope, filter);
    }

    @NotNull
    @Override
    public StubIndexKey<String, RobotTestCaseStatement> getKey() {
        return KEY;
    }

    @Override
    public int getVersion() {
        return RobotStubFileElementType.STUB_FILE_VERSION + super.getVersion() + 1;
    }
}
