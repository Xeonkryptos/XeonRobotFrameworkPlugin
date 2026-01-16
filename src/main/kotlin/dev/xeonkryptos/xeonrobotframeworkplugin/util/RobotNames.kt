package dev.xeonkryptos.xeonrobotframeworkplugin.util

object RobotNames {

    const val INIT_DOT_ROBOT = "__init__.robot"
    const val LIST_VARIABLE_TYPE_INDICATOR_PREFIX = "LIST__"
    const val DICT_VARIABLE_TYPE_INDICATOR_PREFIX = "DICT__"
    const val TAGS_SETTING_NAME = "Tags"

    const val BUILTIN_NAMESPACE = "BuiltIn"

    const val BUILTIN_FULL_PYTHON_NAMESPACE: String = "robot.libraries.${BUILTIN_NAMESPACE}"

    const val DOCUMENTATION_LOCAL_SETTING_NAME = "Documentation"
    const val TEARDOWN_LOCAL_SETTING_NAME = "Teardown"
    const val TEMPLATE_LOCAL_SETTING_NAME = "Template"
    const val RETURN_LOCAL_SETTING_NAME = "Return"

    const val SUITE_TEARDOWN_GLOBAL_SETTING_NAME = "Suite Teardown"
    const val TEST_TEARDOWN_GLOBAL_SETTING_NAME = "Test Teardown"

    const val FOR_IN_ENUMERATE = "IN ENUMERATE"
    const val FOR_IN_ZIP = "IN ZIP"

    const val CREATE_DICTIONARY_NORMALIZED_KEYWORD_NAME: String = "createdictionary"
    const val RUN_KEYWORD_IF_NORMALIZED_KEYWORD_NAME: String = "runkeywordif"
    const val RUN_KEYWORD_IF_ALL_TESTS_PASSED_NORMALIZED_KEYWORD_NAME = "runkeywordifalltestspassed"
    const val RUN_KEYWORD_IF_ANY_TESTS_FAILED_NORMALIZED_KEYWORD_NAME = "runkeywordifanytestsfailed"
    const val RUN_KEYWORD_IF_TEST_FAILED_NORMALIZED_KEYWORD_NAME = "runkeywordiftestfailed"
    const val RUN_KEYWORD_IF_TEST_PASSED_NORMALIZED_KEYWORD_NAME = "runkeywordiftestpassed"
    const val RUN_KEYWORD_IF_TIMEOUT_OCCURRED_NORMALIZED_KEYWORD_NAME = "runkeywordiftimeoutoccurred"
    const val RUN_KEYWORD_AND_EXPECT_ERROR_NORMALIZED_KEYWORD_NAME = "runkeywordandexpecterror"

    const val DEPRECATED_PREFIX = "*DEPRECATED"
}
