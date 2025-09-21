package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubFileElementType;
import org.jetbrains.annotations.NotNull;

public class KeywordDefinitionNameIndex extends StringStubIndexExtension<RobotUserKeywordStatement> {

    public static final StubIndexKey<String, RobotUserKeywordStatement> KEY = StubIndexKey.createIndexKey("robot.keywordDefinition");

    private static final KeywordDefinitionNameIndex ourInstance = new KeywordDefinitionNameIndex();

    public static KeywordDefinitionNameIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, RobotUserKeywordStatement> getKey() {
        return KEY;
    }

    @Override
    public int getVersion() {
        return RobotStubFileElementType.STUB_FILE_VERSION + super.getVersion() + 1;
    }
}
