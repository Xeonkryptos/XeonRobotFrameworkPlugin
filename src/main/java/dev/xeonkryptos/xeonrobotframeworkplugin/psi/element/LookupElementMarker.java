package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;

public interface LookupElementMarker {

    String getLookup();

    default String getPresentableText() {
        return getLookup();
    }

    default String[] getLookupWords() {
        return new String[] { getLookup() };
    }

    default InsertHandler<LookupElement> getInsertHandler() {
        return null;
    }

    PsiElement reference();

    default boolean isCaseSensitive() {
        return true;
    }
}
