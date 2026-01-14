package dev.xeonkryptos.xeonrobotframeworkplugin.psi.util;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.Service.Level;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.testFramework.LightVirtualFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallLibraryName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameterId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateParameterId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatementId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RecursiveRobotVisitor;
import org.jetbrains.annotations.NotNull;

@Service(Level.PROJECT)
public record RobotElementGenerator(Project project) {

    public static RobotElementGenerator getInstance(Project project) {
        return project.getService(RobotElementGenerator.class);
    }

    public RobotTaskId createNewTaskId(String taskId) {
        String fileContent = """
                             *** Tasks ***
                             %s
                                 [Documentation]  Dummy
                             """.formatted(taskId);
        PsiFile psiFile = createDummyPsiFile(fileContent);
        if (psiFile == null) {
            return null;
        }
        RobotTaskIdFinder finder = new RobotTaskIdFinder();
        psiFile.acceptChildren(finder);
        return finder.taskId;
    }

    public RobotTestCaseId createNewTestCaseId(String testCaseId) {
        String fileContent = """
                             *** Test Cases ***
                             %s
                                 [Documentation]  Dummy
                             """.formatted(testCaseId);
        PsiFile psiFile = createDummyPsiFile(fileContent);
        if (psiFile == null) {
            return null;
        }
        RobotTestCaseIdFinder finder = new RobotTestCaseIdFinder();
        psiFile.acceptChildren(finder);
        return finder.testCaseId;
    }

    public RobotUserKeywordStatementId createNewUserKeywordStatementId(String keywordName) {
        String fileContent = """
                             *** Keywords ***
                             %s
                                 [Documentation]  Dummy
                             """.formatted(keywordName);
        PsiFile psiFile = createDummyPsiFile(fileContent);
        if (psiFile == null) {
            return null;
        }
        RobotUserKeywordStatementIdFinder finder = new RobotUserKeywordStatementIdFinder();
        psiFile.acceptChildren(finder);
        return finder.userKeywordStatementId;
    }

    public RobotKeywordCallLibraryName createNewKeywordCallLibraryName(String libraryName) {
        String fileContent = """
                             *** Test Case ***
                             Dummy
                                 %s.dummy
                             """.formatted(libraryName);
        PsiFile psiFile = createDummyPsiFile(fileContent);
        if (psiFile == null) {
            return null;
        }
        RobotKeywordCallLibraryNameFinder finder = new RobotKeywordCallLibraryNameFinder();
        psiFile.acceptChildren(finder);
        return finder.keywordCallLibraryName;
    }

    public RobotKeywordCallName createNewKeywordCallName(String keywordName) {
        String fileContent = """
                             *** Test Case ***
                             Dummy
                                 %s
                             """.formatted(keywordName);

        PsiFile psiFile = createDummyPsiFile(fileContent);
        if (psiFile == null) {
            return null;
        }
        RobotKeywordCallNameFinder finder = new RobotKeywordCallNameFinder();
        psiFile.acceptChildren(finder);
        return finder.keywordCallName;
    }

    public RobotParameter createNewParameter(String parameterId) {
        String fileContent = """
                             *** Test Case ***
                             Dummy
                                 Keyword  %s=\s\s
                            \s""".formatted(parameterId);

        PsiFile psiFile = createDummyPsiFile(fileContent);
        if (psiFile == null) {
            return null;
        }
        RobotParameterFinder parameterFinder = new RobotParameterFinder();
        psiFile.acceptChildren(parameterFinder);
        return parameterFinder.parameter;
    }

    public RobotParameterId createNewParameterId(String parameterId) {
        String fileContent = """
                             *** Test Case ***
                             Dummy
                                 Keyword  %s=Dummy
                             """.formatted(parameterId);

        PsiFile psiFile = createDummyPsiFile(fileContent);
        if (psiFile == null) {
            return null;
        }
        RobotParameterIdFinder parameterIdFinder = new RobotParameterIdFinder();
        psiFile.acceptChildren(parameterIdFinder);
        return parameterIdFinder.parameterId;
    }

    public RobotTemplateParameterId createNewTemplateParameterId(String parameterId) {
        String fileContent = """
                             *** Test Case ***
                             Dummy
                                 [Template]  Keyword
                                 %s=Dummy
                             """.formatted(parameterId);

        PsiFile psiFile = createDummyPsiFile(fileContent);
        if (psiFile == null) {
            return null;
        }
        RobotTemplateParameterIdFinder parameterIdFinder = new RobotTemplateParameterIdFinder();
        psiFile.acceptChildren(parameterIdFinder);
        return parameterIdFinder.parameterId;
    }

    public RobotPositionalArgument createNewPositionalArgument(String positionalArgument) {
        String fileContent = """
                             *** Test Case ***
                             Dummy
                                 Keyword  %s
                             """.formatted(positionalArgument);

        PsiFile psiFile = createDummyPsiFile(fileContent);
        if (psiFile == null) {
            return null;
        }
        RobotPositionalArgumentFinder positionalArgumentFinder = new RobotPositionalArgumentFinder();
        psiFile.acceptChildren(positionalArgumentFinder);
        return positionalArgumentFinder.positionalArgument;
    }

    public RobotVariable createNewScalarVariable(String variableBodyId) {
        return createNewScalarVariable(variableBodyId, "");
    }

    public RobotVariable createNewScalarVariable(String variableBodyId, String extensionText) {
        String fileContent = """
                             *** Variables ***
                             ${%s}%s=  DUMMY
                             """.formatted(variableBodyId, extensionText);

        PsiFile psiFile = createDummyPsiFile(fileContent);
        if (psiFile == null) {
            return null;
        }
        RobotVariableFinder variableFinder = new RobotVariableFinder();
        psiFile.acceptChildren(variableFinder);
        return variableFinder.variable;
    }

    public RobotVariableBodyId createNewVariableBodyId(String variableBodyId) {
        String fileContent = """
                             *** Variables ***
                             ${%s}=  DUMMY
                             """.formatted(variableBodyId);

        PsiFile psiFile = createDummyPsiFile(fileContent);
        if (psiFile == null) {
            return null;
        }
        RobotVariableBodyIdFinder variableBodyFinder = new RobotVariableBodyIdFinder();
        psiFile.acceptChildren(variableBodyFinder);
        return variableBodyFinder.variableBodyId;
    }

    public PsiElement createEolElement(int count) {
        String repeatedEols = "\n".repeat(Math.max(0, count - 1));
        String fileContent = """
                             *** Test Case ***
                             Dummy%s
                             """.formatted(repeatedEols);

        PsiFile psiFile = createDummyPsiFile(fileContent);
        if (psiFile == null) {
            return null;
        }
        RobotTestCaseIdFinder testCaseIdFinder = new RobotTestCaseIdFinder();
        psiFile.acceptChildren(testCaseIdFinder);
        return testCaseIdFinder.testCaseId.getNextSibling();
    }

    public PsiFile createDummyPsiFile(String text) {
        PsiFileFactory factory = PsiFileFactory.getInstance(project);

        LightVirtualFile virtualFile = new LightVirtualFile("dummy.robot", RobotFeatureFileType.getInstance(), text);
        return ((PsiFileFactoryImpl) factory).trySetupPsiForFile(virtualFile, RobotLanguage.INSTANCE, false, true);
    }

    private static final class RobotUserKeywordStatementIdFinder extends RecursiveRobotVisitor {

        private RobotUserKeywordStatementId userKeywordStatementId;

        @Override
        public void visitUserKeywordStatementId(@NotNull RobotUserKeywordStatementId o) {
            userKeywordStatementId = o;
        }
    }

    private static final class RobotTaskIdFinder extends RecursiveRobotVisitor {

        private RobotTaskId taskId;

        @Override
        public void visitTaskId(@NotNull RobotTaskId o) {
            taskId = o;
        }
    }

    private static final class RobotTestCaseIdFinder extends RecursiveRobotVisitor {

        private RobotTestCaseId testCaseId;

        @Override
        public void visitTestCaseId(@NotNull RobotTestCaseId o) {
            testCaseId = o;
        }
    }

    private static final class RobotKeywordCallLibraryNameFinder extends RecursiveRobotVisitor {

        private RobotKeywordCallLibraryName keywordCallLibraryName;

        @Override
        public void visitKeywordCallLibraryName(@NotNull RobotKeywordCallLibraryName o) {
            keywordCallLibraryName = o;
        }
    }

    private static final class RobotKeywordCallNameFinder extends RecursiveRobotVisitor {

        private RobotKeywordCallName keywordCallName;

        @Override
        public void visitKeywordCallName(@NotNull RobotKeywordCallName o) {
            keywordCallName = o;
        }
    }

    private static final class RobotParameterFinder extends RecursiveRobotVisitor {

        private RobotParameter parameter;

        @Override
        public void visitParameter(@NotNull RobotParameter o) {
            parameter = o;
        }
    }

    private static final class RobotParameterIdFinder extends RecursiveRobotVisitor {

        private RobotParameterId parameterId;

        @Override
        public void visitParameterId(@NotNull RobotParameterId o) {
            parameterId = o;
        }
    }

    private static final class RobotTemplateParameterIdFinder extends RecursiveRobotVisitor {

        private RobotTemplateParameterId parameterId;

        @Override
        public void visitTemplateParameterId(@NotNull RobotTemplateParameterId o) {
            parameterId = o;
        }
    }

    private static final class RobotPositionalArgumentFinder extends RecursiveRobotVisitor {

        private RobotPositionalArgument positionalArgument;

        @Override
        public void visitPositionalArgument(@NotNull RobotPositionalArgument o) {
            positionalArgument = o;
        }
    }

    private static final class RobotVariableBodyIdFinder extends RecursiveRobotVisitor {

        private RobotVariableBodyId variableBodyId;

        @Override
        public void visitVariableBodyId(@NotNull RobotVariableBodyId o) {
            variableBodyId = o;
        }
    }

    private static final class RobotVariableFinder extends RecursiveRobotVisitor {

        private RobotVariable variable;

        @Override
        public void visitVariable(@NotNull RobotVariable o) {
            variable = o;
        }
    }
}
