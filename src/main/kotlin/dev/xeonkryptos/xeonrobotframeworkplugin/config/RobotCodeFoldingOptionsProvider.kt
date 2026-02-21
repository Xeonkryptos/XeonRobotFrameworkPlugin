package dev.xeonkryptos.xeonrobotframeworkplugin.config

import com.intellij.application.options.editor.CodeFoldingOptionsProvider
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.bindIntText
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.columns
import com.intellij.ui.dsl.builder.panel
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle

class RobotCodeFoldingOptionsProvider : BoundConfigurable(RobotBundle.message("options.entrypoint")), CodeFoldingOptionsProvider {

    private val instance = RobotFoldingSettings.getInstance().state

    override fun createPanel(): DialogPanel = panel {
        group(displayName) {
            row {
                checkBox(RobotBundle.message("options.code.folding.collapse.section.settings")).bindSelected(instance::collapseSettingsSection)
            }
            row {
                checkBox(RobotBundle.message("options.code.folding.collapse.section.variables")).bindSelected(instance::collapseVariablesSection)
            }
            row {
                checkBox(RobotBundle.message("options.code.folding.collapse.section.testcases")).bindSelected(instance::collapseTestCasesSection)
            }
            row {
                checkBox(RobotBundle.message("options.code.folding.collapse.section.tasks")).bindSelected(instance::collapseTasksSection)
            }
            row {
                checkBox(RobotBundle.message("options.code.folding.collapse.section.keywords")).bindSelected(instance::collapseKeywordsSection)
            }
            row {
                checkBox(RobotBundle.message("options.code.folding.collapse.section.comments")).bindSelected(instance::collapseCommentSection)
            }

            row {
                checkBox(RobotBundle.message("options.code.folding.collapse.variables")).bindSelected(instance::collapseVariables)
            }
            row {
                checkBox(RobotBundle.message("options.code.folding.collapse.variables.with.variable.names")).bindSelected(instance::showVariableNamesInFolding)
            }
            row(RobotBundle.message("options.code.folding.collapse.variables.max.placeholder.length")) {
                intTextField(0..1000).bindIntText(instance::maxVariablePlaceholderValueLength).columns(4)
            }
            row {
                checkBox(RobotBundle.message("options.code.folding.collapse.to.single.line")).bindSelected(instance::collapseToSingleLine)
            }
            row(RobotBundle.message("options.code.folding.collapse.list.max.placeholder.length")) {
                intTextField(0..1000).bindIntText(instance::maxListPlaceholderValueLength).columns(4)
            }
        }
    }
}
