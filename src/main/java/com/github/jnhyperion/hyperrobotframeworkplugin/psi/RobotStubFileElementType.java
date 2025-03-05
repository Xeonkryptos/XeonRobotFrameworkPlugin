package com.github.jnhyperion.hyperrobotframeworkplugin.psi;

import com.intellij.psi.stubs.PsiFileStub;
import com.intellij.psi.tree.IStubFileElementType;

public class RobotStubFileElementType extends IStubFileElementType<PsiFileStub<?>> {

    public RobotStubFileElementType() {
        super("ROBOT_FILE", RobotLanguage.INSTANCE);
    }

    @Override
    public int getStubVersion() {
        return 0;
    }
}
