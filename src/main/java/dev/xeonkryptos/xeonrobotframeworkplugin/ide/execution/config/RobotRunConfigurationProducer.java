package dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.config;

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
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.MyLogger;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

        Set<VirtualFile> virtualFiles = new HashSet<>(selectedElements.length);
        for (PsiElement element : selectedElements) {
            String testName = getTestCaseTaskNameFromAnyElement(element);
            if (!testName.isEmpty()) {
                if (!parameters.isEmpty()) {
                    parameters.append(" ");
                }
                parameters.append("--test \"").append(testName.replace("\"", "\\\"")).append("\"");
            }
            containsRpa = containsTasksOnly(element);
            VirtualFile virtualFile = element.getContainingFile().getVirtualFile();
            virtualFiles.add(virtualFile);
        }

        if (parameters.isEmpty()) {
            for (VirtualFile virtualFile : virtualFiles) {
                String filePath = virtualFile.getPath();
                String relativePath = relativizePath(basePath, filePath);
                parameters.append(" \"").append(relativePath.replace("\"", "\\\"")).append("\"");
            }
        } else {
            String relativePath = computeExecutionLocation(context, basePath, virtualFiles);
            parameters.append(" \"").append(relativePath.replace("\"", "\\\"")).append("\"");
        }

        if (containsRpa) {
            parameters.insert(0, "--rpa ");
        }

        return parameters.toString();
    }

    private static @NotNull String computeExecutionLocation(@NotNull ConfigurationContext context, String basePath, Set<VirtualFile> virtualFiles) {
        VirtualFile commonAncestor = VfsUtil.getCommonAncestor(virtualFiles);
        if (commonAncestor == null) {
            assert context.getLocation() != null;
            MyLogger.logger.warn("No common ancestor found for selected elements, using context location instead. %s".formatted(virtualFiles));
            commonAncestor = context.getLocation().getVirtualFile();
        }

        assert commonAncestor != null;
        String filePath = commonAncestor.getPath();
        return relativizePath(basePath, filePath);
    }

    private static boolean containsTasksOnly(PsiElement element) {
        if (element == null) {
            return false;
        }
        RobotRoot root = PsiTreeUtil.getParentOfType(element, RobotRoot.class);
        if (root == null) {
            return false;
        }
        RobotExecutableSectionSectionVerifier verifier = new RobotExecutableSectionSectionVerifier();
        root.accept(verifier);
        return verifier.hasOnlyTasksSection();
    }

    @NotNull
    private static String getRunParameters(ConfigurationContext context, String basePath) {
        String testCaseName = getTestCaseTaskName(context);
        String projectBasePath = context.getProject().getBasePath();
        assert projectBasePath != null;

        Location<?> location = context.getLocation();
        assert location != null;

        VirtualFile virtualFile = location.getVirtualFile();
        assert virtualFile != null;

        testCaseName = testCaseName.replace("\"", "\\\"");
        String runParameters = "";
        if (!testCaseName.isEmpty()) {
            runParameters = "--test \"" + testCaseName + "\"";
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

        String testCaseName = getTestCaseTaskName(context);
        return !testCaseName.isEmpty() ? testCaseName : virtualFile.getName();
    }

    private static boolean isValidRobotExecutableScript(@NotNull ConfigurationContext context) {
        Location<PsiElement> location = context.getLocation();
        if (location != null) {
            PsiElement element = location.getPsiElement();
            if (isDirectlyExecutable(element)) {
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
                    RobotExecutableSectionSectionVerifier verifier = new RobotExecutableSectionSectionVerifier();
                    robotFile.acceptChildren(verifier);
                    return verifier.isExecutable();
                }
            }
        }
        return false;
    }

    private static boolean isDirectlyExecutable(PsiElement element) {
        RobotExecutableSectionSectionVerifier verifier = new RobotExecutableSectionSectionVerifier();
        if (element instanceof RobotFile robotFile) {
            robotFile.acceptChildren(verifier);
        } else {
            RobotRoot root = PsiTreeUtil.getParentOfType(element, RobotRoot.class);
            if (root != null) {
                root.accept(verifier);
            }
        }
        return verifier.isExecutable();
    }

    @NotNull
    private static String getTestCaseTaskName(@NotNull ConfigurationContext context) {
        Location<?> location = context.getLocation();
        if (location != null) {
            return getTestCaseTaskNameFromAnyElement(location.getPsiElement());
        }
        return "";
    }

    @NotNull
    private String getRunDisplayNameForMultiSelection(@NotNull ConfigurationContext context) {
        PsiElement[] selectedElements = getSelectedPsiElements(context);
        if (selectedElements.length <= 1) {
            return getTestCaseOrFileName(context);
        }

        boolean robotFileFound = Arrays.stream(selectedElements).anyMatch(element -> element instanceof RobotFile);
        if (robotFileFound) {
            return "Multiple Robot Tests";
        }
        return selectedElements.length + " Tests";
    }

    @NotNull
    private static String getTestCaseTaskNameFromAnyElement(PsiElement element) {
        PsiNamedElement executableElement = PsiTreeUtil.getParentOfType(element, false, RobotTestCaseStatement.class, RobotTaskStatement.class);
        if (executableElement == null) {
            return "";
        }
        assert executableElement.getName() != null;
        return executableElement.getName();
    }

    private PsiElement[] getSelectedPsiElements(@NotNull ConfigurationContext context) {
        PsiElement[] elements = LangDataKeys.PSI_ELEMENT_ARRAY.getData(context.getDataContext());
        return elements != null && elements.length > 0 ? elements : new PsiElement[] { context.getPsiLocation() };
    }
}
