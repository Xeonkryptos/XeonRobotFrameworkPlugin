package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotKeywordCallStubImpl extends StubBase<RobotKeywordCall> implements RobotKeywordCallStub {

    private final String myLibraryName;
    private final String myKeyword;

    public RobotKeywordCallStubImpl(final StubElement parent, final String libraryName, final String keyword) {
        super(parent, (IStubElementType<?, ?>) RobotTypes.KEYWORD_CALL);

        myLibraryName = libraryName;
        myKeyword = keyword;
    }

    @Nullable
    @Override
    public String getLibraryName() {
        return myLibraryName;
    }

    @NotNull
    @Override
    public String getName() {
        return myKeyword;
    }
}
