package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.lexer.FlexAdapter;
import dev.xeonkryptos.xeonrobotframeworkplugin.MyLogger;
import org.jetbrains.annotations.NotNull;

public class RobotLexerAdapter extends FlexAdapter {

    public RobotLexerAdapter() {
        super(new RobotLexerExtension());
    }
}
