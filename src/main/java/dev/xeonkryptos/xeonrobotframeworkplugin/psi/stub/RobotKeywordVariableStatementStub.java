package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableName;
import org.jetbrains.annotations.NotNull;

public interface RobotKeywordVariableStatementStub extends StubElement<RobotKeywordVariableStatement> {

    String[] getVariableNames();
}
