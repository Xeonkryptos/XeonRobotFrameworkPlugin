package dev.xeonkryptos.xeonrobotframeworkplugin.localization

import com.intellij.openapi.project.Project
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import dev.xeonkryptos.xeonrobotframeworkplugin.config.RobotOptionsProvider
import kotlin.jvm.java

@Service(Service.Level.PROJECT)
class LocalizationLoadingMechanism(project: Project) {

    private val optionsProvider = RobotOptionsProvider.getInstance(project)

    companion object {
        fun getInstance(project: Project): LocalizationLoadingMechanism {
            return project.service<LocalizationLoadingMechanism>()
        }
    }

    fun loadLocalizationTypeMappingProvider(): LocalizationTypeMappingProvider {
        // TODO: Load mappings provided by optionsProvider and construct a chain of decorated FallbackLocalizationTypeMappingProvider
        return FallbackLocalizationTypeMappingProvider(DefaultLocalizationTypeMappingProvider, DefaultLocalizationTypeMappingProvider)
    }
}
