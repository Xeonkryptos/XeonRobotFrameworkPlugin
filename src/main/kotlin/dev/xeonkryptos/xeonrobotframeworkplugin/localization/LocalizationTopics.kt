package dev.xeonkryptos.xeonrobotframeworkplugin.localization

import com.intellij.util.messages.Topic
import kotlin.jvm.java

object LocalizationTopics {

    @JvmField
    val LOCALIZATION_CHANGED_TOPIC = Topic.create("LOCALIZATION_CHANGED", LocalizationConfigurationChangedListener::class.java)

    @JvmField
    val LOCALIZATION_LOADED_TOPIC = Topic.create("LOCALIZATION_LOADED", LocalizationLoadedListener::class.java)
}

interface LocalizationConfigurationChangedListener {

    fun onLocalizationChanged(languageClassReferences: Collection<String>)
}

interface LocalizationLoadedListener {

    fun onLocalizationTypeMappingChanged(localizationTypeMappingProvider: LocalizationTypeMappingProvider)
}
