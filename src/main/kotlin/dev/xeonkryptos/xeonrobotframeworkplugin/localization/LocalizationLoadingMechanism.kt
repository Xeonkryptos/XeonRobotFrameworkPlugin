package dev.xeonkryptos.xeonrobotframeworkplugin.localization

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.jetbrains.python.psi.PyClass
import com.jetbrains.python.psi.PyListLiteralExpression
import com.jetbrains.python.psi.PyStringLiteralExpression
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.PythonResolver

@Service(Service.Level.PROJECT)
class LocalizationLoadingMechanism(private val project: Project) : Disposable {

    companion object {
        @JvmStatic
        fun getInstance(project: Project): LocalizationLoadingMechanism = project.service<LocalizationLoadingMechanism>()
    }

    @Volatile
    var localizationTypeMappingProvider: LocalizationTypeMappingProvider = DefaultLocalizationTypeMappingProvider
        private set

    init {
        val messageBus = project.messageBus
        messageBus.connect(this).subscribe(LocalizationTopics.LOCALIZATION_CHANGED_TOPIC, object : LocalizationConfigurationChangedListener {
            override fun onLocalizationChanged(languageClassReferences: Collection<String>) {
                val localizationTypeMappingProvider = loadLocalizationTypeMappingProvider(languageClassReferences)
                this@LocalizationLoadingMechanism.localizationTypeMappingProvider = localizationTypeMappingProvider
                messageBus.syncPublisher(LocalizationTopics.LOCALIZATION_LOADED_TOPIC).onLocalizationTypeMappingChanged(localizationTypeMappingProvider)
            }
        })
    }

    fun loadLocalizationTypeMappingProvider(enabledLanguageClassReferences: Collection<String>): LocalizationTypeMappingProvider {
        val providers = enabledLanguageClassReferences.mapNotNull { loadLocalizationFor(it) }
        return providers.foldRight<LocalizationTypeMappingProvider, LocalizationTypeMappingProvider>(DefaultLocalizationTypeMappingProvider) { provider, fallback ->
            FallbackLocalizationTypeMappingProvider(provider, fallback)
        }
    }

    private fun loadLocalizationFor(className: String): LocalizationTypeMappingProvider? {
        @Suppress("UnstableApiUsage")
        fun <T> assignInstanceAttribute(attributeName: String, targetType: T, pyClass: PyClass, targetMap: MutableMap<CharSequence, T>, reversalMap: MutableMap<T, MutableList<CharSequence>>) {
            pyClass.findInstanceAttribute(attributeName, false)?.findAssignedValue()?.let { assignedValue ->
                if (assignedValue is PyStringLiteralExpression) {
                    targetMap[assignedValue.stringValue] = targetType
                    reversalMap.computeIfAbsent(targetType) { mutableListOf() }.add(assignedValue.stringValue)
                } else if (assignedValue is PyListLiteralExpression) {
                    assignedValue.elements.forEach { element ->
                        if (element is PyStringLiteralExpression) {
                            targetMap[element.stringValue] = targetType
                            reversalMap.computeIfAbsent(targetType) { mutableListOf() }.add(element.stringValue)
                        }
                    }
                }
            }
        }

        return PythonResolver.findClass(className, project, null)?.let { pyClass ->
            val sectionTypeMappings = mutableMapOf<CharSequence, SectionType>()
            val sectionTypeMappingsReversal = mutableMapOf<SectionType, MutableList<CharSequence>>()
            val globalSettingTypeMappings = mutableMapOf<CharSequence, GlobalSettingType>()
            val globalSettingTypeMappingsReversal = mutableMapOf<GlobalSettingType, MutableList<CharSequence>>()
            val localSettingTypeMappings = mutableMapOf<CharSequence, LocalSettingType>()
            val localSettingTypeMappingsReversal = mutableMapOf<LocalSettingType, MutableList<CharSequence>>()
            val behaviourDrivenIdentifierTypeMappings = mutableMapOf<CharSequence, BehaviourDrivenType>()
            val behaviourDrivenIdentifierTypeMappingsReversal = mutableMapOf<BehaviourDrivenType, MutableList<CharSequence>>()

            assignInstanceAttribute("settings_header", SectionType.SETTINGS, pyClass, sectionTypeMappings, sectionTypeMappingsReversal)
            assignInstanceAttribute("variables_header", SectionType.VARIABLES, pyClass, sectionTypeMappings, sectionTypeMappingsReversal)
            assignInstanceAttribute("test_cases_header", SectionType.TEST_CASES, pyClass, sectionTypeMappings, sectionTypeMappingsReversal)
            assignInstanceAttribute("tasks_header", SectionType.TASKS, pyClass, sectionTypeMappings, sectionTypeMappingsReversal)
            assignInstanceAttribute("keywords_header", SectionType.KEYWORDS, pyClass, sectionTypeMappings, sectionTypeMappingsReversal)
            assignInstanceAttribute("comments_header", SectionType.COMMENTS, pyClass, sectionTypeMappings, sectionTypeMappingsReversal)

            assignInstanceAttribute("library_setting", GlobalSettingType.LIBRARY, pyClass, globalSettingTypeMappings, globalSettingTypeMappingsReversal)
            assignInstanceAttribute("resource_setting", GlobalSettingType.RESOURCE, pyClass, globalSettingTypeMappings, globalSettingTypeMappingsReversal)
            assignInstanceAttribute("variables_setting", GlobalSettingType.VARIABLES, pyClass, globalSettingTypeMappings, globalSettingTypeMappingsReversal)
            assignInstanceAttribute("name_setting", GlobalSettingType.NAME, pyClass, globalSettingTypeMappings, globalSettingTypeMappingsReversal)
            assignInstanceAttribute("documentation_setting", GlobalSettingType.DOCUMENTATION, pyClass, globalSettingTypeMappings, globalSettingTypeMappingsReversal)
            assignInstanceAttribute("metadata_setting", GlobalSettingType.METADATA, pyClass, globalSettingTypeMappings, globalSettingTypeMappingsReversal)
            assignInstanceAttribute("suite_setup_setting", GlobalSettingType.SUITE_SETUP, pyClass, globalSettingTypeMappings, globalSettingTypeMappingsReversal)
            assignInstanceAttribute("suite_teardown_setting", GlobalSettingType.SUITE_TEARDOWN, pyClass, globalSettingTypeMappings, globalSettingTypeMappingsReversal)
            assignInstanceAttribute("test_setup_setting", GlobalSettingType.TEST_SETUP, pyClass, globalSettingTypeMappings, globalSettingTypeMappingsReversal)
            assignInstanceAttribute("task_setup_setting", GlobalSettingType.TASK_SETUP, pyClass, globalSettingTypeMappings, globalSettingTypeMappingsReversal)
            assignInstanceAttribute("test_teardown_setting", GlobalSettingType.TEST_TEARDOWN, pyClass, globalSettingTypeMappings, globalSettingTypeMappingsReversal)
            assignInstanceAttribute("task_teardown_setting", GlobalSettingType.TASK_TEARDOWN, pyClass, globalSettingTypeMappings, globalSettingTypeMappingsReversal)
            assignInstanceAttribute("test_template_setting", GlobalSettingType.TEST_TEMPLATE, pyClass, globalSettingTypeMappings, globalSettingTypeMappingsReversal)
            assignInstanceAttribute("task_template_setting", GlobalSettingType.TASK_TEMPLATE, pyClass, globalSettingTypeMappings, globalSettingTypeMappingsReversal)
            assignInstanceAttribute("test_timeout_setting", GlobalSettingType.TEST_TIMEOUT, pyClass, globalSettingTypeMappings, globalSettingTypeMappingsReversal)
            assignInstanceAttribute("task_timeout_setting", GlobalSettingType.TASK_TIMEOUT, pyClass, globalSettingTypeMappings, globalSettingTypeMappingsReversal)
            assignInstanceAttribute("test_tags_setting", GlobalSettingType.TEST_TAGS, pyClass, globalSettingTypeMappings, globalSettingTypeMappingsReversal)
            assignInstanceAttribute("task_tags_setting", GlobalSettingType.TASK_TAGS, pyClass, globalSettingTypeMappings, globalSettingTypeMappingsReversal)
            assignInstanceAttribute("keyword_tags_setting", GlobalSettingType.KEYWORD_TAGS, pyClass, globalSettingTypeMappings, globalSettingTypeMappingsReversal)
            assignInstanceAttribute("default_tags_setting", GlobalSettingType.DEFAULT_TAGS, pyClass, globalSettingTypeMappings, globalSettingTypeMappingsReversal)

            assignInstanceAttribute("setup_setting", LocalSettingType.SETUP, pyClass, localSettingTypeMappings, localSettingTypeMappingsReversal)
            assignInstanceAttribute("teardown_setting", LocalSettingType.TEARDOWN, pyClass, localSettingTypeMappings, localSettingTypeMappingsReversal)
            assignInstanceAttribute("template_setting", LocalSettingType.TEMPLATE, pyClass, localSettingTypeMappings, localSettingTypeMappingsReversal)
            assignInstanceAttribute("tags_setting", LocalSettingType.TAGS, pyClass, localSettingTypeMappings, localSettingTypeMappingsReversal)
            assignInstanceAttribute("timeout_setting", LocalSettingType.TIMEOUT, pyClass, localSettingTypeMappings, localSettingTypeMappingsReversal)
            assignInstanceAttribute("arguments_setting", LocalSettingType.ARGUMENTS, pyClass, localSettingTypeMappings, localSettingTypeMappingsReversal)

            assignInstanceAttribute("given_prefixes", BehaviourDrivenType.GIVEN, pyClass, behaviourDrivenIdentifierTypeMappings, behaviourDrivenIdentifierTypeMappingsReversal)
            assignInstanceAttribute("when_prefixes", BehaviourDrivenType.WHEN, pyClass, behaviourDrivenIdentifierTypeMappings, behaviourDrivenIdentifierTypeMappingsReversal)
            assignInstanceAttribute("then_prefixes", BehaviourDrivenType.THEN, pyClass, behaviourDrivenIdentifierTypeMappings, behaviourDrivenIdentifierTypeMappingsReversal)
            assignInstanceAttribute("and_prefixes", BehaviourDrivenType.AND, pyClass, behaviourDrivenIdentifierTypeMappings, behaviourDrivenIdentifierTypeMappingsReversal)
            assignInstanceAttribute("but_prefixes", BehaviourDrivenType.BUT, pyClass, behaviourDrivenIdentifierTypeMappings, behaviourDrivenIdentifierTypeMappingsReversal)

            return@let object : LocalizationTypeMappingProvider {
                override fun getSectionType(sectionName: CharSequence): SectionType? = sectionTypeMappings[sectionName]

                override fun getSectionName(sectionType: SectionType): List<CharSequence>? = sectionTypeMappingsReversal[sectionType]

                override fun getGlobalSettingType(globalSettingName: CharSequence): GlobalSettingType? = globalSettingTypeMappings[globalSettingName]

                override fun getGlobalSettingName(globalSettingType: GlobalSettingType): List<CharSequence>? = globalSettingTypeMappingsReversal[globalSettingType]

                override fun getLocalSettingType(localSettingName: CharSequence): LocalSettingType? = localSettingTypeMappings[localSettingName]

                override fun getLocalSettingName(localSettingType: LocalSettingType): List<CharSequence>? = localSettingTypeMappingsReversal[localSettingType]

                override fun getBehaviourDrivenIdentifierType(behaviourDrivenName: CharSequence): BehaviourDrivenType? = behaviourDrivenIdentifierTypeMappings[behaviourDrivenName]

                override fun getBehaviourDrivenIdentifierName(behaviourDrivenType: BehaviourDrivenType): List<CharSequence>? = behaviourDrivenIdentifierTypeMappingsReversal[behaviourDrivenType]
            }
        }
    }

    override fun dispose() {}
}
