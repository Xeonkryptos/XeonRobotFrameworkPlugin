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

    private val sectionTypeMappingsReversal: Map<SectionType, CharSequence> = mapOf(
        SectionType.SETTINGS to "Settings",
        SectionType.VARIABLES to "Variables",
        SectionType.TEST_CASES to "Test Cases",
        SectionType.TASKS to "Tasks",
        SectionType.KEYWORDS to "Keywords",
        SectionType.COMMENTS to "Comments"
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

    private val globalSettingTypeMappingsReversal: Map<GlobalSettingType, CharSequence> = mapOf(
        GlobalSettingType.LIBRARY to "Library",
        GlobalSettingType.RESOURCE to "Resource",
        GlobalSettingType.VARIABLES to "Variables",
        GlobalSettingType.DOCUMENTATION to "Documentation",
        GlobalSettingType.SUITE_SETUP to "Suite Setup",
        GlobalSettingType.SUITE_TEARDOWN to "Suite Teardown",
        GlobalSettingType.NAME to "Name",
        GlobalSettingType.METADATA to "Metadata",
        GlobalSettingType.TEST_TAGS to "Test Tags",
        GlobalSettingType.TEST_SETUP to "Test Setup",
        GlobalSettingType.TEST_TEARDOWN to "Test Teardown",
        GlobalSettingType.TEST_TIMEOUT to "Test Timeout",
        GlobalSettingType.TEST_TEMPLATE to "Test Template",
        GlobalSettingType.TASK_TAGS to "Task Tags",
        GlobalSettingType.TASK_SETUP to "Task Setup",
        GlobalSettingType.TASK_TEARDOWN to "Task Teardown",
        GlobalSettingType.TASK_TIMEOUT to "Task Timeout",
        GlobalSettingType.TASK_TEMPLATE to "Task Template",
        GlobalSettingType.KEYWORD_TAGS to "Keyword Tags",
        GlobalSettingType.DEFAULT_TAGS to "Default Tags"
    )

    private val localSettingTypeMappings = mapOf(
        "tags" to LocalSettingType.TAGS,
        "setup" to LocalSettingType.SETUP,
        "teardown" to LocalSettingType.TEARDOWN,
        "timeout" to LocalSettingType.TIMEOUT,
        "arguments" to LocalSettingType.ARGUMENTS,
        "template" to LocalSettingType.TEMPLATE
    )

    private val localSettingTypeMappingsReversal: Map<LocalSettingType, CharSequence> = mapOf(
        LocalSettingType.TAGS to "Tags",
        LocalSettingType.SETUP to "Setup",
        LocalSettingType.TEARDOWN to "Teardown",
        LocalSettingType.TIMEOUT to "Timeout",
        LocalSettingType.ARGUMENTS to "Arguments",
        LocalSettingType.TEMPLATE to "Template"
    )

    private val behaviourDrivenIdentifierTypeMappings = mapOf(
        "given" to BehaviourDrivenType.GIVEN,
        "when" to BehaviourDrivenType.WHEN,
        "then" to BehaviourDrivenType.THEN,
        "and" to BehaviourDrivenType.AND,
        "but" to BehaviourDrivenType.BUT
    )

    private val behaviourDrivenIdentifierTypeMappingsReversal: Map<BehaviourDrivenType, CharSequence> = mapOf(
        BehaviourDrivenType.GIVEN to "Given",
        BehaviourDrivenType.WHEN to "When",
        BehaviourDrivenType.THEN to "Then",
        BehaviourDrivenType.AND to "And",
        BehaviourDrivenType.BUT to "But"
    )

    override fun getSectionType(sectionName: CharSequence): SectionType? = sectionTypeMappings[sectionName]

    override fun getSectionName(sectionType: SectionType): List<CharSequence>? = sectionTypeMappingsReversal[sectionType]?.let { listOf(it) }

    override fun getGlobalSettingType(globalSettingName: CharSequence): GlobalSettingType? = globalSettingTypeMappings[globalSettingName]

    override fun getGlobalSettingName(globalSettingType: GlobalSettingType): List<CharSequence>? = globalSettingTypeMappingsReversal[globalSettingType]?.let { listOf(it) }

    override fun getLocalSettingType(localSettingName: CharSequence): LocalSettingType? = localSettingTypeMappings[localSettingName]

    override fun getLocalSettingName(localSettingType: LocalSettingType): List<CharSequence>? = localSettingTypeMappingsReversal[localSettingType]?.let { listOf(it) }

    override fun getBehaviourDrivenIdentifierType(behaviourDrivenName: CharSequence): BehaviourDrivenType? = behaviourDrivenIdentifierTypeMappings[behaviourDrivenName]

    override fun getBehaviourDrivenIdentifierName(behaviourDrivenType: BehaviourDrivenType): List<CharSequence>? =
        behaviourDrivenIdentifierTypeMappingsReversal[behaviourDrivenType]?.let { listOf(it) }
}
