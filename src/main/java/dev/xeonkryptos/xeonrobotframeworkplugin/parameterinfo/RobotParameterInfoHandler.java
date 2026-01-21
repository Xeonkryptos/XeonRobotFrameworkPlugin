package dev.xeonkryptos.xeonrobotframeworkplugin.parameterinfo;

import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.lang.parameterInfo.CreateParameterInfoContext;
import com.intellij.lang.parameterInfo.ParameterInfoHandler;
import com.intellij.lang.parameterInfo.ParameterInfoUIContext;
import com.intellij.lang.parameterInfo.ParameterInfoUIContextEx;
import com.intellij.lang.parameterInfo.ParameterInfoUtils;
import com.intellij.lang.parameterInfo.UpdateParameterInfoContext;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.SyntaxTraverser;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtilRt;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateArguments;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil;
import one.util.streamex.MoreCollectors;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RobotParameterInfoHandler implements ParameterInfoHandler<RobotKeywordCall, PsiElement> {

    @Nullable
    @Override
    public RobotKeywordCall findElementForParameterInfo(@NotNull CreateParameterInfoContext context) {
        PsiFile psiFile = context.getFile();
        int offset = context.getOffset();
        RobotKeywordCall keywordCall = findKeywordCall(psiFile, offset);
        if (keywordCall != null) {
            RobotKeywordCallName keywordCallName = keywordCall.getKeywordCallName();
            PsiReference reference = keywordCallName.getReference();
            PsiElement resolvedElement = reference.resolve();
            context.setItemsToShow(new Object[] { resolvedElement });
        }
        return keywordCall;
    }

    @Override
    public void showParameterInfo(@NotNull RobotKeywordCall element, @NotNull CreateParameterInfoContext context) {
        int offset = element.getTextOffset();
        if (!element.getTextRange().containsOffset(offset)) {
            offset = context.getOffset();
        }
        context.showHint(element, offset, this);
    }

    @Nullable
    @Override
    public RobotKeywordCall findElementForUpdatingParameterInfo(@NotNull UpdateParameterInfoContext context) {
        PsiFile psiFile = context.getFile();
        int offset = context.getOffset();
        return findKeywordCall(psiFile, offset);
    }

    private RobotKeywordCall findKeywordCall(PsiFile psiFile, int offset) {
        RobotKeywordCall keywordCall = ParameterInfoUtils.findParentOfTypeWithStopElements(psiFile,
                                                                                           offset,
                                                                                           RobotKeywordCall.class,
                                                                                           RobotTestCaseStatement.class,
                                                                                           RobotTaskStatement.class,
                                                                                           RobotUserKeywordStatement.class,
                                                                                           RobotSection.class);
        if (keywordCall == null) {
            PsiElement element = psiFile.findElementAt(offset);
            if (element instanceof PsiWhiteSpace || element instanceof PsiComment) {
                do {
                    int textOffset = element.getTextOffset();
                    element = psiFile.findElementAt(textOffset - 1);
                } while (element instanceof PsiWhiteSpace || element instanceof PsiComment);
            }
            if (element instanceof RobotKeywordCall found) {
                keywordCall = found;
            } else {
                RobotElement robotElement = PsiTreeUtil.getParentOfType(element, false, RobotTemplateArguments.class, RobotKeywordCall.class);
                if (robotElement != null) {
                    if (robotElement instanceof RobotKeywordCall found) {
                        keywordCall = found;
                    } else if (robotElement instanceof RobotTemplateArguments) {
                        keywordCall = KeywordUtil.findTemplateKeywordCall(robotElement);
                    }
                }
            }
        }
        return keywordCall;
    }

    @Override
    public void updateParameterInfo(@NotNull RobotKeywordCall keywordCall, @NotNull UpdateParameterInfoContext context) {
        int offset = context.getEditor().getCaretModel().getOffset();
        PsiElement traverserElement = keywordCall;
        if (!keywordCall.getTextRange().containsOffset(offset)) {
            traverserElement = findKeywordCall(context.getFile(), offset);
            if (traverserElement != keywordCall) {
                context.removeHint();
                return;
            }
        }

        SyntaxTraverser<PsiElement> syntaxTraverser = SyntaxTraverser.psiTraverser(traverserElement).expandAndSkip(Conditions.is(traverserElement));
        int parameterIndex = ParameterInfoHandlerUtil.getCurrentParameterIndex(syntaxTraverser, offset);
        parameterIndex = parameterIndex - 1;
        context.setCurrentParameter(parameterIndex);
        // Need to reassign the owner because the traverserElement can be a RobotTemplateArguments
        context.setParameterOwner(traverserElement);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void updateUI(PsiElement callingFunction, @NotNull ParameterInfoUIContext context) {
        final int currentParamIndex = context.getCurrentParameterIndex();
        // formatting of hints: hint index -> flags. this includes flags for parens.
        final Map<Integer, EnumSet<ParameterInfoUIContextEx.Flag>> hintFlags = new HashMap<>();

        final Pair<List<String>, List<String>> hintsAndAnnotations = ParameterInfoHandlerUtil.buildHintsAndAnnotationsWithHighlights(callingFunction,
                                                                                                                                     context,
                                                                                                                                     currentParamIndex,
                                                                                                                                     hintFlags);
        if (hintsAndAnnotations == null) {
            return;
        }

        String[] hints = ArrayUtilRt.toStringArray(hintsAndAnnotations.first);
        String[] annotations = ArrayUtilRt.toStringArray(hintsAndAnnotations.second);
        if (context instanceof ParameterInfoUIContextEx contextEx) {
            EnumSet<ParameterInfoUIContextEx.Flag>[] flags = new EnumSet[hintFlags.size()];
            for (int i = 0; i < flags.length; i++) {
                EnumSet<ParameterInfoUIContextEx.Flag> curFlags = hintFlags.get(i);
                if (!curFlags.contains(ParameterInfoUIContextEx.Flag.HIGHLIGHT) && i < hints.length && i < annotations.length) {
                    String annotation = annotations[i];
                    if (!annotation.isEmpty() && annotation.length() < hints[i].length()) {
                        hints[i] = annotation;
                    }
                }
                flags[i] = StreamEx.of(hintFlags.get(i)).collect(MoreCollectors.toEnumSet(ParameterInfoUIContextEx.Flag.class));
            }
            if (hints.length == 0) {
                hints = new String[] { getNoParamsMsg() };
                flags = new EnumSet[] { EnumSet.of(ParameterInfoUIContextEx.Flag.DISABLE) };
            }

            contextEx.setupUIComponentPresentation(hints, flags, context.getDefaultParameterColor());
        } else {
            final StringBuilder signatureBuilder = new StringBuilder();
            if (hints.length == 0) {
                signatureBuilder.append(getNoParamsMsg());
            } else {
                for (String s : hints) {
                    signatureBuilder.append(s);
                }
            }
            context.setupUIComponentPresentation(signatureBuilder.toString(), -1, 0, false, false, false, context.getDefaultParameterColor());
        }
    }

    private static String getNoParamsMsg() {
        return CodeInsightBundle.message("parameter.info.no.parameters");
    }
}
