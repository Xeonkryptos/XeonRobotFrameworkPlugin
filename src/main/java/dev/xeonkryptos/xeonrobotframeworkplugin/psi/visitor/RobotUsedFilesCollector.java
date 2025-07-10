package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotBddStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExecutableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotGlobalSettingStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotResourceImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot;
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
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class RobotUsedFilesCollector extends RobotVisitor {

    private final Map<String, PsiReference> references = new HashMap<>();

    private final Set<PsiElement> visitedGlobalSettingStatement = new HashSet<>();

    @Override
    public void visitRoot(@NotNull RobotRoot o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitSection(@NotNull RobotSection o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitGlobalSettingStatement(@NotNull RobotGlobalSettingStatement o) {
        if (visitedGlobalSettingStatement.add(o)) {
            o.accept(this);
        }
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
    public void visitLibraryImportGlobalSetting(@NotNull RobotLibraryImportGlobalSetting o) {
        visitImportGlobalSetting(o);
    }

    @Override
    public void visitResourceImportGlobalSetting(@NotNull RobotResourceImportGlobalSetting o) {
        visitImportGlobalSetting(o);
    }

    @Override
    public void visitVariablesImportGlobalSetting(@NotNull RobotVariablesImportGlobalSetting o) {
        visitImportGlobalSetting(o);
    }

    @Override
    public void visitImportGlobalSetting(@NotNull RobotImportGlobalSetting o) {
        RobotPositionalArgument positionalArgument = o.getImportedFile();
        String fileName = positionalArgument.getText();
        references.put(fileName, positionalArgument.getReference());
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
        RobotKeywordCallId keywordCallId = o.getKeywordCallId();
        references.put(keywordCallId.getName(), keywordCallId.getReference());
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
        PsiElement nameIdentifier = o.getNameIdentifier();
        if (nameIdentifier != null) {
            String variableName = nameIdentifier.getText();
            PsiReference reference = o.getReference();
            references.put(variableName, reference);
        }
    }

    public Collection<PsiReference> getReferences() {
        return references.values();
    }
}
