package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.lexer.FlexAdapter;

public class RobotLexerAdapter extends FlexAdapter {

    public RobotLexerAdapter() {
        super(new RobotLexer(null));
    }
}
