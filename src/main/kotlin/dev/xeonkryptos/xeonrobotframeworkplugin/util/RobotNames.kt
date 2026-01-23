package dev.xeonkryptos.xeonrobotframeworkplugin.util

object RobotNames {

    const val INIT_DOT_ROBOT = "__init__.robot"
    const val LIST_VARIABLE_TYPE_INDICATOR_PREFIX = "LIST__"
    const val DICT_VARIABLE_TYPE_INDICATOR_PREFIX = "DICT__"
    const val BUILTIN_NAMESPACE = "BuiltIn"

    const val BUILTIN_FULL_PYTHON_NAMESPACE: String = "robot.libraries.${BUILTIN_NAMESPACE}"

    const val TEST_CASE_SECTION_NAME = "Test Case"
    const val TASK_SECTION_NAME = "Task"
    const val KEYWORD_SECTION_NAME = "Keyword"
    const val VARIABLE_SECTION_NAME = "Variable"
    const val SETTING_SECTION_NAME = "Setting"
    const val COMMENT_SECTION_NAME = "Comment"

    const val TAGS_LOCAL_SETTING_NAME = "Tags"
    const val DOCUMENTATION_LOCAL_SETTING_NAME = "Documentation"
    const val TEARDOWN_LOCAL_SETTING_NAME = "Teardown"
    const val TEMPLATE_LOCAL_SETTING_NAME = "Template"
    const val RETURN_LOCAL_SETTING_NAME = "Return"
    const val DEFAULT_TAGS_LOCAL_SETTING_NAME = "Default Tags"
    const val FORCE_TAGS_LOCAL_SETTING_NAME = "Force Tags"

    const val SUITE_SETUP_GLOBAL_SETTING_NAME = "Suite Setup"
    const val SUITE_TEARDOWN_GLOBAL_SETTING_NAME = "Suite Teardown"
    const val TEST_SETUP_GLOBAL_SETTING_NAME = "Test Setup"
    const val TEST_TEARDOWN_GLOBAL_SETTING_NAME = "Test Teardown"

    const val RETURN_RESERVED_NAME = "RETURN"

    const val FOR_IN_ENUMERATE_RESERVED_NAME = "IN ENUMERATE"
    const val FOR_IN_ZIP_RESERVED_NAME = "IN ZIP"

    const val WITH_NAME_RESERVED_NAME = "WITH NAME"

    const val CONTINUE_FOR_LOOP_NORMALIZED_KEYWORD_NAME = "continueforloop"
    const val CONTINUE_FOR_LOOP_IF_NORMALIZED_KEYWORD_NAME = "continueforloopif"
    const val EXIT_FOR_LOOP_NORMALIZED_KEYWORD_NAME = "exitforloop"
    const val EXIT_FOR_LOOP_IF_NORMALIZED_KEYWORD_NAME = "exitforloopif"
    const val CREATE_DICTIONARY_NORMALIZED_KEYWORD_NAME: String = "createdictionary"
    const val RUN_KEYWORD_IF_NORMALIZED_KEYWORD_NAME: String = "runkeywordif"
    const val RUN_KEYWORD_IF_ALL_TESTS_PASSED_NORMALIZED_KEYWORD_NAME = "runkeywordifalltestspassed"
    const val RUN_KEYWORD_IF_ANY_TESTS_FAILED_NORMALIZED_KEYWORD_NAME = "runkeywordifanytestsfailed"
    const val RUN_KEYWORD_IF_TEST_FAILED_NORMALIZED_KEYWORD_NAME = "runkeywordiftestfailed"
    const val RUN_KEYWORD_IF_TEST_PASSED_NORMALIZED_KEYWORD_NAME = "runkeywordiftestpassed"
    const val RUN_KEYWORD_IF_TIMEOUT_OCCURRED_NORMALIZED_KEYWORD_NAME = "runkeywordiftimeoutoccurred"
    const val RUN_KEYWORD_AND_EXPECT_ERROR_NORMALIZED_KEYWORD_NAME = "runkeywordandexpecterror"
    const val RETURN_FROM_KEYWORD_NORMALIZED_KEYWORD_NAME = "returnfromkeyword"
    const val RETURN_FROM_KEYWORD_IF_NORMALIZED_KEYWORD_NAME = "returnfromkeywordif"
    const val RUN_KEYWORD_AND_RETURN_NORMALIZED_KEYWORD_NAME = "runkeywordandreturn"
    const val RUN_KEYWORD_AND_RETURN_IF_NORMALIZED_KEYWORD_NAME = "runkeywordandreturnif"

    const val DEPRECATED_PREFIX = "*DEPRECATED"
}
