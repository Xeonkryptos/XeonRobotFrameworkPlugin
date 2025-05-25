package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element.KeywordDefinitionStub;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public interface KeywordDefinition
        extends RobotStatement, PsiNamedElement, PsiNameIdentifierOwner, NavigationItem, StubBasedPsiElement<KeywordDefinitionStub>, RobotQualifiedNameOwner {

    Collection<DefinedParameter> getParameters();

    @NotNull
    List<KeywordInvokable> getInvokedKeywords();

    @NotNull
    Collection<DefinedVariable> getDeclaredVariables();

    String getKeywordName();
}
