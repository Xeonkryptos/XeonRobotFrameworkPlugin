package dev.xeonkryptos.xeonrobotframeworkplugin.ide.parameterinfo;

import com.intellij.lang.parameterInfo.ParameterInfoUIContext;
import com.intellij.lang.parameterInfo.ParameterInfoUIContextEx;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SyntaxTraverser;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.python.psi.PyCallable;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyNamedParameter;
import com.jetbrains.python.psi.PySingleStarParameter;
import com.jetbrains.python.psi.PySlashParameter;
import com.jetbrains.python.psi.PyTupleParameter;
import com.jetbrains.python.psi.impl.ParamHelper;
import com.jetbrains.python.psi.types.PyCallableParameter;
import com.jetbrains.python.psi.types.PyCallableParameterImpl;
import com.jetbrains.python.psi.types.PyCallableType;
import com.jetbrains.python.psi.types.PyStructuralType;
import com.jetbrains.python.psi.types.TypeEvalContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import org.jetbrains.annotations.NotNull;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class ParameterInfoHandlerUtil {

    static <V> int getCurrentParameterIndex(SyntaxTraverser<V> traverser, int offset, IElementType... delimiterTypes) {
        V root = traverser.getRoot();
        int curOffset = traverser.api.rangeOf(root).getStartOffset();
        if (offset < curOffset) {
            return -1;
        }

        int index = 0;
        Set<IElementType> delimiterTypeSet = Set.of(delimiterTypes);
        for (V child : traverser) {
            curOffset += traverser.api.rangeOf(child).getLength();
            if (offset < curOffset) {
                IElementType type = traverser.api.typeOf(child);
                if (delimiterTypeSet.contains(type)) {
                    index++;
                }
                break;
            }

            IElementType type = traverser.api.typeOf(child);
            if (delimiterTypeSet.contains(type)) {
                index++;
            }
        }

        return index;
    }

    static Pair<List<String>, List<String>> buildHintsAndAnnotationsWithHighlights(PsiElement callingFunction,
                                                                                   @NotNull ParameterInfoUIContext context,
                                                                                   int currentParamIndex,
                                                                                   Map<Integer, EnumSet<ParameterInfoUIContextEx.Flag>> hintFlags) {
        RobotKeywordCall keywordCall = (RobotKeywordCall) context.getParameterOwner();
        RobotArgument[] arguments = keywordCall.getAllCallArguments().toArray(RobotArgument[]::new);

        final Pair<List<String>, List<String>> hintsAndAnnotations;
        if (callingFunction instanceof PyFunction pyFunction) {
            final Map<Integer, PyCallableParameter> indexToNamedParameter = new HashMap<>();

            // param -> hint index. indexes are not contiguous, because some hints are parentheses.
            final Map<PyCallableParameter, Integer> parameterToHintIndex = new HashMap<>();

            final TypeEvalContext typeEvalContext = TypeEvalContext.codeAnalysis(keywordCall.getProject(), pyFunction.getContainingFile());
            final List<PyCallableParameter> parameters = pyFunction.getParameters(typeEvalContext);
            final PyCallableType callableType = (PyCallableType) typeEvalContext.getType(pyFunction);
            if (callableType == null) {
                return null;
            }

            hintsAndAnnotations = ParameterInfoHandlerUtil.buildParameterListHint(parameters,
                                                                                  indexToNamedParameter,
                                                                                  parameterToHintIndex,
                                                                                  hintFlags,
                                                                                  typeEvalContext);
            ParameterInfoHandlerUtil.highlightParameters(Arrays.asList(arguments),
                                                         callableType,
                                                         parameters,
                                                         indexToNamedParameter,
                                                         parameterToHintIndex,
                                                         hintFlags,
                                                         currentParamIndex);
        } else {
            // param -> hint index. indexes are not contiguous, because some hints are parentheses.
            final Map<DefinedParameter, Integer> parameterToHintIndex = new HashMap<>();

            RobotUserKeywordStatement keyword = (RobotUserKeywordStatement) callingFunction;
            Collection<DefinedParameter> parameters = keyword.getInputParameters();
            List<String> hints = new ArrayList<>();
            List<String> annotations = new ArrayList<>();
            for (DefinedParameter parameter : parameters) {
                String hint = parameter.getLookup();
                if (parameter.hasDefaultValue()) {
                    hint += " = " + parameter.getDefaultValue();
                }
                final int hintIndex = hints.size();
                hintFlags.put(hintIndex, EnumSet.noneOf(ParameterInfoUIContextEx.Flag.class));
                parameterToHintIndex.put(parameter, hintIndex);
                hints.add(hint);
                annotations.add("");
            }
            hintsAndAnnotations = Pair.create(hints, annotations);
            ParameterInfoHandlerUtil.highlightParameters(List.copyOf(parameters), parameterToHintIndex, hintFlags, Arrays.asList(arguments), currentParamIndex);
        }

        return hintsAndAnnotations;
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
    static Pair<List<String>, List<String>> buildParameterListHint(@NotNull List<PyCallableParameter> parameters,
                                                                   @NotNull final Map<Integer, PyCallableParameter> indexToNamedParameter,
                                                                   @NotNull final Map<PyCallableParameter, Integer> parameterToHintIndex,
                                                                   @NotNull final Map<Integer, EnumSet<ParameterInfoUIContextEx.Flag>> hintFlags,
                                                                   @NotNull TypeEvalContext context) {
        final List<String> hintsList = new ArrayList<>();
        final List<String> annotations = new ArrayList<>();
        final int[] currentParameterIndex = new int[] { 0 };
        ParamHelper.walkDownParameters(parameters, new ParamHelper.ParamWalker() {
            @Override
            public void enterTupleParameter(PyTupleParameter param, boolean first, boolean last) {
                hintFlags.put(hintsList.size(), EnumSet.noneOf(ParameterInfoUIContextEx.Flag.class));
                hintsList.add("(");
                annotations.add("");
            }

            @Override
            public void leaveTupleParameter(PyTupleParameter param, boolean first, boolean last) {
                hintFlags.put(hintsList.size(), EnumSet.noneOf(ParameterInfoUIContextEx.Flag.class));
                hintsList.add(last ? ")" : "), ");
                annotations.add("");
            }

            @Override
            public void visitNamedParameter(PyNamedParameter param, boolean first, boolean last) {
                visitNonPsiParameter(PyCallableParameterImpl.psi(param), first, last);
            }

            @Override
            public void visitSlashParameter(@NotNull PySlashParameter param, boolean first, boolean last) {
                hintFlags.put(hintsList.size(), EnumSet.noneOf(ParameterInfoUIContextEx.Flag.class));
                hintsList.add(last ? PySlashParameter.TEXT : (PySlashParameter.TEXT + ", "));
                annotations.add("");
                currentParameterIndex[0]++;
            }

            @Override
            public void visitSingleStarParameter(PySingleStarParameter param, boolean first, boolean last) {
                hintFlags.put(hintsList.size(), EnumSet.noneOf(ParameterInfoUIContextEx.Flag.class));
                hintsList.add(last ? PySingleStarParameter.TEXT : (PySingleStarParameter.TEXT + ", "));
                annotations.add("");
                currentParameterIndex[0]++;
            }

            @Override
            public void visitNonPsiParameter(@NotNull PyCallableParameter parameter, boolean first, boolean last) {
                indexToNamedParameter.put(currentParameterIndex[0], parameter);
                final StringBuilder stringBuilder = new StringBuilder();
                boolean annotationAdded = false;
                if (parameter.getParameter() instanceof PyNamedParameter) {
                    final String annotation = ((PyNamedParameter) parameter.getParameter()).getAnnotationValue();
                    if (annotation != null) {
                        String annotationText = ParamHelper.getNameInSignature(parameter) + ": " + annotation.replaceAll("\n", "").replaceAll("\\s+", " ");
                        annotations.add(last ? annotationText : (annotationText + ", "));
                        annotationAdded = true;
                    }
                }
                if (!annotationAdded) {
                    annotations.add("");
                }
                stringBuilder.append(parameter.getPresentableText(true, context, type -> type == null || type instanceof PyStructuralType));
                if (!last) {
                    stringBuilder.append(", ");
                }
                final int hintIndex = hintsList.size();
                parameterToHintIndex.put(parameter, hintIndex);
                hintFlags.put(hintIndex, EnumSet.noneOf(ParameterInfoUIContextEx.Flag.class));
                hintsList.add(stringBuilder.toString());
                currentParameterIndex[0]++;
            }
        });
        return new Pair<>(hintsList, annotations);
    }

    static void highlightParameters(List<RobotArgument> arguments,
                                    PyCallableType callableType,
                                    List<PyCallableParameter> parameters,
                                    Map<Integer, PyCallableParameter> indexToNamedParameter,
                                    Map<PyCallableParameter, Integer> parameterToHintIndex,
                                    Map<Integer, EnumSet<ParameterInfoUIContextEx.Flag>> hintFlags,
                                    int currentParamIndex) {
        // gray out enough first parameters as implicit (self, cls, ...)
        for (int i = 0; i < callableType.getImplicitOffset(); i++) {
            if (indexToNamedParameter.containsKey(i)) {
                final PyCallableParameter parameter = indexToNamedParameter.get(i);
                hintFlags.get(parameterToHintIndex.get(parameter)).add(ParameterInfoUIContextEx.Flag.DISABLE); // show but mark as absent
            }
        }

        collectHighlights(callableType, parameters, parameterToHintIndex, hintFlags, arguments, currentParamIndex);
    }

    /**
     * match params to available args, highlight current param(s)
     */
    static void collectHighlights(@NotNull final PyCallableType callableType,
                                  @NotNull final List<PyCallableParameter> parameterList,
                                  @NotNull final Map<PyCallableParameter, Integer> parameterHintToIndex,
                                  @NotNull final Map<Integer, EnumSet<ParameterInfoUIContextEx.Flag>> hintFlags,
                                  @NotNull final List<RobotArgument> args,
                                  int currentParamIndex) {
        Map<String, PyCallableParameter> nameToCallableParameterIndex = parameterList.stream()
                                                                                     .filter(param -> param.getName() != null)
                                                                                     .collect(Collectors.toMap(PyCallableParameter::getName,
                                                                                                               Function.identity()));

        int implicitOffset = callableType.getImplicitOffset();
        int positionalContainerIndex = findPositionalContainerIndex(parameterList);
        boolean parameterFound = false;
        PyCallable callable = callableType.getCallable();
        assert callable != null;
        Collator parameterNameCollator = RobotOptionsProvider.getInstance(callable.getProject()).getParameterNameCollator();

        for (int i = 0; i < args.size(); i++) {
            RobotArgument arg = args.get(i);
            boolean mustHighlight = currentParamIndex == i;
            if (arg instanceof RobotParameter parameter) {
                parameterFound = true;

                String parameterName = parameter.getName();
                if (nameToCallableParameterIndex.containsKey(parameterName)) {
                    PyCallableParameter pyCallableParameter = nameToCallableParameterIndex.get(parameterName);
                    highlightParameter(pyCallableParameter, parameterHintToIndex, hintFlags, mustHighlight);
                } else {
                    nameToCallableParameterIndex.keySet()
                                                .stream()
                                                .filter(name -> parameterNameCollator.equals(name, parameterName))
                                                .findFirst()
                                                .map(nameToCallableParameterIndex::get)
                                                .ifPresentOrElse(callableParameter -> highlightParameter(callableParameter,
                                                                                                         parameterHintToIndex,
                                                                                                         hintFlags,
                                                                                                         mustHighlight), () -> {
                                                    int lastParameterIndex = parameterList.size() - 1;
                                                    PyCallableParameter lastCallableParameter = parameterList.get(lastParameterIndex);
                                                    if (lastCallableParameter.isKeywordContainer()) {
                                                        highlightParameter(lastCallableParameter, parameterHintToIndex, hintFlags, mustHighlight);
                                                    }
                                                });
                }
            } else if (!parameterFound) {
                int argIndex = Math.max(0, Math.min(i + implicitOffset, positionalContainerIndex));
                PyCallableParameter pyCallableParameter = parameterList.get(argIndex);
                highlightParameter(pyCallableParameter, parameterHintToIndex, hintFlags, mustHighlight);
            }
        }
    }

    /**
     * match params to available args, highlight current param(s)
     */
    static void highlightParameters(@NotNull final List<DefinedParameter> parameterList,
                                    @NotNull final Map<DefinedParameter, Integer> parameterHintToIndex,
                                    @NotNull final Map<Integer, EnumSet<ParameterInfoUIContextEx.Flag>> hintFlags,
                                    @NotNull final List<RobotArgument> args,
                                    int currentParamIndex) {
        Map<String, DefinedParameter> nameToCallableParameterIndex = parameterList.stream()
                                                                                  .filter(param -> param.getLookup() != null)
                                                                                  .collect(Collectors.toMap(DefinedParameter::getLookup, Function.identity()));

        boolean parameterFound = false;
        for (int i = 0; i < args.size(); i++) {
            RobotArgument arg = args.get(i);
            boolean mustHighlight = currentParamIndex == i;
            if (arg instanceof RobotParameter parameter) {
                parameterFound = true;

                String parameterName = parameter.getName();
                if (nameToCallableParameterIndex.containsKey(parameterName)) {
                    DefinedParameter pyCallableParameter = nameToCallableParameterIndex.get(parameterName);
                    highlightParameter(pyCallableParameter, parameterHintToIndex, hintFlags, mustHighlight);
                }
            } else {
                if (!parameterFound) {
                    DefinedParameter pyCallableParameter = parameterList.get(i);
                    highlightParameter(pyCallableParameter, parameterHintToIndex, hintFlags, mustHighlight);
                }
            }
        }
    }

    private static int findPositionalContainerIndex(@NotNull List<PyCallableParameter> parameterList) {
        for (int i = 0; i < parameterList.size(); i++) {
            PyCallableParameter parameter = parameterList.get(i);
            if (parameter.isPositionalContainer()) {
                return i;
            }
        }
        return -1;
    }

    private static void highlightParameter(@NotNull final PyCallableParameter parameter,
                                           @NotNull final Map<PyCallableParameter, Integer> parameterToHintIndex,
                                           @NotNull final Map<Integer, EnumSet<ParameterInfoUIContextEx.Flag>> hintFlags,
                                           final boolean mustHighlight) {
        final Integer hintIndex = parameterToHintIndex.get(parameter);
        if (mustHighlight && hintIndex != null && hintFlags.containsKey(hintIndex)) {
            hintFlags.get(hintIndex).add(ParameterInfoUIContextEx.Flag.HIGHLIGHT);
        }
    }

    private static void highlightParameter(@NotNull final DefinedParameter parameter,
                                           @NotNull final Map<DefinedParameter, Integer> parameterToHintIndex,
                                           @NotNull final Map<Integer, EnumSet<ParameterInfoUIContextEx.Flag>> hintFlags,
                                           final boolean mustHighlight) {
        final Integer hintIndex = parameterToHintIndex.get(parameter);
        if (mustHighlight && hintIndex != null && hintFlags.containsKey(hintIndex)) {
            hintFlags.get(hintIndex).add(ParameterInfoUIContextEx.Flag.HIGHLIGHT);
        }
    }
}
