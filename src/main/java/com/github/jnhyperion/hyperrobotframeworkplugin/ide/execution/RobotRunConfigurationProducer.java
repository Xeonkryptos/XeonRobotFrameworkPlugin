package com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotTokenTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Heading;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordDefinition;
import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.ConfigurationFromContext;
import com.intellij.execution.actions.LazyRunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class RobotRunConfigurationProducer extends LazyRunConfigurationProducer<RobotRunConfiguration> {

    @NotNull
    @Override
    public ConfigurationFactory getConfigurationFactory() {
        return RobotRunConfigurationType.getRobotRunConfigurationType().getConfigurationFactory();
    }

    @Override
    public boolean isPreferredConfiguration(ConfigurationFromContext context1, @NotNull ConfigurationFromContext context2) {
        return context2.isProducedBy(RobotRunConfigurationProducer.class);
    }

    @Override
    protected boolean setupConfigurationFromContext(@NotNull RobotRunConfiguration runConfig,
                                                    @NotNull ConfigurationContext context,
                                                    @NotNull Ref<PsiElement> sourceElement) {
        if (isValidRobotExecutableScript(context)) {
            String workingDirectory = getWorkingDirectoryToUse(runConfig);
            String runParam = getRunParameters(context, workingDirectory);
            runConfig.setUseModuleSdk(true);
            runConfig.setModuleMode(true);
            runConfig.setScriptName("robotcode");
            runConfig.setWorkingDirectory(context.getProject().getBasePath());
            runConfig.setScriptParameters(runParam);
            Sdk sdk = ProjectRootManager.getInstance(context.getProject()).getProjectSdk();
            if (sdk != null) {
                runConfig.setSdkHome(sdk.getHomePath());
            }
            runConfig.setName(getRunDisplayName(context));
            return true;
        }
        return false;
    }

    @Override
    public boolean isConfigurationFromContext(@NotNull RobotRunConfiguration runConfig, @NotNull ConfigurationContext context) {
        if (isValidRobotExecutableScript(context)) {
            String workingDirectory = getWorkingDirectoryToUse(runConfig);
            String runParam = getRunParameters(context, workingDirectory);
            boolean ret = runParam.trim().equals(runConfig.getScriptParameters().trim());
            if (ret) {
                runConfig.setName(getRunDisplayName(context));
            }
            return ret;
        }
        return false;
    }

    private String getWorkingDirectoryToUse(@NotNull RobotRunConfiguration runConfig) {
        String workingDirectory = runConfig.getWorkingDirectory();
        if (workingDirectory == null || workingDirectory.isEmpty()) {
            workingDirectory = runConfig.getWorkingDirectorySafe();
        }
        return workingDirectory;
    }

    private static boolean containsTasksOnly(ConfigurationContext context) {
        PsiElement element = context.getPsiLocation();
        Heading heading;
        while (true) {
            if (element == null) {
                return false;
            }
            if (element instanceof Heading) {
                heading = (Heading) element;
                break;
            }
            element = element.getParent();
        }
        return heading.containsTasks() && !heading.containsTestCases();
    }

    @NotNull
    private static String getRunParameters(ConfigurationContext context, String basePath) {
        String testCaseName = getTestCaseName(context);
        String projectBasePath = context.getProject().getBasePath();
        assert projectBasePath != null;

        Location<?> location = context.getLocation();
        assert location != null;

        VirtualFile virtualFile = location.getVirtualFile();
        assert virtualFile != null;

        testCaseName = testCaseName.replace("\"", "\\\"");
        String runParameters;
        if (!testCaseName.isEmpty()) {
            runParameters = "--test \"" + testCaseName + "\"";
        } else {
            runParameters = "--test *";
        }
        String filePath = virtualFile.getPath();
        basePath = relativizePath(basePath, filePath);
        runParameters += " \"" + basePath.replace("\"", "\\\"") + "\"";

        if (containsTasksOnly(context)) {
            runParameters = "--rpa " + runParameters;
        }
        return runParameters;
    }

    @NotNull
    private static String relativizePath(String basePath, String targetPath) {
        Path targetFile = Path.of(targetPath);
        try {
            Path relativePath = Path.of(basePath).relativize(targetFile);
            return relativePath.toString();
        } catch (IllegalArgumentException e) {
            return targetPath;
        }
    }

    @NotNull
    private static String getTestCaseOrFileName(ConfigurationContext context) {
        Location<?> location = context.getLocation();
        assert location != null;

        VirtualFile virtualFile = location.getVirtualFile();
        assert virtualFile != null;

        String testCaseName = getTestCaseName(context);
        return !testCaseName.isEmpty() ? testCaseName : virtualFile.getName();
    }

    private static boolean isValidRobotExecutableScript(@NotNull ConfigurationContext context) {
        @SuppressWarnings("rawtypes")
        Location location = context.getLocation();
        PsiElement element;
        if (location != null) {
            element = location.getPsiElement();
            if (element instanceof LeafPsiElement leafPsiElement) {
                IElementType type = leafPsiElement.getElementType();
                return RobotTokenTypes.KEYWORD_DEFINITION.equals(type) || RobotTokenTypes.HEADING.equals(type);
            }
        }
        return false;
    }

    @NotNull
    private static String getTestCaseName(@NotNull ConfigurationContext context) {
        Location<?> location = context.getLocation();
        if (location != null) {
            return getKeywordNameFromAnyElement(location.getPsiElement());
        }
        return "";
    }

    @NotNull
    private static String getKeywordNameFromAnyElement(PsiElement element) {
        while (!(element instanceof KeywordDefinition)) {
            element = element.getParent();
            if (element == null) {
                return "";
            }
        }
        return ((KeywordDefinition) element).getKeywordName();
    }

    @NotNull
    private static String getRunDisplayName(@NotNull ConfigurationContext context) {
        return getTestCaseOrFileName(context);
    }
}
