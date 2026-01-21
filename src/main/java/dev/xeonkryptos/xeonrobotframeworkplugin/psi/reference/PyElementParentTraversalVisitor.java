package dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference;

import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyElement;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyFile;
import org.jetbrains.annotations.NotNull;

public class PyElementParentTraversalVisitor extends PyElementVisitor {

    private final PyElementVisitor targetVisitor;

    public PyElementParentTraversalVisitor(PyElementVisitor targetVisitor) {
        this.targetVisitor = targetVisitor;
    }

    @Override
    public void visitPyElement(@NotNull PyElement node) {
        super.visitPyElement(node);
        node.accept(targetVisitor);

        PsiElement parent = node.getParent();
        if (parent != null && !(parent instanceof PyFile)) {
            parent.accept(this);
        }
    }
}
