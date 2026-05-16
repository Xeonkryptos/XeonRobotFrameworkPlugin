package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.stubs.PsiFileStub;
import com.intellij.psi.tree.IStubFileElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.lsp.RobotLspLexerFileContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import org.jetbrains.annotations.NotNull;

public class RobotStubFileElementType extends IStubFileElementType<PsiFileStub<?>> {

    public RobotStubFileElementType() {
        super("ROBOT_FILE", RobotLanguage.INSTANCE);
    }

    @Override
    public int getStubVersion() {
        return super.getStubVersion() + 27;
    }

    @Override
    protected ASTNode doParseContents(@NotNull ASTNode chameleon, @NotNull PsiElement psi) {
        PsiFile psiFile = psi.getContainingFile();
        VirtualFile virtualFile = psiFile != null ? psiFile.getVirtualFile() : null;
        RobotLspLexerFileContext.INSTANCE.getCurrentFile().set(virtualFile);
        try {
            return super.doParseContents(chameleon, psi);
        } finally {
            RobotLspLexerFileContext.INSTANCE.getCurrentFile().remove();
        }
    }
}
