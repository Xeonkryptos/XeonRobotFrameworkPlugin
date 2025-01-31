package com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotTokenTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Heading;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordDefinitionImpl;
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

import java.io.File;

public class RobotRunConfigurationProducer extends LazyRunConfigurationProducer<RobotRunConfiguration> {

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
        if (projectBasePath == null) {
            throw new AssertionError("Project base path is null");
        }

        Location<?> location = context.getLocation();
        if (location == null) {
            throw new AssertionError("Location is null");
        }

        VirtualFile virtualFile = location.getVirtualFile();
        if (virtualFile == null) {
            throw new AssertionError("Virtual file is null");
        }

        testCaseName = testCaseName.replace("\"", "\\\"");
        if (basePath.startsWith(projectBasePath) && virtualFile.getPath().startsWith(basePath)) {
            basePath = relativizePath(basePath, virtualFile.getPath()).replace("\"", "\\\"");
            basePath = !testCaseName.isEmpty() ? "--test \"" + basePath + "." + testCaseName + "\" ." : "--suite \"" + basePath + "\" .";
        } else {
            basePath = (!testCaseName.isEmpty() ? "--test \"" + testCaseName + "\"" : "--test *") + " \"" + virtualFile.getPath().replace("\"", "\\\"") + "\"";
        }

        if (containsTasksOnly(context)) {
            basePath = "--rpa " + basePath;
        }
        return basePath;
    }

    @NotNull
    private static String relativizePath(String basePath, String targetPath) {
        String relativePath;
        targetPath = (relativePath = new File(basePath).toURI().relativize(new File(targetPath).toURI()).getPath().replace("/", ".")).substring(0,
                                                                                                                                                relativePath.lastIndexOf(
                                                                                                                                                        '.'));
        return new File(basePath).getName() + "." + targetPath;
    }

    @NotNull
    private static String getTestCaseOrFileName(ConfigurationContext context) {
        Location<?> location = context.getLocation();
        if (location == null) {
            throw new AssertionError();
        } else {
            VirtualFile virtualFile = location.getVirtualFile();
            if (virtualFile == null) {
                throw new AssertionError();
            } else {
                String testCaseName = getTestCaseName(context);
                return !testCaseName.isEmpty() ? testCaseName : virtualFile.getName();
            }
        }
    }

    private static boolean isValidRobotExecutableScript(@NotNull ConfigurationContext context) {
        @SuppressWarnings("rawtypes")
        Location location = context.getLocation();
        PsiElement element;
        if (location != null) {
            element = location.getPsiElement();
            if (element instanceof LeafPsiElement) {
                IElementType type = ((LeafPsiElement) element).getElementType();
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
        while (!(element instanceof KeywordDefinitionImpl)) {
            element = element.getParent();
            if (element == null) {
                return "";
            }
        }
        return ((KeywordDefinitionImpl) element).getKeywordName();
    }

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
            String workingDirectorySafe = runConfig.getWorkingDirectorySafe();
            String runParam = getRunParameters(context, workingDirectorySafe);
            runConfig.setUseModuleSdk(false);
            runConfig.setModuleMode(true);
            runConfig.setScriptName("robot.run");
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
            String workingDirectorySafe = runConfig.getWorkingDirectorySafe();
            String runParam = getRunParameters(context, workingDirectorySafe);
            boolean ret = runParam.trim().equals(runConfig.getScriptParameters().trim());
            if (ret) {
                runConfig.setName(getRunDisplayName(context));
            }
            return ret;
        }
        return false;
    }

    @NotNull
    private static String getRunDisplayName(@NotNull ConfigurationContext context) {
        return getTestCaseOrFileName(context);
    }
}
