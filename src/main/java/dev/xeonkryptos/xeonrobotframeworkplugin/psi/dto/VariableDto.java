package dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.ReservedVariableScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VariableDto implements DefinedVariable {

    private static final String SCALAR_VARIABLE_FORMAT = "${%s}";

    private final PsiElement reference;
    private final String name;
    private final String matchingVariableName;
    private final ReservedVariableScope scope;

    public VariableDto(@NotNull PsiElement reference, @NotNull String name, @Nullable ReservedVariableScope scope) {
        this(reference, SCALAR_VARIABLE_FORMAT.formatted(name.trim()), name, scope);
    }

    public VariableDto(@NotNull PsiElement reference, @NotNull String name, @NotNull String matchingVariableName, @Nullable ReservedVariableScope scope) {
        this.reference = reference;
        this.name = name.trim();
        this.matchingVariableName = matchingVariableName.trim();
        this.scope = scope;
    }

    @Override
    public final boolean matches(@Nullable String text) {
        if (text == null) {
            return false;
        }
        return matchingVariableName.equalsIgnoreCase(text.trim());
    }

    @Override
    public final boolean isInScope(@NotNull PsiElement position) {
        return this.scope == null || this.scope.isInScope(reference, position);
    }

    @NotNull
    @Override
    public final PsiElement reference() {
        return this.reference;
    }

    @Nullable
    @Override
    public final String getLookup() {
        return name;
    }

    @Override
    public String getPresentableText() {
        return this.name;
    }

    @Override
    public String[] getLookupWords() {
        return new String[] { name, matchingVariableName };
    }

    @Override
    public InsertHandler<LookupElement> getInsertHandler() {
        return (context, item) -> {
            Document document = context.getDocument();
            String lookupString = item.getLookupString();

            int startOffset = context.getStartOffset();
            int selectionEndOffset = context.getSelectionEndOffset();

            int targetStartOffset = Math.max(startOffset - 2, 0);
            int targetEndOffset = Math.min(selectionEndOffset + 1, document.getTextLength());

            String text = document.getText(new TextRange(targetStartOffset, targetEndOffset));
            if (text.startsWith("${") || text.startsWith("%{") || text.startsWith("@{") || text.startsWith("&{")) {
                startOffset -= 2;
            }
            if (text.endsWith("}")) {
                selectionEndOffset += 1;
            }

            document.replaceString(startOffset, selectionEndOffset, lookupString);
        };
    }

    @Override
    public boolean isCaseSensitive() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            VariableDto variable = (VariableDto) o;
            return this.name.equals(variable.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
