package dev.xeonkryptos.xeonrobotframeworkplugin.execution.ui.editor

enum class RobotTestExecutionMode(val unitExecutionMode: Boolean) {
    TEST_CASES(true), TASKS(true), DIRECTORIES(false)
}
