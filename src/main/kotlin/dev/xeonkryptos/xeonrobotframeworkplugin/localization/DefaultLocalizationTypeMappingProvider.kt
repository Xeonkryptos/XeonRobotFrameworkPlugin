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
        SectionType.SETTINGS to "settings",
        SectionType.VARIABLES to "variables",
        SectionType.TEST_CASES to "test cases",
        SectionType.TASKS to "tasks",
        SectionType.KEYWORDS to "keywords",
        SectionType.COMMENTS to "comments"
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
        GlobalSettingType.LIBRARY to "library",
        GlobalSettingType.RESOURCE to "resource",
        GlobalSettingType.VARIABLES to "variables",
        GlobalSettingType.DOCUMENTATION to "documentation",
        GlobalSettingType.SUITE_SETUP to "suite setup",
        GlobalSettingType.SUITE_TEARDOWN to "suite teardown",
        GlobalSettingType.NAME to "name",
        GlobalSettingType.METADATA to "metadata",
        GlobalSettingType.TEST_TAGS to "test tags",
        GlobalSettingType.TEST_SETUP to "test setup",
        GlobalSettingType.TEST_TEARDOWN to "test teardown",
        GlobalSettingType.TEST_TIMEOUT to "test timeout",
        GlobalSettingType.TEST_TEMPLATE to "test template",
        GlobalSettingType.TASK_TAGS to "task tags",
        GlobalSettingType.TASK_SETUP to "task setup",
        GlobalSettingType.TASK_TEARDOWN to "task teardown",
        GlobalSettingType.TASK_TIMEOUT to "task timeout",
        GlobalSettingType.TASK_TEMPLATE to "task template",
        GlobalSettingType.KEYWORD_TAGS to "keyword tags",
        GlobalSettingType.DEFAULT_TAGS to "default tags"
    )

    private val localSettingTypeMappings = mapOf(
        "tags" to LocalSettingType.TAGS,
        "setup" to LocalSettingType.SETUP,
        "teardown" to LocalSettingType.TEARDOWN,
        "timeout" to LocalSettingType.TIMEOUT,
        "arguments" to LocalSettingType.ARGUMENTS
    )

    private val localSettingTypeMappingsReversal: Map<LocalSettingType, CharSequence> = mapOf(
        LocalSettingType.TAGS to "tags",
        LocalSettingType.SETUP to "setup",
        LocalSettingType.TEARDOWN to "teardown",
        LocalSettingType.TIMEOUT to "timeout",
        LocalSettingType.ARGUMENTS to "arguments"
    )

    private val behaviourDrivenIdentifierTypeMappings = mapOf(
        "given" to BehaviourDrivenType.GIVEN,
        "when" to BehaviourDrivenType.WHEN,
        "then" to BehaviourDrivenType.THEN,
        "and" to BehaviourDrivenType.AND,
        "but" to BehaviourDrivenType.BUT
    )

    private val behaviourDrivenIdentifierTypeMappingsReversal: Map<BehaviourDrivenType, CharSequence> = mapOf(
        BehaviourDrivenType.GIVEN to "given",
        BehaviourDrivenType.WHEN to "when",
        BehaviourDrivenType.THEN to "then",
        BehaviourDrivenType.AND to "and",
        BehaviourDrivenType.BUT to "but"
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
