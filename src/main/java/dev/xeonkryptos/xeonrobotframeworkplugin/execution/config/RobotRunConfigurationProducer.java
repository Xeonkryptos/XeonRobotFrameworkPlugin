package dev.xeonkryptos.xeonrobotframeworkplugin.execution.config;

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
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.config.RobotRunConfiguration.RobotRunnableUnitExecutionInfo;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotQualifiedNameOwner;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            String workingDirectory = FileUtils.getWorkingDirectoryToUse(runConfig);
            addRunParametersForMultiSelection(context, workingDirectory, runConfig);
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
            String workingDirectory = FileUtils.getWorkingDirectoryToUse(runConfig);
            RobotRunConfiguration verificationConfiguration = (RobotRunConfiguration) RobotRunConfigurationType.getRobotRunConfigurationType()
                                                                                                               .getConfigurationFactory()
                                                                                                               .createTemplateConfiguration(context.getProject());
            addRunParametersForMultiSelection(context, workingDirectory, verificationConfiguration);

            List<RobotRunnableUnitExecutionInfo> verificationTestCasesInfo = verificationConfiguration.getTestCases();
            List<RobotRunnableUnitExecutionInfo> verificationTasksInfo = verificationConfiguration.getTasks();
            List<String> verificationDirectories = verificationConfiguration.getDirectories();
            boolean ret = verificationTestCasesInfo.equals(runConfig.getTestCases()) && verificationTasksInfo.equals(runConfig.getTasks())
                          && verificationDirectories.equals(runConfig.getDirectories());
            if (ret) {
                runConfig.setName(getRunDisplayNameForMultiSelection(context));
            }
            return ret;
        }
        return false;
    }

    private void addRunParametersForMultiSelection(@NotNull ConfigurationContext context, String basePath, @NotNull RobotRunConfiguration runConfig) {
        PsiElement[] selectedElements = getSelectedPsiElements(context);
        if (selectedElements.length <= 1) {
            addRunParameters(context, basePath, runConfig);
        } else {
            List<RobotRunnableUnitExecutionInfo> executionInfos = null;
            for (PsiElement element : selectedElements) {
                RobotQualifiedNameOwner executableElement = getTestCaseTaskFromAnyElement(element);
                if (executableElement != null) {
                    if (executionInfos == null) {
                        executionInfos = new ArrayList<>();
                        if (executableElement instanceof RobotTestCaseStatement) {
                            runConfig.setTestCases(executionInfos);
                        } else {
                            runConfig.setTasks(executionInfos);
                        }
                    }
                    String unitName = executableElement.getName();
                    assert unitName != null;
                    unitName = unitName.replace(".", "\\.");
                    String qualifiedName = executableElement.getQualifiedName();
                    String qualifiedLocation = qualifiedName.substring(0, qualifiedName.length() - unitName.length() - 1);
                    RobotRunnableUnitExecutionInfo executionInfo = new RobotRunnableUnitExecutionInfo(qualifiedLocation, unitName);
                    executionInfos.add(executionInfo);
                }
            }

            if (executionInfos == null) {
                List<String> directories = runConfig.getDirectories();
                for (PsiElement selectedElement : selectedElements) {
                    if (selectedElement instanceof PsiFile psiFile) {
                        VirtualFile virtualFile = psiFile.getVirtualFile();
                        if (virtualFile == null) {
                            virtualFile = psiFile.getOriginalFile().getVirtualFile();
                        }
                        String filePath = virtualFile.getPath();
                        String relativePath = FileUtils.relativizePath(basePath, filePath);
                        directories.add(relativePath);
                    }
                }
            }
        }
    }

    private static void addRunParameters(ConfigurationContext context, String basePath, @NotNull RobotRunConfiguration runConfig) {
        RobotQualifiedNameOwner element = getTestCaseTaskElement(context);
        if (element != null) {
            String elementName = element.getName();
            assert elementName != null;
            String qualifiedName = element.getQualifiedName();
            String qualifiedLocation = qualifiedName.substring(0, qualifiedName.length() - elementName.length() - 1).replace("\"", "\\\"");
            List<RobotRunnableUnitExecutionInfo> executionInfos = new ArrayList<>();
            if (element instanceof RobotTestCaseStatement) {
                runConfig.setTestCases(executionInfos);
            } else {
                runConfig.setTasks(executionInfos);
            }
            RobotRunnableUnitExecutionInfo executionInfo = new RobotRunnableUnitExecutionInfo(qualifiedLocation, elementName);
            executionInfos.add(executionInfo);
        } else {
            List<String> directories = new ArrayList<>();
            runConfig.setDirectories(directories);
            Location<PsiElement> location = context.getLocation();
            assert location != null;
            VirtualFile virtualFile = location.getVirtualFile();
            assert virtualFile != null;
            String filePath = virtualFile.getPath();
            String relativePath = FileUtils.relativizePath(basePath, filePath);
            directories.add(relativePath);
        }
    }

    private static String getTestCaseOrFileName(ConfigurationContext context) {
        Location<PsiElement> location = context.getLocation();
        assert location != null;

        VirtualFile virtualFile = location.getVirtualFile();
        assert virtualFile != null;

        RobotQualifiedNameOwner qualifiedNameOwner = getTestCaseTaskElement(context);
        return qualifiedNameOwner != null ? qualifiedNameOwner.getName() : virtualFile.getName();
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

    private static RobotQualifiedNameOwner getTestCaseTaskElement(@NotNull ConfigurationContext context) {
        Location<?> location = context.getLocation();
        if (location != null) {
            return getTestCaseTaskFromAnyElement(location.getPsiElement());
        }
        return null;
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

    @Nullable
    private static RobotQualifiedNameOwner getTestCaseTaskFromAnyElement(PsiElement element) {
        return PsiTreeUtil.getParentOfType(element, false, RobotTestCaseStatement.class, RobotTaskStatement.class);
    }

    private PsiElement[] getSelectedPsiElements(@NotNull ConfigurationContext context) {
        PsiElement[] elements = LangDataKeys.PSI_ELEMENT_ARRAY.getData(context.getDataContext());
        return elements != null && elements.length > 0 ? elements : new PsiElement[] { context.getPsiLocation() };
    }
}
