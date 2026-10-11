package dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.psi.AccessDirection
import com.jetbrains.python.psi.PyClass
import com.jetbrains.python.psi.PyFunction
import com.jetbrains.python.psi.PyTypedElement
import com.jetbrains.python.psi.resolve.PyResolveContext
import com.jetbrains.python.psi.types.PyClassTypeImpl
import com.jetbrains.python.psi.types.PyType
import com.jetbrains.python.psi.types.PyUnionType
import com.jetbrains.python.psi.types.TypeEvalContext
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotPsiUtil
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordVariableStatement
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotReturnStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDatatype
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableExpressionDefinition
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableExpressionId
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableExpressionMethodCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableExpressionMethodCallId

/**
 * Resolves the parts of an extended variable syntax expression like `${OBJECT.attribute.method(1).other}`.
 *
 * The first part (`OBJECT`) is a normal variable. Its Python type is inferred from the variable definition (data type conversion, return type of the
 * keyword it is assigned from or the type of a variable in a Python variable file). Every following part is resolved as a member of the type of the
 * part before.
 */
object RobotExtendedVariableResolver {

    /**
     * The accessed parts of the expression in source order. Each entry is a [RobotVariableExpressionId] or a [RobotVariableExpressionMethodCallId]. A nested
     * variable is represented by `null` as nothing can be said about what it evaluates to.
     */
    fun segmentsOf(definition: RobotVariableExpressionDefinition): List<PsiElement?> {
        val segments = mutableListOf<PsiElement?>()
        for (child in definition.children) {
            when (child) {
                is RobotVariableExpressionId -> segments += child
                is RobotVariableExpressionMethodCall -> segments += child.variableExpressionMethodCallId
                is RobotVariable -> segments += null
            }
        }
        return segments
    }

    fun definitionOf(segment: PsiElement): RobotVariableExpressionDefinition? = PsiTreeUtil.getParentOfType(segment, RobotVariableExpressionDefinition::class.java)

    fun isBase(id: RobotVariableExpressionId): Boolean {
        val definition = definitionOf(id) ?: return false
        return segmentsOf(definition).firstOrNull() === id
    }

    /** Resolves the base variable of an extended variable expression like `OBJECT` in `${OBJECT.attribute}` to its definitions. */
    fun resolveBase(id: RobotVariableExpressionId): List<PsiElement> {
        val variable = PsiTreeUtil.getParentOfType(id, RobotVariable::class.java) ?: return emptyList()
        return RobotVariableContentReference.findVariableDefinitions(variable, id.text).toList()
    }

    /** Resolves a part behind the base variable to the matching members (attributes, properties, methods) of the object before it. */
    fun resolveMember(segment: PsiElement, name: String): List<PsiElement> {
        val context = typeEvalContext(segment)
        val isMethodCall = segment is RobotVariableExpressionMethodCallId
        return receiverTypes(segment, context).flatMap { membersOf(it, name, context) }.filter { !isMethodCall || it is PyFunction }.distinct()
    }

    /** The possible Python types of the object a part is accessed on. Empty when they are unknown. */
    fun receiverTypes(segment: PsiElement, context: TypeEvalContext = typeEvalContext(segment)): List<PyType> {
        val definition = definitionOf(segment) ?: return emptyList()
        val segments = segmentsOf(definition)
        val index = segments.indexOfFirst { it === segment }
        if (index <= 0) return emptyList()

        var types = baseTypes(segments.first() as? RobotVariableExpressionId ?: return emptyList(), context)
        for (i in 1..<index) {
            val previousSegment = segments[i] ?: return emptyList()
            types = types.flatMap { typeOfMember(it, previousSegment, context) }
            if (types.isEmpty()) return emptyList()
        }
        return types
    }

    private const val RETURN_SETTING_NAME = "Return"

    fun typeEvalContext(element: PsiElement): TypeEvalContext = TypeEvalContext.codeAnalysis(element.project, element.containingFile)

    private fun baseTypes(base: RobotVariableExpressionId, context: TypeEvalContext): List<PyType> {
        val visited = mutableSetOf<PsiElement>()
        return resolveBase(base).flatMap { typeOfDefinition(it, context, visited) }
    }

    // visited protects against endless recursion with user keywords returning variables which are assigned from the keyword itself
    private fun typeOfDefinition(definition: PsiElement, context: TypeEvalContext, visited: MutableSet<PsiElement>): List<PyType> = when (definition) {
        is RobotVariableDefinition -> typeOfRobotVariableDefinition(definition, context, visited)
        is PyTypedElement -> listOfNotNull(context.getType(definition)).flatMap(::flatten)
        else -> emptyList()
    }

    private fun typeOfRobotVariableDefinition(definition: RobotVariableDefinition, context: TypeEvalContext, visited: MutableSet<PsiElement>): List<PyType> {
        val dataType = PsiTreeUtil.findChildOfType(definition, RobotVariableDatatype::class.java)
        if (dataType != null) {
            val pyClass = dataType.reference.resolve() as? PyClass ?: return emptyList()
            return listOf(PyClassTypeImpl(pyClass, false))
        }

        val statement = PsiTreeUtil.getParentOfType(definition, RobotKeywordVariableStatement::class.java, true) ?: return emptyList()
        // With multiple assigned variables the keyword returns a collection, nothing can be said about a single one
        if (statement.variableDefinitionList.size != 1) return emptyList()
        val keywordReference = statement.keywordCall.keywordCallName.reference as? PsiPolyVariantReference ?: return emptyList()
        return keywordReference.multiResolve(false).mapNotNull { it.element }.flatMap { keyword ->
            when (keyword) {
                is PyFunction -> listOfNotNull(context.getReturnType(keyword)).flatMap(::flatten)
                is RobotUserKeywordStatement -> typeOfUserKeywordReturn(keyword, context, visited)
                else -> emptyList()
            }
        }
    }

    /**
     * The type of what a user keyword returns with `[Return]` or `RETURN`. Only values consisting of exactly one variable are considered as the type of
     * anything else can't be said.
     */
    private fun typeOfUserKeywordReturn(keyword: RobotUserKeywordStatement, context: TypeEvalContext, visited: MutableSet<PsiElement>): List<PyType> {
        if (!visited.add(keyword)) return emptyList()

        val returnedValues = keyword.localSettingList.filter { it.settingName.equals(RETURN_SETTING_NAME, ignoreCase = true) }.map { it.positionalArgumentList } +
                PsiTreeUtil.findChildrenOfType(keyword, RobotReturnStructure::class.java).map { it.positionalArgumentList }
        return returnedValues.filter { it.size == 1 }.mapNotNull { it.single() }.mapNotNull { returnedValue ->
            PsiTreeUtil.findChildOfType(returnedValue, RobotVariable::class.java, true)?.takeIf { it.text == returnedValue.text }
        }.flatMap { returnedVariable ->
            val variableContent = RobotPsiUtil.getVariableContent(returnedVariable) ?: return@flatMap emptyList()
            (variableContent.reference as? PsiPolyVariantReference)?.multiResolve(false).orEmpty().mapNotNull { it.element }
                .flatMap { typeOfDefinition(it, context, visited) }
        }
    }

    private fun typeOfMember(receiver: PyType, segment: PsiElement, context: TypeEvalContext): List<PyType> {
        val isMethodCall = segment is RobotVariableExpressionMethodCallId
        return membersOf(receiver, segment.text, context).mapNotNull { member ->
            when {
                member is PyFunction && (isMethodCall || member.property != null) -> context.getReturnType(member)
                member is PyFunction -> null
                !isMethodCall && member is PyTypedElement -> context.getType(member)
                else -> null
            }
        }.flatMap(::flatten)
    }

    private fun membersOf(type: PyType, name: String, context: TypeEvalContext): List<PsiElement> =
        type.resolveMember(name, null, AccessDirection.READ, PyResolveContext.defaultContext(context))?.mapNotNull { it.element } ?: emptyList()

    private fun flatten(type: PyType): List<PyType> = if (type is PyUnionType) type.members.filterNotNull().flatMap(::flatten) else listOf(type)
}
