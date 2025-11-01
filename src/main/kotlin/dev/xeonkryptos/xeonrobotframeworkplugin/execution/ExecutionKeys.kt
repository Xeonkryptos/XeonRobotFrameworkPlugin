package dev.xeonkryptos.xeonrobotframeworkplugin.execution

import com.intellij.openapi.util.Key

object ExecutionKeys {

    @JvmField
    val TESTS_ONLY_MODE_KEY: Key<Boolean> = Key.create("ROBOT_TESTS_ONLY_MODE")
}
