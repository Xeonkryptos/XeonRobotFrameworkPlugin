package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiElement;

public interface LookupElementMarker {

    String[] EMPTY_LOOKUP_WORDS = new String[0];

    String getLookup();

    default String getPresentableText() {
        return getLookup();
    }

    default String[] getLookupWords() {
        return EMPTY_LOOKUP_WORDS;
    }

    PsiElement reference();

    default boolean isCaseSensitive() {
        return true;
    }
}
