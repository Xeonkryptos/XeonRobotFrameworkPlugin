package dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.config;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public final class FileUtils {

    private FileUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String getWorkingDirectoryToUse(@NotNull RobotRunConfiguration runConfig) {
        String workingDirectory = runConfig.getPythonRunConfiguration().getWorkingDirectory();
        if (workingDirectory == null || workingDirectory.isEmpty()) {
            workingDirectory = runConfig.getPythonRunConfiguration().getWorkingDirectorySafe();
        }
        return workingDirectory;
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
