package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.formatting.SpacingBuilder
import com.intellij.psi.codeStyle.CommonCodeStyleSettings

data class RobotBlockContext(val commonCodeStyleSettings: CommonCodeStyleSettings, val robotCodeStyleSettings: RobotCodeStyleSettings, val spacingBuilder: SpacingBuilder)
