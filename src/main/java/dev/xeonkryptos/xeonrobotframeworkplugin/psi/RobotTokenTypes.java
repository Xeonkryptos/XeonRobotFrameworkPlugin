package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

public interface RobotTokenTypes {

   RobotElementType HEADING = new RobotElementType("HEADING");
   RobotElementType SETTING = new RobotElementType("SETTING");
   RobotElementType BRACKET_SETTING = new RobotElementType("BRACKET_SETTING");
   RobotElementType IMPORT = new RobotElementType("IMPORT");
   RobotElementType KEYWORD_DEFINITION_ID = new RobotElementType("KEYWORD_DEFINITION_ID");
   RobotElementType KEYWORD = new RobotElementType("KEYWORD");
   RobotElementType PARAMETER = new RobotElementType("PARAMETER");
   RobotElementType PARAMETER_ID = new RobotElementType("PARAMETER_ID");
   RobotElementType VARIABLE_DEFINITION_GROUP = new RobotElementType("VARIABLE_DEFINITION_GROUP");
   RobotElementType VARIABLE_DEFINITION_ID = new RobotElementType("VARIABLE_DEFINITION_ID");
   RobotElementType COMMENT = new RobotElementType("COMMENT");
   RobotElementType GHERKIN = new RobotElementType("GHERKIN");
   RobotElementType SYNTAX_MARKER = new RobotElementType("SYNTAX_MARKER");
   RobotElementType KEYWORD_PART = new RobotElementType("KEYWORD_PART");
   RobotElementType ERROR = new RobotElementType("ERROR");
   RobotElementType WHITESPACE = new RobotElementType("WHITESPACE");
}
