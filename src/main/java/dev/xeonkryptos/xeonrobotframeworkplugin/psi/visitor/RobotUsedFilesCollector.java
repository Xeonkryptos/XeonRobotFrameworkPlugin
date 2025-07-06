package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotBddStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExecutableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSetupTeardownStatementsGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateArguments;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateStatementsGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableValue;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public final class RobotUsedFilesCollector extends RobotVisitor {

    private final Set<PsiFile> usedFiles = new LinkedHashSet<>();

    @Override
    public void visitSection(@NotNull RobotSection o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitSetupTeardownStatementsGlobalSetting(@NotNull RobotSetupTeardownStatementsGlobalSetting o) {
        RobotKeywordCall keywordCall = o.getKeywordCall();
        if (keywordCall != null) {
            keywordCall.accept(this);
        }
    }

    @Override
    public void visitTemplateStatementsGlobalSetting(@NotNull RobotTemplateStatementsGlobalSetting o) {
        o.getKeywordCall().accept(this);
    }

    @Override
    public void visitImportGlobalSetting(@NotNull RobotImportGlobalSetting o) {
        PsiElement resolvedElement = o.getImportedFile().getReference().resolve();
        if (resolvedElement != null) {
            PsiFile containingFile = resolvedElement.getContainingFile();
            usedFiles.add(containingFile);
        }
    }

    @Override
    public void visitTestCaseStatement(@NotNull RobotTestCaseStatement o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitTaskStatement(@NotNull RobotTaskStatement o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitUserKeywordStatement(@NotNull RobotUserKeywordStatement o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitBddStatement(@NotNull RobotBddStatement o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitExecutableStatement(@NotNull RobotExecutableStatement o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitVariableStatement(@NotNull RobotVariableStatement o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitVariableValue(@NotNull RobotVariableValue o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitTemplateArguments(@NotNull RobotTemplateArguments o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitTemplateParameter(@NotNull RobotTemplateParameter o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitKeywordCall(@NotNull RobotKeywordCall o) {
        PsiElement resolvedElement = o.getKeywordCallId().getReference().resolve();
        if (resolvedElement != null) {
            PsiFile containingFile = resolvedElement.getContainingFile();
            usedFiles.add(containingFile);
        }
        o.getAllCallArguments().forEach(argument -> argument.accept(this));
    }

    @Override
    public void visitParameter(@NotNull RobotParameter o) {
        o.getPositionalArgument().accept(this);
    }

    @Override
    public void visitPositionalArgument(@NotNull RobotPositionalArgument o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitVariable(@NotNull RobotVariable o) {
        PsiElement resolvedElement = o.getReference().resolve();
        if (resolvedElement != null) {
            PsiFile containingFile = resolvedElement.getContainingFile();
            usedFiles.add(containingFile);
        }
    }

    public Collection<PsiFile> getUsedFiles() {
        return usedFiles;
    }
}
