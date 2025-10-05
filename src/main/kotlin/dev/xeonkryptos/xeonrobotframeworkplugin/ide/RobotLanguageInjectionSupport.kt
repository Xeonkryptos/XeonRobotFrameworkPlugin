package dev.xeonkryptos.xeonrobotframeworkplugin.ide

import com.intellij.psi.PsiLanguageInjectionHost
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.patterns.RobotPatterns
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotElement
import org.intellij.plugins.intelliLang.inject.AbstractLanguageInjectionSupport

class RobotLanguageInjectionSupport : AbstractLanguageInjectionSupport() {

    override fun getId(): String = "Robot"

    override fun getPatternClasses(): Array<out Class<*>?> = arrayOf(RobotPatterns::class.java)

    override fun isApplicableTo(host: PsiLanguageInjectionHost?): Boolean = host is RobotElement

    override fun useDefaultInjector(host: PsiLanguageInjectionHost?): Boolean = true

    override fun getHelpId(): String = "reference.settings.language.injection.generic.robot"
}
