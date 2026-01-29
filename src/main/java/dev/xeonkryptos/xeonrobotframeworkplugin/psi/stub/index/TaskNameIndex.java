package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.util.Processor;
import com.intellij.util.indexing.IdFilter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class TaskNameIndex extends StringStubIndexExtension<RobotTaskStatement> {

    public static final StubIndexKey<String, RobotTaskStatement> KEY = StubIndexKey.createIndexKey("robot.task");

    public static Collection<RobotTaskStatement> find(String name, Project project, GlobalSearchScope scope) {
        return StubIndex.getElements(KEY, name, project, scope, RobotTaskStatement.class);
    }

    public static boolean processAllKeys(@NotNull GlobalSearchScope scope, @Nullable IdFilter idFilter, @NotNull Processor<? super String> processor) {
        return StubIndex.getInstance().processAllKeys(KEY, processor, scope, idFilter);
    }

    @NotNull
    @Override
    public StubIndexKey<String, RobotTaskStatement> getKey() {
        return KEY;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + 1;
    }
}
