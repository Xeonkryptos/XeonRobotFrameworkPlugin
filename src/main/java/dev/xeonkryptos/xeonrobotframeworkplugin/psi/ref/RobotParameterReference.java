package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.python.psi.PyClass;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameterId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.Collator;
import java.util.Arrays;
import java.util.Objects;

public class RobotParameterReference extends PsiReferenceBase<RobotParameterId> implements PsiReference {

    public RobotParameterReference(@NotNull RobotParameterId parameter) {
        super(parameter, false);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        RobotParameterId parameterId = getElement();
        ResolveCache resolveCache = ResolveCache.getInstance(parameterId.getProject());
        return resolveCache.resolveWithCaching(this, (robotParameterReference, incompleteCode) -> {
            String parameterName = parameterId.getText();
            RobotKeywordCall keywordCall = PsiTreeUtil.getParentOfType(parameterId, RobotKeywordCall.class);
            PsiElement reference = null;
            if (keywordCall != null) {
                reference = keywordCall.findParameterReference(parameterName);
            } else {
                RobotOptionsProvider robotOptionsProvider = RobotOptionsProvider.getInstance(parameterId.getProject());
                Collator parameterNameCollator = robotOptionsProvider.getParameterNameCollator();
                reference = findParameterReferenceInImportedClass(parameterId, reference, parameterNameCollator, parameterName);
            }
            return reference;
        }, false, false);
    }

    @Nullable
    @SuppressWarnings("UnstableApiUsage")
    private static PsiElement findParameterReferenceInImportedClass(RobotParameterId parameterId,
                                                                    PsiElement reference,
                                                                    Collator parameterNameCollator,
                                                                    String parameterName) {
        RobotLibraryImportGlobalSetting importSetting = PsiTreeUtil.getParentOfType(parameterId, RobotLibraryImportGlobalSetting.class);
        if (importSetting != null) {
            PsiElement resolvedImport = importSetting.getImportedFile().getReference().resolve();
            if (resolvedImport instanceof PyClass pyClass) {
                reference = Arrays.stream(pyClass.getMethods())
                                  .filter(method -> Objects.equals(method.getName(), "__init__"))
                                  .flatMap(method -> Arrays.stream(method.getParameterList().getParameters()))
                                  .filter(parameter -> !parameter.isSelf())
                                  .filter(parameter -> parameterNameCollator.equals(parameterName, parameter.getName())
                                                       || parameter.getAsNamed() != null && parameter.getAsNamed().isKeywordContainer())
                                  .findFirst()
                                  .orElse(null);
            }
        }
        return reference;
    }
}
