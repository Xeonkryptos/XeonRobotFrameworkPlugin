package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.lexer.FlexAdapter;

public class RobotExtendedVariableAccessLayerAdapter extends FlexAdapter {

    public RobotExtendedVariableAccessLayerAdapter() {
        super(new RobotExtendedVariableAccessLayerLexer());
    }
}
