package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.compilation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotHighlighter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordInvokable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinitionGroup;
import org.jetbrains.annotations.NotNull;

public class RobotVariableArgumentAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof KeywordStatement keywordStatement) {
            VariableDefinitionGroup variableDefinitionGroup = PsiTreeUtil.getParentOfType(keywordStatement, VariableDefinitionGroup.class);
            if (variableDefinitionGroup != null) {
                KeywordInvokable keywordStatementInvokable = keywordStatement.getInvokable();
                PsiReference reference = keywordStatementInvokable.getReference();
                PsiElement resolvedElement = reference.resolve();
                if (resolvedElement == null) {
                    holder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                          .textAttributes(RobotHighlighter.ARGUMENT)
                          .range(keywordStatementInvokable)
                          .create();
                }
            }
        }
    }
}
