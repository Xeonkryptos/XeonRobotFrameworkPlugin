@file:Suppress("UnstableApiUsage")

package dev.xeonkryptos.xeonrobotframeworkplugin.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.util.ProcessingContext
import com.jetbrains.python.documentation.PythonDocumentationProvider
import com.jetbrains.python.psi.PyFunction
import com.jetbrains.python.psi.PyTargetExpression
import com.jetbrains.python.psi.types.PyClassType
import com.jetbrains.python.psi.types.TypeEvalContext
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableExpressionId
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableExpressionMethodCallId
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.RobotExtendedVariableResolver
import javax.swing.Icon

/**
 * Proposes the members (attributes, properties and methods) of the object a part of an extended variable expression like `${OBJECT.<caret>}` is accessed on.
 * The members are looked up in the Python type the variable is inferred to have.
 */
internal class ExtendedVariableCompletionProvider : CompletionProvider<CompletionParameters>() {

    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
        val segment = extendedVariableSegmentAt(parameters.position) ?: return

        // The name is directly followed by an opening parenthesis already, only methods can be called then
        val methodCallOnly = segment is RobotVariableExpressionMethodCallId
        val typeEvalContext = RobotExtendedVariableResolver.typeEvalContext(segment)
        val lookupElements = RobotExtendedVariableResolver.receiverTypes(segment, typeEvalContext)
            .asSequence()
            .filterIsInstance<PyClassType>()
            .flatMap { collectMembers(it, typeEvalContext) }
            .distinctBy { it.name }
            .filter { methodCallOnly.not() || it is PyFunction && it.property == null }
            .mapNotNull { createLookupElement(it, typeEvalContext, addParentheses = !methodCallOnly) }
            .toList()

        result.addAllElements(lookupElements)
    }

    private fun collectMembers(type: PyClassType, context: TypeEvalContext): List<PsiNamedElement> {
        val pyClass = type.pyClass
        return (listOf(pyClass) + pyClass.getAncestorClasses(context)).flatMap { pyClassInHierarchy ->
            pyClassInHierarchy.methods.asList() + pyClassInHierarchy.classAttributes + pyClassInHierarchy.instanceAttributes
        }.filter { member -> member.name?.startsWith("__") == false }
    }

    private fun createLookupElement(member: PsiNamedElement, context: TypeEvalContext, addParentheses: Boolean): LookupElement? {
        val name = member.name ?: return null
        val builder = LookupElementBuilder.create(member, name)
        val lookupElement = when (member) {
            is PyFunction -> {
                val parameters = member.parameterList.parameters.filterNot { it.isSelf }
                val isProperty = member.property != null
                val returnType = context.getReturnType(member)?.let { PythonDocumentationProvider.getTypeName(it, context) }
                val functionBuilder = builder.withIcon(if (isProperty) AllIcons.Nodes.Property else AllIcons.Nodes.Method).withTypeText(returnType, true)
                if (isProperty) {
                    functionBuilder
                } else {
                    functionBuilder.withTailText("(" + parameters.joinToString(", ") { it.name ?: "" } + ")", true)
                        .let { if (addParentheses) it.withInsertHandler(parenthesesInsertHandler(hasParameters = parameters.isNotEmpty())) else it }
                }
            }
            is PyTargetExpression -> builder.withIcon(AllIcons.Nodes.Field).withTypeText(context.getType(member)?.let { PythonDocumentationProvider.getTypeName(it, context) }, true)
            else -> builder.withIcon(icon(member))
        }
        lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_CONTEXT, RobotLookupContext.EXTENDED_VARIABLE_SYNTAX)
        lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.VARIABLE)
        return lookupElement
    }

    private fun parenthesesInsertHandler(hasParameters: Boolean) = InsertHandler<LookupElement> { insertionContext, _ ->
        val document = insertionContext.document
        val tailOffset = insertionContext.tailOffset
        if (document.charsSequence.getOrNull(tailOffset) != '(') {
            document.insertString(tailOffset, "()")
        }
        insertionContext.editor.caretModel.moveToOffset(tailOffset + if (hasParameters) 1 else 2)
    }

    private fun icon(member: PsiNamedElement): Icon? = member.getIcon(0)
}

/**
 * Returns the part of an extended variable expression the given element is in, except the base variable.
 */
internal fun extendedVariableSegmentAt(position: PsiElement): PsiElement? = when (val parent = position.parent) {
    is RobotVariableExpressionMethodCallId -> parent
    is RobotVariableExpressionId -> parent.takeUnless(RobotExtendedVariableResolver::isBase)
    else -> null
}
