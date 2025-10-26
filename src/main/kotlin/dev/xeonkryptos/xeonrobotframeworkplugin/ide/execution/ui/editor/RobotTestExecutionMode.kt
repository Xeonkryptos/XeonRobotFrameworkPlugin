package dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.ui.editor

enum class RobotTestExecutionMode(val unitExecutionMode: Boolean) {
    TEST_CASES(true), TASKS(true), DIRECTORIES(false)
}
