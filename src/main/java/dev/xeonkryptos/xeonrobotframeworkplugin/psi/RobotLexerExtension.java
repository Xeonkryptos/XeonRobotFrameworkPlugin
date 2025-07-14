package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

public class RobotLexerExtension extends RobotLexer {

    @Override
    public void reset(CharSequence buffer, int start, int end, int initialState) {
        super.reset(buffer, start, end, initialState);
        resetLexer();
    }
}
