package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.lexer.FlexAdapter;

public class RobotParsingLayerLexerAdapter extends FlexAdapter {

    public RobotParsingLayerLexerAdapter() {
        super(new RobotExtendedVariableAccessLayerLexer());
    }
}
