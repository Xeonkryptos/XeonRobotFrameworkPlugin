package dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotPositionalArgumentReference extends PsiReferenceBase<RobotPositionalArgument> {

    public RobotPositionalArgumentReference(@NotNull RobotPositionalArgument positionalArgument) {
        super(positionalArgument);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return ResolveCache.getInstance(getElement().getProject()).resolveWithCaching(this, (reference, incompleteCode) -> {
            RobotPositionalArgument positionalArgument = reference.getElement();
            Object[] variants = RobotEnumValuesResolver.findPossibleEnumValuesFor(positionalArgument);
            if (variants.length > 0) {
                String enumValue = positionalArgument.getText();
                for (Object variant : variants) {
                    LookupElement lookupElement = (LookupElement) variant;
                    if (lookupElement.getAllLookupStrings().contains(enumValue)) {
                        return lookupElement.getPsiElement();
                    }
                }
            }
            return null;
        }, false, false);
    }
}
