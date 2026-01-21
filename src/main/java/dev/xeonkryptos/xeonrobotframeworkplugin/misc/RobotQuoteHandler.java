package dev.xeonkryptos.xeonrobotframeworkplugin.misc;

import com.jetbrains.python.editor.BaseQuoteHandler;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenSets;

public class RobotQuoteHandler extends BaseQuoteHandler {

    public RobotQuoteHandler() {
        super(RobotTokenSets.STRING_SET, new char[] { ']', '}' });
    }
}
