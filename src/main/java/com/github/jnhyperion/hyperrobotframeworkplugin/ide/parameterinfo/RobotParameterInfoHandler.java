package com.github.jnhyperion.hyperrobotframeworkplugin.ide.parameterinfo;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotStubTokenTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotTokenTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordInvokable;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordStatement;
import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.lang.parameterInfo.CreateParameterInfoContext;
import com.intellij.lang.parameterInfo.ParameterInfoHandler;
import com.intellij.lang.parameterInfo.ParameterInfoUIContext;
import com.intellij.lang.parameterInfo.ParameterInfoUIContextEx;
import com.intellij.lang.parameterInfo.ParameterInfoUtils;
import com.intellij.lang.parameterInfo.UpdateParameterInfoContext;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.SyntaxTraverser;
import com.intellij.util.ArrayUtilRt;
import one.util.streamex.MoreCollectors;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RobotParameterInfoHandler implements ParameterInfoHandler<KeywordStatement, PsiElement> {

    @Nullable
    @Override
    public KeywordStatement findElementForParameterInfo(@NotNull CreateParameterInfoContext context) {
        PsiFile psiFile = context.getFile();
        int offset = context.getOffset();
        KeywordStatement keywordStatement = findKeywordStatement(psiFile, offset);
        if (keywordStatement != null) {
            KeywordInvokable invokable = keywordStatement.getInvokable();
            if (invokable == null) {
                return null;
            }
            PsiReference reference = invokable.getReference();
            if (reference == null) {
                return null;
            }
            PsiElement resolvedElement = reference.resolve();
            context.setItemsToShow(new Object[] { resolvedElement });
        }
        return keywordStatement;
    }

    @Override
    public void showParameterInfo(@NotNull KeywordStatement element, @NotNull CreateParameterInfoContext context) {
        context.showHint(element, element.getTextOffset(), this);
    }

    @Nullable
    @Override
    public KeywordStatement findElementForUpdatingParameterInfo(@NotNull UpdateParameterInfoContext context) {
        PsiFile psiFile = context.getFile();
        int offset = context.getOffset();
        return findKeywordStatement(psiFile, offset);
    }

    private KeywordStatement findKeywordStatement(PsiFile psiFile, int offset) {
        KeywordStatement keywordStatement = ParameterInfoUtils.findParentOfType(psiFile, offset, KeywordStatement.class);
        if (keywordStatement == null) {
            InjectedLanguageManager injectedLanguageManager = InjectedLanguageManager.getInstance(psiFile.getProject());
            PsiElement element = psiFile.findElementAt(offset);
            if (element instanceof PsiWhiteSpace) {
                boolean firstElement = true;
                boolean newLineAssignedToStatement = false;
                do {
                    String unescapedWhitespaceText = injectedLanguageManager.getUnescapedText(element);
                    if ("...".equals(unescapedWhitespaceText)) {
                        newLineAssignedToStatement = true;
                    } else if ("\n".equals(unescapedWhitespaceText) && !firstElement) {
                        if (!newLineAssignedToStatement) {
                            break;
                        }
                        newLineAssignedToStatement = false;
                    }
                    firstElement = false;
                    element = element.getPrevSibling();
                } while (element instanceof PsiWhiteSpace);
            }
            if (element instanceof KeywordStatement found) {
                keywordStatement = found;
            }
        }
        return keywordStatement;
    }

    @Override
    public void updateParameterInfo(@NotNull KeywordStatement keywordStatement, @NotNull UpdateParameterInfoContext context) {
        int offset = context.getEditor().getCaretModel().getOffset();
        if (!keywordStatement.getTextRange().containsOffset(offset)) {
            PsiElement element = context.getFile().findElementAt(offset);
            if (element instanceof PsiWhiteSpace) {
                do {
                    element = element.getPrevSibling();
                } while (element instanceof PsiWhiteSpace);
            }
            if (element != keywordStatement) {
                context.removeHint();
                return;
            }
        }

        SyntaxTraverser<PsiElement> syntaxTraverser = SyntaxTraverser.psiTraverser(keywordStatement).expandAndSkip(Conditions.is(keywordStatement));
        int parameterIndex = ParameterInfoHandlerUtil.getCurrentParameterIndex(syntaxTraverser,
                                                                               offset,
                                                                               RobotTokenTypes.PARAMETER,
                                                                               RobotStubTokenTypes.ARGUMENT);
        parameterIndex = parameterIndex - 1;
        context.setCurrentParameter(parameterIndex);
    }

    @Override
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
            //noinspection unchecked
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
                //noinspection unchecked
                flags = new EnumSet[] { EnumSet.of(ParameterInfoUIContextEx.Flag.DISABLE) };
            }

            contextEx.setupUIComponentPresentation(hints, flags, context.getDefaultParameterColor());
        } else {
            final StringBuilder signatureBuilder = new StringBuilder();
            if (hints.length == 0) {
                signatureBuilder.append(getNoParamsMsg());
            } else {
                for (String s : hints) signatureBuilder.append(s);
            }
            context.setupUIComponentPresentation(signatureBuilder.toString(), -1, 0, false, false, false, context.getDefaultParameterColor());
        }
    }

    private static String getNoParamsMsg() {
        return CodeInsightBundle.message("parameter.info.no.parameters");
    }
}
