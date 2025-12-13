package dev.xeonkryptos.xeonrobotframeworkplugin.completion

import com.intellij.codeInsight.completion.LookupElementListPresenter
import com.intellij.codeInsight.lookup.CharFilter
import com.intellij.codeInsight.lookup.Lookup
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage

class RobotCharFilter : CharFilter() {

    override fun acceptChar(
        c: Char, prefixLength: Int, lookup: Lookup?
    ): Result? {
        if (lookup?.isCompletion?.not() ?: false || lookup?.psiFile?.language != RobotLanguage.INSTANCE) return null

        if (c == '\t' || c == ' ' && endsWithSpace(lookup)) return Result.SELECT_ITEM_AND_FINISH_LOOKUP
        return Result.ADD_TO_PREFIX
    }

    private fun endsWithSpace(lookup: Lookup) = extractPrefix(lookup).endsWith(' ')

    private fun extractPrefix(lookup: Lookup): String {
        lookup.currentItem?.let { currentItem -> return lookup.itemPattern(currentItem) }
        return (lookup as LookupElementListPresenter).additionalPrefix
    }
}
