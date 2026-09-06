package dev.xeonkryptos.xeonrobotframeworkplugin.localization

interface LocalizationTypeMappingProvider {

    fun getSectionType(sectionName: CharSequence): SectionType?

    fun getSectionName(sectionType: SectionType): List<CharSequence>?

    fun getGlobalSettingType(globalSettingName: CharSequence): GlobalSettingType?

    fun getGlobalSettingName(globalSettingType: GlobalSettingType): List<CharSequence>?

    fun getLocalSettingType(localSettingName: CharSequence): LocalSettingType?

    fun getLocalSettingName(localSettingType: LocalSettingType): List<CharSequence>?

    fun getBehaviourDrivenIdentifierType(behaviourDrivenName: CharSequence): BehaviourDrivenType?

    fun getBehaviourDrivenIdentifierName(behaviourDrivenType: BehaviourDrivenType): List<CharSequence>?
}

class FallbackLocalizationTypeMappingProvider(private val mappingProvider: LocalizationTypeMappingProvider, private val fallbackMappingProvider: LocalizationTypeMappingProvider) :
    LocalizationTypeMappingProvider {

    override fun getSectionType(sectionName: CharSequence): SectionType? = mappingProvider.getSectionType(sectionName) ?: fallbackMappingProvider.getSectionType(sectionName)

    override fun getSectionName(sectionType: SectionType): List<CharSequence> = mutableListOf<CharSequence>().also { sectionNames ->
        mappingProvider.getSectionName(sectionType)?.let { sectionNames.addAll(it) }
        fallbackMappingProvider.getSectionName(sectionType)?.let { sectionNames.addAll(it) }
    }

    override fun getGlobalSettingType(globalSettingName: CharSequence): GlobalSettingType? =
        mappingProvider.getGlobalSettingType(globalSettingName) ?: fallbackMappingProvider.getGlobalSettingType(globalSettingName)

    override fun getGlobalSettingName(globalSettingType: GlobalSettingType): List<CharSequence> = mutableListOf<CharSequence>().also { globalSettingNames ->
        mappingProvider.getGlobalSettingName(globalSettingType)?.let { globalSettingNames.addAll(it) }
        fallbackMappingProvider.getGlobalSettingName(globalSettingType)?.let { globalSettingNames.addAll(it) }
    }

    override fun getLocalSettingType(localSettingName: CharSequence): LocalSettingType? =
        mappingProvider.getLocalSettingType(localSettingName) ?: fallbackMappingProvider.getLocalSettingType(localSettingName)

    override fun getLocalSettingName(localSettingType: LocalSettingType): List<CharSequence> =
        mutableListOf<CharSequence>().also { localSettingNames ->
            mappingProvider.getLocalSettingName(localSettingType)?.let { localSettingNames.addAll(it) }
            fallbackMappingProvider.getLocalSettingName(localSettingType)?.let { localSettingNames.addAll(it) }
        }

    override fun getBehaviourDrivenIdentifierType(behaviourDrivenName: CharSequence): BehaviourDrivenType? =
        mappingProvider.getBehaviourDrivenIdentifierType(behaviourDrivenName) ?: fallbackMappingProvider.getBehaviourDrivenIdentifierType(behaviourDrivenName)

    override fun getBehaviourDrivenIdentifierName(behaviourDrivenType: BehaviourDrivenType): List<CharSequence> =
        mutableListOf<CharSequence>().also { behaviourDrivenNames ->
            mappingProvider.getBehaviourDrivenIdentifierName(behaviourDrivenType)?.let { behaviourDrivenNames.addAll(it) }
            fallbackMappingProvider.getBehaviourDrivenIdentifierName(behaviourDrivenType)?.let { behaviourDrivenNames.addAll(it) }
        }
}
