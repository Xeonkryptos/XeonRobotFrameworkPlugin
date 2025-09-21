package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.VariableNameUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class RobotVariableReferenceSearcher extends RecursiveRobotVisitor {

    private final RobotVariable variable;
    private final String variableName;
    private final Set<String> variableNameVariants;

    private final Set<PsiElement> foundElements = new LinkedHashSet<>();

    private boolean inOvershadowingContext = false;
    private boolean overshadows = false;

    public RobotVariableReferenceSearcher(RobotVariable variable, String variableName) {
        this.variable = variable;
        this.variableName = variableName;
        this.variableNameVariants = VariableNameUtil.INSTANCE.computeVariableNameVariants(variableName);
    }

    @Override
    public void visitVariablesSection(@NotNull RobotVariablesSection o) {
        inOvershadowingContext = true;
        super.visitVariablesSection(o);
        inOvershadowingContext = false;
    }

    @Override
    public void visitLocalArgumentsSetting(@NotNull RobotLocalArgumentsSetting o) {
        inOvershadowingContext = true;
        super.visitLocalArgumentsSetting(o);
        inOvershadowingContext = false;
    }

    @Override
    public void visitVariableDefinition(@NotNull RobotVariableDefinition o) {
        if (o.matches(variableName) && o.isInScope(variable)) {
            foundElements.add(o);
            overshadows |= inOvershadowingContext;
        }
    }

    @Override
    public void visitVariable(@NotNull RobotVariable o) {
        String variableName = o.getVariableName();
        if (VariableNameUtil.INSTANCE.matchesVariableName(variableName, variableNameVariants)) {
            foundElements.add(o);
        }
    }

    public Collection<PsiElement> getFoundElements() {
        return foundElements;
    }

    public boolean isOvershadowing() {
        return overshadows;
    }
}
