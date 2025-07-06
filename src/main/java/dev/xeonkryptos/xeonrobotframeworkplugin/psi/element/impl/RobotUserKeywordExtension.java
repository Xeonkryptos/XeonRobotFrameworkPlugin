package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotUserKeywordStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.QualifiedNameBuilder;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotUserKeywordInputArgumentCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class RobotUserKeywordExtension extends RobotStubPsiElementBase<RobotUserKeywordStub, RobotUserKeywordStatement>
        implements RobotUserKeywordStatement {

    public RobotUserKeywordExtension(@NotNull ASTNode node) {
        super(node);
    }

    public RobotUserKeywordExtension(final RobotUserKeywordStub stub, final IStubElementType<RobotUserKeywordStub, RobotUserKeywordStatement> nodeType) {
        super(stub, nodeType);
    }

    @Override
    public Collection<DefinedParameter> getInputParameters() {
        Optional<RobotLocalSetting> argumentsSetting = getLocalSettingList().stream()
                                                                            .filter(localSetting -> "Arguments".equals(localSetting.getName()))
                                                                            .findFirst();
        if (argumentsSetting.isPresent()) {
            RobotLocalSetting robotLocalSetting = argumentsSetting.get();
            RobotUserKeywordInputArgumentCollector inputArgumentCollector = new RobotUserKeywordInputArgumentCollector();
            robotLocalSetting.acceptChildren(inputArgumentCollector);
            return inputArgumentCollector.getInputArguments();
        }
        return List.of();
    }

    @NotNull
    @Override
    public PsiElement getNameIdentifier() {
        return getUserKeywordStatementId();
    }

    @NotNull
    @Override
    public String getQualifiedName() {
        return QualifiedNameBuilder.computeQualifiedName(this);
    }
}
