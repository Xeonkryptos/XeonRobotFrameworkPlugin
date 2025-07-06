package dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.config;

import com.intellij.psi.PsiNamedElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.ConfigurationFromContext;
import com.intellij.execution.actions.LazyRunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
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
            String runParam = getRunParametersForMultiSelection(context, workingDirectory);
            runConfig.getPythonRunConfiguration().setScriptParameters(runParam);
            Sdk sdk = ProjectRootManager.getInstance(context.getProject()).getProjectSdk();
            if (sdk != null) {
                runConfig.getPythonRunConfiguration().setSdk(sdk);
            }
            runConfig.setName(getRunDisplayNameForMultiSelection(context));
            return true;
        }
        return false;
    }

    @Override
    public boolean isConfigurationFromContext(@NotNull RobotRunConfiguration runConfig, @NotNull ConfigurationContext context) {
        if (isValidRobotExecutableScript(context)) {
            String workingDirectory = getWorkingDirectoryToUse(runConfig);
            String runParam = getRunParametersForMultiSelection(context, workingDirectory);
            boolean ret = runParam.trim().equals(runConfig.getPythonRunConfiguration().getScriptParameters().trim());
            if (ret) {
                runConfig.setName(getRunDisplayNameForMultiSelection(context));
            }
            return ret;
        }
        return false;
    }

    private String getWorkingDirectoryToUse(@NotNull RobotRunConfiguration runConfig) {
        String workingDirectory = runConfig.getPythonRunConfiguration().getWorkingDirectory();
        if (workingDirectory == null || workingDirectory.isEmpty()) {
            workingDirectory = runConfig.getPythonRunConfiguration().getWorkingDirectorySafe();
        }
        return workingDirectory;
    }

    @NotNull
    private String getRunParametersForMultiSelection(@NotNull ConfigurationContext context, String basePath) {
        PsiElement[] selectedElements = getSelectedPsiElements(context);
        if (selectedElements.length <= 1) {
            // Verwende bisherige Logik für einzelne Selektion
            return getRunParameters(context, basePath);
        }

        StringBuilder parameters = new StringBuilder();
        boolean containsRpa = false;

        for (PsiElement element : selectedElements) {
            String testName = getKeywordNameFromAnyElement(element);
            if (!testName.isEmpty()) {
                if (!parameters.isEmpty()) {
                    parameters.append(" ");
                }
                parameters.append("--test \"").append(testName.replace("\"", "\\\"")).append("\"");
            }
            containsRpa = containsTasksOnly(element);
        }

        if (parameters.isEmpty()) {
            parameters.append("--test *");
        }

        Location<?> location = context.getLocation();
        assert location != null;
        VirtualFile virtualFile = location.getVirtualFile();
        assert virtualFile != null;

        String filePath = virtualFile.getPath();
        String relativePath = relativizePath(basePath, filePath);
        parameters.append(" \"").append(relativePath.replace("\"", "\\\"")).append("\"");

        if (containsRpa) {
            parameters.insert(0, "--rpa ");
        }

        return parameters.toString();
    }

    private static boolean containsTasksOnly(PsiElement element) {
        if (element == null) {
            return false;
        }
        RobotExecutableSectionSectionVerifier verifier = new RobotExecutableSectionSectionVerifier();
        element.accept(verifier);
        return verifier.hasOnlyTasksSection();
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

        PsiElement element = context.getPsiLocation();
        if (containsTasksOnly(element)) {
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
        Location<PsiElement> location = context.getLocation();
        assert location != null;

        VirtualFile virtualFile = location.getVirtualFile();
        assert virtualFile != null;

        String testCaseName = getTestCaseName(context);
        return !testCaseName.isEmpty() ? testCaseName : virtualFile.getName();
    }

    private static boolean isValidRobotExecutableScript(@NotNull ConfigurationContext context) {
        Location<PsiElement> location = context.getLocation();
        if (location != null) {
            PsiElement element = location.getPsiElement();
            RobotExecutableSectionSectionVerifier verifier = new RobotExecutableSectionSectionVerifier();
            element.accept(verifier);
            if (verifier.isExecutable()) {
                return true;
            }

            VirtualFile virtualFile = location.getVirtualFile();
            if (virtualFile != null) {
                if (virtualFile.isDirectory()) {
                    Ref<Boolean> containsRobotFiles = Ref.create(false);
                    VfsUtil.processFileRecursivelyWithoutIgnored(virtualFile, file -> {
                        if (file.isDirectory()) {
                            return true;
                        }
                        if (RobotFeatureFileType.getInstance() == file.getFileType()) {
                            containsRobotFiles.set(true);
                            return false;
                        }
                        return true;
                    });
                    return containsRobotFiles.get();
                } else if (location.getPsiElement() instanceof RobotFile robotFile) {
                    verifier.reset();
                    robotFile.acceptChildren(verifier); // TODO: Does the robot file has the tree under it?
                    return verifier.isExecutable();
                }
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
        while (!(element instanceof RobotKeywordCall namedElement)) {
            element = element.getParent();
            if (element == null) {
                return "";
            }
        }
        String name = namedElement.getName();
        return name != null ? name : "";
    }

    private PsiElement[] getSelectedPsiElements(@NotNull ConfigurationContext context) {
        PsiElement[] elements = LangDataKeys.PSI_ELEMENT_ARRAY.getData(context.getDataContext());
        return elements != null && elements.length > 0 ? elements : new PsiElement[] { context.getPsiLocation() };
    }

    @NotNull
    private String getRunDisplayNameForMultiSelection(@NotNull ConfigurationContext context) {
        PsiElement[] selectedElements = getSelectedPsiElements(context);

        if (selectedElements.length <= 1) {
            return getTestCaseOrFileName(context);
        }

        int testCount = 0;
        for (PsiElement element : selectedElements) {
            String testName = getKeywordNameFromAnyElement(element);
            if (!testName.isEmpty()) {
                testCount++;
            }
        }

        if (testCount > 0) {
            return testCount + " Tests";
        }

        // Fallback auf Dateiname
        Location<?> location = context.getLocation();
        assert location != null;
        VirtualFile virtualFile = location.getVirtualFile();
        return virtualFile != null ? virtualFile.getName() : "Multiple Robot Tests";
    }
}
