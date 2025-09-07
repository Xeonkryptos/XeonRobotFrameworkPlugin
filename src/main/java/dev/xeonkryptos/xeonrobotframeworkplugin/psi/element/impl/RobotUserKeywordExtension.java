package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatementId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotUserKeywordStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotUserKeywordInputArgumentCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class RobotUserKeywordExtension extends RobotStubPsiElementBase<RobotUserKeywordStub, RobotUserKeywordStatement>
        implements RobotUserKeywordStatement {

    private Collection<DefinedParameter> inputParameters;

    public RobotUserKeywordExtension(@NotNull ASTNode node) {
        super(node);
    }

    public RobotUserKeywordExtension(final RobotUserKeywordStub stub, final IStubElementType<RobotUserKeywordStub, RobotUserKeywordStatement> nodeType) {
        super(stub, nodeType);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        inputParameters = null;
    }

    @Override
    public Collection<DefinedParameter> getInputParameters() {
        if (inputParameters == null) {
            Optional<RobotLocalArgumentsSetting> argumentsSetting = getLocalArgumentsSettingList().stream().findFirst();
            if (argumentsSetting.isPresent()) {
                RobotLocalArgumentsSetting robotLocalSetting = argumentsSetting.get();
                RobotUserKeywordInputArgumentCollector inputArgumentCollector = new RobotUserKeywordInputArgumentCollector();
                robotLocalSetting.acceptChildren(inputArgumentCollector);
                inputParameters = inputArgumentCollector.getInputArguments();
            } else {
                inputParameters = List.of();
            }
        }
        return inputParameters;
    }

    @Override
    public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        RobotUserKeywordStatementId newUserKeywordStatementId = RobotElementGenerator.getInstance(getProject()).createNewUserKeywordStatementId(newName);
        if (newUserKeywordStatementId != null) {
            getNameIdentifier().replace(newUserKeywordStatementId);
        }
        return this;
    }
}
