package dev.xeonkryptos.xeonrobotframeworkplugin.localization

import com.intellij.openapi.project.Project
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.platform.ide.progress.ModalTaskOwner.project
import com.jetbrains.python.psi.PyClass
import com.jetbrains.python.psi.PyListLiteralExpression
import com.jetbrains.python.psi.PyStringLiteralExpression
import dev.xeonkryptos.xeonrobotframeworkplugin.config.RobotOptionsProvider
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.PythonResolver
import kotlin.jvm.java

@Service(Service.Level.PROJECT)
class LocalizationLoadingMechanism(private val project: Project) {

    private val optionsProvider = RobotOptionsProvider.getInstance(project)

    companion object {
        fun getInstance(project: Project): LocalizationLoadingMechanism = project.service<LocalizationLoadingMechanism>()
    }

    fun loadLocalizationTypeMappingProvider(): LocalizationTypeMappingProvider {
        val providers = optionsProvider.enabledLanguages.mapNotNull { loadLocalizationFor(it) }
        return providers.foldRight<LocalizationTypeMappingProvider, LocalizationTypeMappingProvider>(DefaultLocalizationTypeMappingProvider) { provider, fallback ->
            FallbackLocalizationTypeMappingProvider(provider, fallback)
        }
    }

    private fun loadLocalizationFor(className: String): LocalizationTypeMappingProvider? {
        @Suppress("UnstableApiUsage")
        fun <T> assignInstanceAttribute(attributeName: String, targetType: T, pyClass: PyClass, targetMap: MutableMap<CharSequence, T>) {
            pyClass.findInstanceAttribute(attributeName, false)?.findAssignedValue()?.let { assignedValue ->
                if (assignedValue is PyStringLiteralExpression) {
                    targetMap[assignedValue.stringValue] = targetType
                } else if (assignedValue is PyListLiteralExpression) {
                    assignedValue.elements.forEach { element ->
                        if (element is PyStringLiteralExpression) {
                            targetMap[element.stringValue] = targetType
                        }
                    }
                }
            }
        }

        return PythonResolver.findClass(className, project, null)?.let { pyClass ->
            val sectionTypeMappings = mutableMapOf<CharSequence, SectionType>()
            val globalSettingTypeMappings = mutableMapOf<CharSequence, GlobalSettingType>()
            val localSettingTypeMappings = mutableMapOf<CharSequence, LocalSettingType>()
            val behaviourDrivenIdentifierTypeMappings = mutableMapOf<CharSequence, BehaviourDrivenType>()

            assignInstanceAttribute("settings_header", SectionType.SETTINGS, pyClass, sectionTypeMappings)
            assignInstanceAttribute("variables_header", SectionType.VARIABLES, pyClass, sectionTypeMappings)
            assignInstanceAttribute("test_cases_header", SectionType.TEST_CASES, pyClass, sectionTypeMappings)
            assignInstanceAttribute("tasks_header", SectionType.TASKS, pyClass, sectionTypeMappings)
            assignInstanceAttribute("keywords_header", SectionType.KEYWORDS, pyClass, sectionTypeMappings)
            assignInstanceAttribute("comments_header", SectionType.COMMENTS, pyClass, sectionTypeMappings)

            assignInstanceAttribute("library_setting", GlobalSettingType.LIBRARY, pyClass, globalSettingTypeMappings)
            assignInstanceAttribute("resource_setting", GlobalSettingType.RESOURCE, pyClass, globalSettingTypeMappings)
            assignInstanceAttribute("variables_setting", GlobalSettingType.VARIABLES, pyClass, globalSettingTypeMappings)
            assignInstanceAttribute("name_setting", GlobalSettingType.NAME, pyClass, globalSettingTypeMappings)
            assignInstanceAttribute("documentation_setting", GlobalSettingType.DOCUMENTATION, pyClass, globalSettingTypeMappings)
            assignInstanceAttribute("metadata_setting", GlobalSettingType.METADATA, pyClass, globalSettingTypeMappings)
            assignInstanceAttribute("suite_setup_setting", GlobalSettingType.SUITE_SETUP, pyClass, globalSettingTypeMappings)
            assignInstanceAttribute("suite_teardown_setting", GlobalSettingType.SUITE_TEARDOWN, pyClass, globalSettingTypeMappings)
            assignInstanceAttribute("test_setup_setting", GlobalSettingType.TEST_SETUP, pyClass, globalSettingTypeMappings)
            assignInstanceAttribute("task_setup_setting", GlobalSettingType.TASK_SETUP, pyClass, globalSettingTypeMappings)
            assignInstanceAttribute("test_teardown_setting", GlobalSettingType.TEST_TEARDOWN, pyClass, globalSettingTypeMappings)
            assignInstanceAttribute("task_teardown_setting", GlobalSettingType.TASK_TEARDOWN, pyClass, globalSettingTypeMappings)
            assignInstanceAttribute("test_template_setting", GlobalSettingType.TEST_TEMPLATE, pyClass, globalSettingTypeMappings)
            assignInstanceAttribute("task_template_setting", GlobalSettingType.TASK_TEMPLATE, pyClass, globalSettingTypeMappings)
            assignInstanceAttribute("test_timeout_setting", GlobalSettingType.TEST_TIMEOUT, pyClass, globalSettingTypeMappings)
            assignInstanceAttribute("task_timeout_setting", GlobalSettingType.TASK_TIMEOUT, pyClass, globalSettingTypeMappings)
            assignInstanceAttribute("test_tags_setting", GlobalSettingType.TEST_TAGS, pyClass, globalSettingTypeMappings)
            assignInstanceAttribute("task_tags_setting", GlobalSettingType.TASK_TAGS, pyClass, globalSettingTypeMappings)
            assignInstanceAttribute("keyword_tags_setting", GlobalSettingType.KEYWORD_TAGS, pyClass, globalSettingTypeMappings)
            assignInstanceAttribute("default_tags_setting", GlobalSettingType.DEFAULT_TAGS, pyClass, globalSettingTypeMappings)

            assignInstanceAttribute("setup_setting", LocalSettingType.SETUP, pyClass, localSettingTypeMappings)
            assignInstanceAttribute("teardown_setting", LocalSettingType.TEARDOWN, pyClass, localSettingTypeMappings)
            assignInstanceAttribute("template_setting", LocalSettingType.TEMPLATE, pyClass, localSettingTypeMappings)
            assignInstanceAttribute("tags_setting", LocalSettingType.TAGS, pyClass, localSettingTypeMappings)
            assignInstanceAttribute("timeout_setting", LocalSettingType.TIMEOUT, pyClass, localSettingTypeMappings)
            assignInstanceAttribute("arguments_setting", LocalSettingType.ARGUMENTS, pyClass, localSettingTypeMappings)

            assignInstanceAttribute("given_prefixes", BehaviourDrivenType.GIVEN, pyClass, behaviourDrivenIdentifierTypeMappings)
            assignInstanceAttribute("when_prefixes", BehaviourDrivenType.WHEN, pyClass, behaviourDrivenIdentifierTypeMappings)
            assignInstanceAttribute("then_prefixes", BehaviourDrivenType.THEN, pyClass, behaviourDrivenIdentifierTypeMappings)
            assignInstanceAttribute("and_prefixes", BehaviourDrivenType.AND, pyClass, behaviourDrivenIdentifierTypeMappings)
            assignInstanceAttribute("but_prefixes", BehaviourDrivenType.BUT, pyClass, behaviourDrivenIdentifierTypeMappings)

            return@let object : LocalizationTypeMappingProvider {
                override fun getSectionTypeMapping(sectionName: CharSequence): SectionType? = sectionTypeMappings[sectionName]

                override fun getGlobalSettingTypeMapping(globalSettingName: CharSequence): GlobalSettingType? = globalSettingTypeMappings[globalSettingName]

                override fun getLocalSettingTypeMapping(localSettingName: CharSequence): LocalSettingType? = localSettingTypeMappings[localSettingName]

                override fun getBehaviourDrivenIdentifierTypeMapping(behaviourDrivenName: CharSequence): BehaviourDrivenType? = behaviourDrivenIdentifierTypeMappings[behaviourDrivenName]
            }
        }
    }
}
