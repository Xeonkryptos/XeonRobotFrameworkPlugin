package dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.util.parentOfType
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.RobotAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.localization.GlobalSettingType
import dev.xeonkryptos.xeonrobotframeworkplugin.localization.LocalSettingType
import dev.xeonkryptos.xeonrobotframeworkplugin.localization.LocalizationLoadingMechanism
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSetupTeardownStatementsGlobalSetting
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames

class InvalidlyPlacedBuiltInKeywordCallsAnnotator : RobotAnnotator() {

    override fun visitKeywordCall(keywordCall: RobotKeywordCall) {
        val keywordCallName = keywordCall.keywordCallName.keywordName.text
        val normalizedKeywordName = KeywordUtil.normalizeKeywordName(keywordCallName)
        when (normalizedKeywordName) {
            RobotNames.RUN_KEYWORD_IF_ALL_TESTS_PASSED_NORMALIZED_KEYWORD_NAME, RobotNames.RUN_KEYWORD_IF_ANY_TESTS_FAILED_NORMALIZED_KEYWORD_NAME -> {
                val globalSetting = keywordCall.parentOfType<RobotSetupTeardownStatementsGlobalSetting>()
                val localizationTypeMappingProvider = LocalizationLoadingMechanism.getInstance(keywordCall.project).localizationTypeMappingProvider
                val misplacedKeywordCall = globalSetting?.let { localizationTypeMappingProvider.getGlobalSettingType(it.settingName) != GlobalSettingType.SUITE_TEARDOWN } ?: true
                if (misplacedKeywordCall) {
                    holder.newAnnotation(
                        HighlightSeverity.ERROR, RobotBundle.message("annotation.keyword.run-keyword-if.suite-teardown.invalid", keywordCallName)
                    ).range(keywordCall).create()
                }
            }

            RobotNames.RUN_KEYWORD_IF_TEST_FAILED_NORMALIZED_KEYWORD_NAME, RobotNames.RUN_KEYWORD_IF_TEST_PASSED_NORMALIZED_KEYWORD_NAME, RobotNames.RUN_KEYWORD_IF_TIMEOUT_OCCURRED_NORMALIZED_KEYWORD_NAME -> {
                val localizationTypeMappingProvider = LocalizationLoadingMechanism.getInstance(keywordCall.project).localizationTypeMappingProvider
                val globalSetting = keywordCall.parentOfType<RobotSetupTeardownStatementsGlobalSetting>()
                val misplacedKeywordCall: Boolean = if (globalSetting == null) {
                    keywordCall.parentOfType<RobotLocalSetting>()?.let { localizationTypeMappingProvider.getLocalSettingType(it.settingName) != LocalSettingType.TEARDOWN } ?: true
                } else {
                    localizationTypeMappingProvider.getGlobalSettingType(globalSetting.settingName) != GlobalSettingType.SUITE_TEARDOWN
                }
                if (misplacedKeywordCall) {
                    holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.keyword.run-keyword-if.test-teardown.invalid", keywordCallName)).range(keywordCall).create()
                }
            }
        }
    }
}
