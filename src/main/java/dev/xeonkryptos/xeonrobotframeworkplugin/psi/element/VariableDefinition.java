package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element.VariableDefinitionStub;
import org.jetbrains.annotations.NotNull;

public interface VariableDefinition extends RobotStatement,
                                            PsiNameIdentifierOwner,
                                            DefinedVariable,
                                            NavigationItem,
                                            StubBasedPsiElement<VariableDefinitionStub>,
                                            VariableName,
                                            RobotQualifiedNameOwner {

    boolean isNested();

    @NotNull
    @Override
    String getName();
}
