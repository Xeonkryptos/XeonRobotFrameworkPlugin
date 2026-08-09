package dev.xeonkryptos.xeonrobotframeworkplugin.localization

enum class SectionType {
    SETTINGS, VARIABLES, TEST_CASES, TASKS, KEYWORDS, COMMENTS
}

enum class GlobalSettingType {
    LIBRARY,
    RESOURCE,
    VARIABLES,
    NAME,
    DOCUMENTATION,
    SUITE_SETUP,
    SUITE_TEARDOWN,
    METADATA,
    TEST_TAGS,
    TEST_SETUP,
    TEST_TEARDOWN,
    TEST_TIMEOUT,
    TEST_TEMPLATE,
    TASK_TAGS,
    TASK_SETUP,
    TASK_TEARDOWN,
    TASK_TIMEOUT,
    TASK_TEMPLATE,
    KEYWORD_TAGS,
    DEFAULT_TAGS
}

enum class LocalSettingType {
    TAGS, SETUP, TEARDOWN, TIMEOUT, ARGUMENTS
}

enum class BehaviourDrivenType {
    GIVEN, WHEN, THEN, AND, BUT
}
