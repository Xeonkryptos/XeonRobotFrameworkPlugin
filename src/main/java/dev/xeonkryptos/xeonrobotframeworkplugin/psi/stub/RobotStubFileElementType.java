package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.PsiFileStub;
import com.intellij.psi.tree.IStubFileElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;

public class RobotStubFileElementType extends IStubFileElementType<PsiFileStub<?>> {

    public static final int STUB_FILE_VERSION = 16;

    public RobotStubFileElementType() {
        super("ROBOT_FILE", RobotLanguage.INSTANCE);
    }

    @Override
    public int getStubVersion() {
        return STUB_FILE_VERSION;
    }
}
