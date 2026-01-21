package dev.xeonkryptos.xeonrobotframeworkplugin.util

import com.intellij.openapi.application.PathManager
import java.nio.file.Path

object BundleUtil {

    private val DATA_DIR: Path = PathManager.getPluginsDir().resolve("Xeon RobotFramework Support").resolve("data")
    private val BUNDLED_DIR: Path = DATA_DIR.resolve("bundled")
    private val TOOL_DIR: Path = BUNDLED_DIR.resolve("tool")

    @JvmField
    val ROBOTCODE_DIR: Path = TOOL_DIR.resolve("robotcode")
}
