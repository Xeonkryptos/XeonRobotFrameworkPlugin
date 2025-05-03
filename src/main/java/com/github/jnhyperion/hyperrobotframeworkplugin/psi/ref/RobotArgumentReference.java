package com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Import;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.PositionalArgument;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.CachedValueProvider.Result;
import com.intellij.psi.util.CachedValuesManager;
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
        PositionalArgument currentPositionalArgument = getElement();
        PsiElement parent = currentPositionalArgument.getParent();
        if (parent instanceof Import importElement) {
            PsiElement[] children = importElement.getChildren();
            if (children.length > 0 && children[0] == currentPositionalArgument) {
                return CachedValuesManager.getCachedValue(importElement, () -> {
                    PsiElement result = null;
                    Project project = importElement.getProject();
                    String importFileArgument = currentPositionalArgument.getContent();
                    if (importElement.isResource()) {
                        result = RobotFileManager.findElement(importFileArgument, project, importElement);
                    } else if (importElement.isLibrary() || importElement.isVariables()) {
                        result = RobotFileManager.findElementInContext(importFileArgument, project, importElement);
                    }

                    if (result == null) {
                        ResolveResult[] resolveResults = multiResolve(currentPositionalArgument);
                        if (resolveResults.length == 1) {
                            result = resolveResults[0].getElement();
                        }
                    }
                    return new Result<>(result, importElement, currentPositionalArgument);
                });
            }
            return null;
        }
        return null;
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        PositionalArgument positionalArgument = getElement();
        return multiResolve(positionalArgument);
    }

    // A static implementation of multiResolve is needed to avoid side effects in the CachedValueProvider implementation
    private static ResolveResult @NotNull [] multiResolve(PositionalArgument positionalArgument) {
        Project project = positionalArgument.getProject();
        PsiElement parent = positionalArgument.getParent();
        String argumentValue = positionalArgument.getContent();

        Set<ResolveResult> results = new LinkedHashSet<>();
        if (parent instanceof Import importElement) {
            if (importElement.isResource()) {
                for (PsiFile file : RobotFileManager.findPsiFiles(argumentValue, project)) {
                    results.add(new PsiElementResolveResult(file));
                }
            } else if ((importElement.isLibrary() || importElement.isVariables()) && argumentValue.endsWith(".py")) {
                for (PsiFile file : RobotFileManager.findPsiFiles(argumentValue, project)) {
                    results.add(new PsiElementResolveResult(file));
                }
            }
        }
        return results.toArray(ResolveResult.EMPTY_ARRAY);
    }
}
