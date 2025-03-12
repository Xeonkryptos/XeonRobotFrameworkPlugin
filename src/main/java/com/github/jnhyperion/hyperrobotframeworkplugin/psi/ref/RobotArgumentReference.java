package com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Import;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.PositionalArgument;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

public class RobotArgumentReference extends PsiPolyVariantReferenceBase<PositionalArgument> {

    public RobotArgumentReference(@NotNull PositionalArgument positionalArgument) {
        super(positionalArgument, false);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiElement result = null;

        PositionalArgument positionalArgument = getElement();
        PsiElement parent = positionalArgument.getParent();
        if (parent instanceof Import importElement) {
            PsiElement[] children = parent.getChildren();
            if (children.length > 0 && children[0] == positionalArgument) {
                if (importElement.isResource()) {
                    result = RobotFileManager.findElement(positionalArgument.getPresentableText(), positionalArgument.getProject(), positionalArgument);
                } else if (importElement.isLibrary() || importElement.isVariables()) {
                    result = RobotFileManager.findElementInContext(positionalArgument.getPresentableText(),
                                                                   positionalArgument.getProject(),
                                                                   positionalArgument);
                }

                if (result == null) {
                    ResolveResult[] resolveResults = multiResolve(false);
                    if (resolveResults.length == 1) {
                        result = resolveResults[0].getElement();
                    }
                }
            }
        }

        return result;
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        Set<ResolveResult> results = new LinkedHashSet<>();

        PositionalArgument positionalArgument = getElement();
        Project project = positionalArgument.getProject();
        PsiElement parent = positionalArgument.getParent();
        String presentableText = positionalArgument.getPresentableText();

        if (parent instanceof Import importElement) {
            if (importElement.isResource()) {
                for (PsiFile file : RobotFileManager.findPsiFiles(presentableText, project)) {
                    results.add(new PsiElementResolveResult(file));
                }
            } else if ((importElement.isLibrary() || importElement.isVariables()) && presentableText.endsWith(".py")) {
                for (PsiFile file : RobotFileManager.findPsiFiles(presentableText, project)) {
                    results.add(new PsiElementResolveResult(file));
                }
            }
        }
        return results.toArray(new ResolveResult[0]);
    }
}
