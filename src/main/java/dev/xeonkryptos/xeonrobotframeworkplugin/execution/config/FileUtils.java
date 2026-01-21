package dev.xeonkryptos.xeonrobotframeworkplugin.execution.config;

import com.jetbrains.python.run.PythonScriptCommandLineState;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

final class FileUtils {

    private FileUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String getWorkingDirectoryToUse(@NotNull RobotRunConfiguration runConfig) {
        return PythonScriptCommandLineState.getExpandedWorkingDir(runConfig.getPythonRunConfiguration());
    }

    @NotNull
    public static String relativizePath(String basePath, String targetPath) {
        Path targetFile = Path.of(targetPath);
        try {
            Path relativePath = Path.of(basePath).relativize(targetFile);
            return relativePath.toString();
        } catch (IllegalArgumentException e) {
            return targetPath;
        }
    }
}
