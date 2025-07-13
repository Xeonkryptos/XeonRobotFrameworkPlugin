package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class RobotTokenType extends IElementType {

    public RobotTokenType(@NotNull @NonNls String debugName) {
        super(debugName, RobotLanguage.INSTANCE);
    }
}
