package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element.KeywordDefinitionStubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element.KeywordStatementStubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element.PositionalArgumentStubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element.VariableDefinitionStubElement;

public interface RobotStubTokenTypes {

    RobotStubFileElementType ROBOT_FILE = new RobotStubFileElementType();

    KeywordDefinitionStubElement KEYWORD_DEFINITION = new KeywordDefinitionStubElement("KEYWORD_DEFINITION");
    VariableDefinitionStubElement VARIABLE_DEFINITION = new VariableDefinitionStubElement("VARIABLE_DEFINITION");
    KeywordStatementStubElement KEYWORD_STATEMENT = new KeywordStatementStubElement("KEYWORD_STATEMENT");
    PositionalArgumentStubElement ARGUMENT = new PositionalArgumentStubElement("ARGUMENT");
}
