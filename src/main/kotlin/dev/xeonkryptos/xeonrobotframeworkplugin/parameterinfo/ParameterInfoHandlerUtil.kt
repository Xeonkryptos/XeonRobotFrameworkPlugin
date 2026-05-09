package dev.xeonkryptos.xeonrobotframeworkplugin.parameterinfo

import com.intellij.lang.parameterInfo.ParameterInfoUIContext
import com.intellij.lang.parameterInfo.ParameterInfoUIContextEx
import com.intellij.openapi.util.Pair
import com.intellij.psi.PsiElement
import com.intellij.psi.SyntaxTraverser
import com.jetbrains.python.documentation.PythonDocumentationProvider
import com.jetbrains.python.psi.PyCallable
import com.jetbrains.python.psi.PyFunction
import com.jetbrains.python.psi.PyNamedParameter
import com.jetbrains.python.psi.PySingleStarParameter
import com.jetbrains.python.psi.PySlashParameter
import com.jetbrains.python.psi.PyTupleParameter
import com.jetbrains.python.psi.PyUtil
import com.jetbrains.python.psi.impl.ParamHelper
import com.jetbrains.python.psi.types.PyCallableParameter
import com.jetbrains.python.psi.types.PyCallableParameterImpl
import com.jetbrains.python.psi.types.PyCallableType
import com.jetbrains.python.psi.types.PyStructuralType
import com.jetbrains.python.psi.types.PyType
import com.jetbrains.python.psi.types.TypeEvalContext
import dev.xeonkryptos.xeonrobotframeworkplugin.config.RobotOptionsProvider
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotArgument
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotCallArgumentsContainer
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors

@Suppress("UnstableApiUsage")
object ParameterInfoHandlerUtil {
    @JvmStatic
    fun <V> getCurrentParameterIndex(traverser: SyntaxTraverser<V>, offset: Int): Int {
        val root: V = traverser.getRoot()
        var curOffset = traverser.api.rangeOf(root!!).startOffset
        if (offset < curOffset) {
            return -1
        }

        var index = 0
        val delimiterTypeSet = setOf(RobotTypes.PARAMETER, RobotTypes.POSITIONAL_ARGUMENT, RobotTypes.TEMPLATE_PARAMETER, RobotTypes.TEMPLATE_ARGUMENT)
        for (child in traverser) {
            curOffset += traverser.api.rangeOf(child!!).length
            if (offset < curOffset) {
                val type = traverser.api.typeOf(child)
                if (delimiterTypeSet.contains(type)) {
                    index++
                }
                break
            }

            val type = traverser.api.typeOf(child)
            if (delimiterTypeSet.contains(type)) {
                index++
            }
        }

        return index
    }

    @JvmStatic
    fun buildHintsAndAnnotationsWithHighlights(callingFunction: PsiElement,
                                               context: ParameterInfoUIContext,
                                               currentParamIndex: Int,
                                               hintFlags: MutableMap<Int, EnumSet<ParameterInfoUIContextEx.Flag>>): Pair<MutableList<String>, MutableList<String>>? {
        val allCallArgumentsContainer = context.parameterOwner as RobotCallArgumentsContainer
        val arguments = allCallArgumentsContainer.getAllCallArguments().toTypedArray()

        val project = callingFunction.project
        val hintsAndAnnotations: Pair<MutableList<String>, MutableList<String>>
        if (callingFunction is PyFunction) {
            val indexToNamedParameter: MutableMap<Int, PyCallableParameter> = mutableMapOf()

            // param -> hint index. indexes are not contiguous, because some hints are parentheses.
            val parameterToHintIndex: MutableMap<PyCallableParameter, Int> = mutableMapOf()

            val typeEvalContext = TypeEvalContext.codeAnalysis(project, callingFunction.containingFile)
            val parameters = callingFunction.getParameters(typeEvalContext)
            val callableType = typeEvalContext.getType(callingFunction) as? PyCallableType? ?: return null

            hintsAndAnnotations = buildParameterListHint(parameters, indexToNamedParameter, parameterToHintIndex, hintFlags, typeEvalContext)
            highlightParameters(mutableListOf(*arguments), callableType, parameters, indexToNamedParameter, parameterToHintIndex, hintFlags, currentParamIndex)
        } else {
            val parameterToHintIndex: MutableMap<DefinedParameter, Int> = mutableMapOf()

            val userKeyword = callingFunction as RobotUserKeywordStatement
            val parameters = userKeyword.getInputParameters()
            val hints: MutableList<String> = ArrayList()
            val annotations: MutableList<String> = ArrayList()
            val size = parameters.size
            for ((index, parameter) in parameters.withIndex()) {
                var hint = parameter.getLookup() ?: continue
                if (parameter.hasDefaultValue()) {
                    hint += " = " + parameter.getDefaultValue()
                }
                if (index + 1 < size) {
                    hint += ", "
                }
                val hintIndex = hints.size
                hintFlags[hintIndex] = EnumSet.noneOf(ParameterInfoUIContextEx.Flag::class.java)
                parameterToHintIndex[parameter] = hintIndex
                hints.add(hint)
                annotations.add("")
            }
            hintsAndAnnotations = Pair.create(hints, annotations)
            highlightParameters(parameters.toList(), parameterToHintIndex, hintFlags, mutableListOf(*arguments), currentParamIndex)
        }

        return hintsAndAnnotations
    }

    /**
     * Builds a list of textual representation of parameters
     * Returns two lists: parameters with type hints and parameters with type annotations
     *
     * @param parameters            parameters of a callable
     * @param indexToNamedParameter used to collect all named parameters of callable
     * @param parameterToHintIndex  used to collect info about parameter hints
     * @param hintFlags             mark parameter as deprecated/highlighted/strikeout
     * @param context               context to be used to get parameter representation
     */
    fun buildParameterListHint(parameters: MutableList<PyCallableParameter>,
                               indexToNamedParameter: MutableMap<Int, PyCallableParameter>,
                               parameterToHintIndex: MutableMap<PyCallableParameter, Int>,
                               hintFlags: MutableMap<Int, EnumSet<ParameterInfoUIContextEx.Flag>>,
                               context: TypeEvalContext): Pair<MutableList<String>, MutableList<String>> {
        val hintsList: MutableList<String> = ArrayList()
        val annotations: MutableList<String> = ArrayList()
        val currentParameterIndex = intArrayOf(0)
        ParamHelper.walkDownParameters(parameters, object : ParamHelper.ParamWalker {
            override fun enterTupleParameter(param: PyTupleParameter?, first: Boolean, last: Boolean) {
                hintFlags[hintsList.size] = EnumSet.noneOf(ParameterInfoUIContextEx.Flag::class.java)
                hintsList.add("(")
                annotations.add("")
            }

            override fun leaveTupleParameter(param: PyTupleParameter?, first: Boolean, last: Boolean) {
                hintFlags[hintsList.size] = EnumSet.noneOf(ParameterInfoUIContextEx.Flag::class.java)
                hintsList.add(if (last) ")" else "), ")
                annotations.add("")
            }

            override fun visitNamedParameter(param: PyNamedParameter, first: Boolean, last: Boolean) {
                visitNonPsiParameter(PyCallableParameterImpl.psi(param), first, last)
            }

            override fun visitSlashParameter(param: PySlashParameter, first: Boolean, last: Boolean) {
                hintFlags[hintsList.size] = EnumSet.noneOf(ParameterInfoUIContextEx.Flag::class.java)
                hintsList.add(if (last) PySlashParameter.TEXT else (PySlashParameter.TEXT + ", "))
                annotations.add("")
                currentParameterIndex[0]++
            }

            override fun visitSingleStarParameter(param: PySingleStarParameter?, first: Boolean, last: Boolean) {
                hintFlags[hintsList.size] = EnumSet.noneOf(ParameterInfoUIContextEx.Flag::class.java)
                hintsList.add(if (last) PySingleStarParameter.TEXT else (PySingleStarParameter.TEXT + ", "))
                annotations.add("")
                currentParameterIndex[0]++
            }

            override fun visitNonPsiParameter(parameter: PyCallableParameter, first: Boolean, last: Boolean) {
                indexToNamedParameter[currentParameterIndex[0]] = parameter
                val stringBuilder = StringBuilder()
                var annotationAdded = false
                if (parameter.parameter is PyNamedParameter) {
                    val annotation = (parameter.parameter as PyNamedParameter).annotationValue
                    if (annotation != null) {
                        val annotationText = ParamHelper.getNameInSignature(parameter) + ": " + annotation.replace("\n", "").replace("\\s+".toRegex(), " ")
                        annotations.add(if (last) annotationText else "$annotationText, ")
                        annotationAdded = true
                    }
                }
                if (!annotationAdded) {
                    annotations.add("")
                }
                val presentableText = getPresentableText(parameter, context) { type: PyType? -> type == null || type is PyStructuralType }
                stringBuilder.append(presentableText)
                if (!last) {
                    stringBuilder.append(", ")
                }
                val hintIndex = hintsList.size
                parameterToHintIndex[parameter] = hintIndex
                hintFlags[hintIndex] = EnumSet.noneOf(ParameterInfoUIContextEx.Flag::class.java)
                hintsList.add(stringBuilder.toString())
                currentParameterIndex[0]++
            }

            // Copied from PyCallableParameter#getPresentableText(Boolean, TypeEvalContext, Predicate) as with 261.2 they changed the implementation to Kotlin which makes it byte-incompatible due to
            // the lambda expression.
            private fun getPresentableText(callableParameter: PyCallableParameter, context: TypeEvalContext, typeFilter: (PyType?) -> Boolean): String {
                if (callableParameter.parameter is PyNamedParameter || callableParameter.parameter == null) {
                    return buildString {
                        append(ParamHelper.getNameInSignature(callableParameter))

                        var renderedAsTyped = false
                        val argumentType: PyType? = callableParameter.getArgumentType(context)
                        if (!typeFilter(argumentType)) {
                            append(": ")
                            append(PythonDocumentationProvider.getTypeName(argumentType, context))
                            renderedAsTyped = true
                        }
                        append(ParamHelper.getDefaultValuePartInSignature(callableParameter.defaultValueText, renderedAsTyped) ?: "")
                    }
                }

                return PyUtil.getReadableRepr(callableParameter.parameter, false)
            }
        })
        return Pair<MutableList<String>, MutableList<String>>(hintsList, annotations)
    }

    fun highlightParameters(arguments: MutableList<RobotArgument>,
                            callableType: PyCallableType,
                            parameters: MutableList<PyCallableParameter>,
                            indexToNamedParameter: MutableMap<Int, PyCallableParameter>,
                            parameterToHintIndex: MutableMap<PyCallableParameter, Int>,
                            hintFlags: MutableMap<Int, EnumSet<ParameterInfoUIContextEx.Flag>>,
                            currentParamIndex: Int) { // gray out enough first parameters as implicit (self, cls, ...)
        for (i in 0..<callableType.implicitOffset) {
            if (indexToNamedParameter.containsKey(i)) {
                val parameter = indexToNamedParameter[i]
                hintFlags[parameterToHintIndex[parameter]]!!.add(ParameterInfoUIContextEx.Flag.DISABLE) // show but mark as absent
            }
        }

        collectHighlights(callableType, parameters, parameterToHintIndex, hintFlags, arguments, currentParamIndex)
    }

    /**
     * match params to available args, highlight current param(s)
     */
    fun collectHighlights(callableType: PyCallableType,
                          parameterList: MutableList<PyCallableParameter>,
                          parameterHintToIndex: MutableMap<PyCallableParameter, Int>,
                          hintFlags: MutableMap<Int, EnumSet<ParameterInfoUIContextEx.Flag>>,
                          args: MutableList<RobotArgument>,
                          currentParamIndex: Int) {
        val nameToCallableParameterIndex = parameterList.stream()
            .filter { param: PyCallableParameter? -> param!!.name != null }
            .collect(Collectors.toMap(Function { obj: PyCallableParameter? -> obj!!.name }, Function.identity<PyCallableParameter?>()))

        val implicitOffset = callableType.implicitOffset
        val positionalContainerIndex = findPositionalContainerIndex(parameterList)
        var parameterFound = false
        val callable: PyCallable = checkNotNull(callableType.callable)
        val parameterNameCollator = RobotOptionsProvider.getInstance(callable.project).getParameterNameCollator()

        for (i in args.indices) {
            val arg = args[i]
            val mustHighlight = currentParamIndex == i
            if (arg is RobotParameter) {
                parameterFound = true

                val parameterName = arg.getParameterName()
                if (nameToCallableParameterIndex.containsKey(parameterName)) {
                    val pyCallableParameter: PyCallableParameter = nameToCallableParameterIndex[parameterName]!!
                    highlightParameter(pyCallableParameter, parameterHintToIndex, hintFlags, mustHighlight)
                } else {
                    nameToCallableParameterIndex.keys.stream()
                        .filter { name: String? -> parameterNameCollator.equals(name, parameterName) }
                        .findFirst()
                        .map<PyCallableParameter?>(Function { key: String? -> nameToCallableParameterIndex[key] })
                        .ifPresentOrElse(Consumer { callableParameter: PyCallableParameter? ->
                            highlightParameter(callableParameter!!, parameterHintToIndex, hintFlags, mustHighlight)
                        }) {
                            val lastParameterIndex = parameterList.size - 1
                            val lastCallableParameter = parameterList[lastParameterIndex]
                            if (lastCallableParameter.isKeywordContainer) {
                                highlightParameter(lastCallableParameter, parameterHintToIndex, hintFlags, mustHighlight)
                            }
                        }
                }
            } else if (!parameterFound) {
                val argIndex = Math.clamp((i + implicitOffset).toLong(), 0, positionalContainerIndex)
                val pyCallableParameter = parameterList[argIndex]
                highlightParameter(pyCallableParameter, parameterHintToIndex, hintFlags, mustHighlight)
            }
        }
    }

    /**
     * match params to available args, highlight current param(s)
     */
    fun highlightParameters(parameterList: List<DefinedParameter>,
                            parameterHintToIndex: MutableMap<DefinedParameter, Int>,
                            hintFlags: MutableMap<Int, EnumSet<ParameterInfoUIContextEx.Flag>>,
                            args: MutableList<RobotArgument>,
                            currentParamIndex: Int) {
        val nameToCallableParameterIndex = parameterList.stream()
            .filter { param: DefinedParameter? -> param!!.getLookup() != null }
            .collect(Collectors.toMap(Function { obj: DefinedParameter? -> obj!!.getLookup() }, Function.identity<DefinedParameter?>()))

        var parameterFound = false
        for (i in args.indices) {
            val arg = args[i]
            val mustHighlight = currentParamIndex == i
            if (arg is RobotParameter) {
                parameterFound = true

                val parameterName = arg.getParameterName()
                if (nameToCallableParameterIndex.containsKey(parameterName)) {
                    val pyCallableParameter: DefinedParameter = nameToCallableParameterIndex[parameterName]!!
                    highlightParameter(pyCallableParameter, parameterHintToIndex, hintFlags, mustHighlight)
                }
            } else {
                if (!parameterFound) {
                    val pyCallableParameter = parameterList[i]
                    highlightParameter(pyCallableParameter, parameterHintToIndex, hintFlags, mustHighlight)
                }
            }
        }
    }

    private fun findPositionalContainerIndex(parameterList: MutableList<PyCallableParameter>): Int {
        for (i in parameterList.indices) {
            val parameter = parameterList[i]
            if (parameter.isPositionalContainer) {
                return i
            }
        }
        return -1
    }

    private fun highlightParameter(parameter: PyCallableParameter,
                                   parameterToHintIndex: MutableMap<PyCallableParameter, Int>,
                                   hintFlags: MutableMap<Int, EnumSet<ParameterInfoUIContextEx.Flag>>,
                                   mustHighlight: Boolean) {
        val hintIndex = parameterToHintIndex[parameter]
        if (mustHighlight && hintIndex != null && hintFlags.containsKey(hintIndex)) {
            hintFlags[hintIndex]!!.add(ParameterInfoUIContextEx.Flag.HIGHLIGHT)
        }
    }

    private fun highlightParameter(parameter: DefinedParameter,
                                   parameterToHintIndex: MutableMap<DefinedParameter, Int>,
                                   hintFlags: MutableMap<Int, EnumSet<ParameterInfoUIContextEx.Flag>>,
                                   mustHighlight: Boolean) {
        val hintIndex = parameterToHintIndex[parameter]
        if (mustHighlight && hintIndex != null && hintFlags.containsKey(hintIndex)) {
            hintFlags[hintIndex]!!.add(ParameterInfoUIContextEx.Flag.HIGHLIGHT)
        }
    }
}
