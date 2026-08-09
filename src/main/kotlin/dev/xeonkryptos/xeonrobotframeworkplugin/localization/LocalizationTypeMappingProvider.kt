package dev.xeonkryptos.xeonrobotframeworkplugin.localization

interface LocalizationTypeMappingProvider {

    fun getSectionTypeMapping(sectionName: CharSequence): SectionType?

    fun getGlobalSettingTypeMapping(globalSettingName: CharSequence): GlobalSettingType?

    fun getLocalSettingTypeMapping(localSettingName: CharSequence): LocalSettingType?

    fun getBehaviourDrivenIdentifierTypeMapping(behaviourDrivenName: CharSequence): BehaviourDrivenType?
}

class FallbackLocalizationTypeMappingProvider(private val mappingProvider: LocalizationTypeMappingProvider, private val fallbackMappingProvider: LocalizationTypeMappingProvider) :
    LocalizationTypeMappingProvider {

    override fun getSectionTypeMapping(sectionName: CharSequence): SectionType? = mappingProvider.getSectionTypeMapping(sectionName) ?: fallbackMappingProvider.getSectionTypeMapping(sectionName)

    override fun getGlobalSettingTypeMapping(globalSettingName: CharSequence): GlobalSettingType? =
        mappingProvider.getGlobalSettingTypeMapping(globalSettingName) ?: fallbackMappingProvider.getGlobalSettingTypeMapping(globalSettingName)

    override fun getLocalSettingTypeMapping(localSettingName: CharSequence): LocalSettingType? =
        mappingProvider.getLocalSettingTypeMapping(localSettingName) ?: fallbackMappingProvider.getLocalSettingTypeMapping(localSettingName)

    override fun getBehaviourDrivenIdentifierTypeMapping(behaviourDrivenName: CharSequence): BehaviourDrivenType? =
        mappingProvider.getBehaviourDrivenIdentifierTypeMapping(behaviourDrivenName) ?: fallbackMappingProvider.getBehaviourDrivenIdentifierTypeMapping(behaviourDrivenName)
}
