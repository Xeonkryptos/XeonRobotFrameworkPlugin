package dev.xeonkryptos.xeonrobotframeworkplugin.completion

import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.LookupElementMarker

class VariableLookupElementMarkerDelegate(private val variable: DefinedVariable) : LookupElementMarker by variable {

    override fun getLookup(): String? = variable.lookup?.let { lookup -> variable.variableType.fullFledgedWrapping(lookup) }

    override fun getPresentableText(): String = variable.presentableText

    override fun getLookupWords(): Array<String> = variable.lookupWords

    override fun getInsertHandler(): InsertHandler<LookupElement?>? = variable.insertHandler

    override fun isCaseSensitive(): Boolean = variable.isCaseSensitive
}
