package dev.xeonkryptos.xeonrobotframeworkplugin.config

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

@State(
    name = "RobotFrameworkFoldingSettings", storages = [Storage("robotFrameworkFoldingSettings.xml")]
)
@Service(Service.Level.APP)
class RobotFoldingSettings : PersistentStateComponent<RobotFoldingSettings.State> {

    data class State(
        var collapseSettingsSection: Boolean = false,
        var collapseCommentSection: Boolean = true,
        var collapseTestCasesSection: Boolean = false,
        var collapseTasksSection: Boolean = false,
        var collapseKeywordsSection: Boolean = false,
        var collapseVariablesSection: Boolean = false,
        var collapseVariables: Boolean = true,
        var showVariableNamesInFolding: Boolean = false,
        var maxVariablePlaceholderValueLength: Int = 50,
        var collapseToSingleLine: Boolean = false,
        var maxListPlaceholderValueLength: Int = 100
    )

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    companion object {
        @JvmStatic
        fun getInstance(): RobotFoldingSettings = service()
    }
}
