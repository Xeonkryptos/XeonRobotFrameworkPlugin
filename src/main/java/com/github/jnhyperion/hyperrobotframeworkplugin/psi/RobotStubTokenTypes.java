package com.github.jnhyperion.hyperrobotframeworkplugin.psi;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.stub.element.KeywordDefinitionStubElement;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.stub.element.KeywordStatementStubElement;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.stub.element.VariableDefinitionStubElement;

public interface RobotStubTokenTypes {

    RobotStubFileElementType ROBOT_FILE = new RobotStubFileElementType();

    KeywordDefinitionStubElement KEYWORD_DEFINITION = new KeywordDefinitionStubElement("KEYWORD_DEFINITION");
    VariableDefinitionStubElement VARIABLE_DEFINITION = new VariableDefinitionStubElement("VARIABLE_DEFINITION");
    KeywordStatementStubElement KEYWORD_STATEMENT = new KeywordStatementStubElement("KEYWORD_STATEMENT");
}
