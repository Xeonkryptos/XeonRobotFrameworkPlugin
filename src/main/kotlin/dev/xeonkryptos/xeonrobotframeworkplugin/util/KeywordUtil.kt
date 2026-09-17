package dev.xeonkryptos.xeonrobotframeworkplugin.util

import com.intellij.application.options.CodeStyle
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import dev.xeonkryptos.xeonrobotframeworkplugin.formatter.RobotCodeStyleSettings
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSettingsSection
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateStatementsGlobalSetting
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import org.apache.commons.text.WordUtils

fun RobotKeywordCall.matchesNormalizedName(normalizedName: String): Boolean = KeywordUtil.normalizeKeywordName(this.keywordCallName.keywordName.text) == normalizedName

@Service(Service.Level.APP)
class KeywordUtil {

    companion object {

        const val SPACE = " "
        const val UNDERSCORE = "_"

        @JvmStatic
        fun getInstance(): KeywordUtil = ApplicationManager.getApplication().service<KeywordUtil>()

        @JvmStatic
        fun normalizeKeywordName(name: String): String = RobotUtil.normalizeRobotIdentifier(name) ?: ""

        @JvmStatic
        fun findTemplateKeywordCall(element: PsiElement): RobotKeywordCall? {
            val robotFile = element.containingFile
            if (robotFile !is RobotFile) return null

            val testCaseStatement = PsiTreeUtil.getParentOfType(element, RobotTestCaseStatement::class.java)
            if (testCaseStatement != null) {
                var referencedKeywordCall: RobotKeywordCall? = null
                val visitor = object : RobotVisitor() {
                    override fun visitLocalSetting(o: RobotLocalSetting) = o.acceptChildren(this)
                    override fun visitPositionalArgument(o: RobotPositionalArgument) = o.acceptChildren(this)

                    override fun visitKeywordCall(o: RobotKeywordCall) {
                        referencedKeywordCall = o
                    }
                }
                testCaseStatement.acceptChildren(visitor)
                if (referencedKeywordCall != null) return referencedKeywordCall
            }
            val testTemplateFinder = TestTemplateFinder()
            robotFile.acceptChildren(testTemplateFinder)
            return testTemplateFinder.templateKeywordCall
        }
    }

    fun functionToKeyword(function: String, file: PsiFile): String {
        var keyword = function.replace(UNDERSCORE.toRegex(), SPACE).trim { it <= ' ' }

        val capitalizeKeywords = CodeStyle.getCustomSettings(file, RobotCodeStyleSettings::class.java).CAPITALIZE_KEYWORDS
        if (capitalizeKeywords && keyword != function) {
            keyword = WordUtils.capitalize(keyword)
        }
        return keyword
    }

    private class TestTemplateFinder : RobotVisitor() {
        var templateKeywordCall: RobotKeywordCall? = null

        override fun visitRoot(o: RobotRoot) {
            super.visitRoot(o)
            o.acceptChildren(this)
        }

        override fun visitSettingsSection(o: RobotSettingsSection) {
            super.visitSettingsSection(o)
            o.acceptChildren(this)
        }

        override fun visitTemplateStatementsGlobalSetting(o: RobotTemplateStatementsGlobalSetting) {
            super.visitTemplateStatementsGlobalSetting(o)
            templateKeywordCall = o.getKeywordCall()
        }
    }
}
