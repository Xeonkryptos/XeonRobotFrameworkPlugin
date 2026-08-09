package dev.xeonkryptos.xeonrobotframeworkplugin.localization

object DefaultLocalizationTypeMappingProvider : LocalizationTypeMappingProvider {

    private val sectionTypeMappings = mapOf(
        "setting" to SectionType.SETTINGS,
        "settings" to SectionType.SETTINGS,
        "variable" to SectionType.VARIABLES,
        "variables" to SectionType.VARIABLES,
        "test case" to SectionType.TEST_CASES,
        "test cases" to SectionType.TEST_CASES,
        "task" to SectionType.TASKS,
        "tasks" to SectionType.TASKS,
        "keyword" to SectionType.KEYWORDS,
        "keywords" to SectionType.KEYWORDS,
        "comment" to SectionType.COMMENTS,
        "comments" to SectionType.COMMENTS
    )

    private val globalSettingTypeMappings = mapOf(
        "library" to GlobalSettingType.LIBRARY,
        "resource" to GlobalSettingType.RESOURCE,
        "variables" to GlobalSettingType.VARIABLES,
        "documentation" to GlobalSettingType.DOCUMENTATION,
        "suite setup" to GlobalSettingType.SUITE_SETUP,
        "suite teardown" to GlobalSettingType.SUITE_TEARDOWN,
        "name" to GlobalSettingType.NAME,
        "metadata" to GlobalSettingType.METADATA,
        "test tags" to GlobalSettingType.TEST_TAGS,
        "test setup" to GlobalSettingType.TEST_SETUP,
        "test teardown" to GlobalSettingType.TEST_TEARDOWN,
        "test timeout" to GlobalSettingType.TEST_TIMEOUT,
        "test template" to GlobalSettingType.TEST_TEMPLATE,
        "task tags" to GlobalSettingType.TASK_TAGS,
        "task setup" to GlobalSettingType.TASK_SETUP,
        "task teardown" to GlobalSettingType.TASK_TEARDOWN,
        "task timeout" to GlobalSettingType.TASK_TIMEOUT,
        "task template" to GlobalSettingType.TASK_TEMPLATE,
        "keyword tags" to GlobalSettingType.KEYWORD_TAGS,
        "default tags" to GlobalSettingType.DEFAULT_TAGS
    )

    private val localSettingTypeMappings = mapOf(
        "tags" to LocalSettingType.TAGS,
        "setup" to LocalSettingType.SETUP,
        "teardown" to LocalSettingType.TEARDOWN,
        "timeout" to LocalSettingType.TIMEOUT,
        "arguments" to LocalSettingType.ARGUMENTS
    )

    private val behaviourDrivenIdentifierTypeMappings = mapOf(
        "given" to BehaviourDrivenType.GIVEN,
        "when" to BehaviourDrivenType.WHEN,
        "then" to BehaviourDrivenType.THEN,
        "and" to BehaviourDrivenType.AND,
        "but" to BehaviourDrivenType.BUT
    )

    override fun getSectionTypeMapping(sectionName: CharSequence): SectionType? = sectionTypeMappings[sectionName]

    override fun getGlobalSettingTypeMapping(globalSettingName: CharSequence): GlobalSettingType? = globalSettingTypeMappings[globalSettingName]

    override fun getLocalSettingTypeMapping(localSettingName: CharSequence): LocalSettingType? = localSettingTypeMappings[localSettingName]

    override fun getBehaviourDrivenIdentifierTypeMapping(behaviourDrivenName: CharSequence): BehaviourDrivenType? = behaviourDrivenIdentifierTypeMappings[behaviourDrivenName]
}
