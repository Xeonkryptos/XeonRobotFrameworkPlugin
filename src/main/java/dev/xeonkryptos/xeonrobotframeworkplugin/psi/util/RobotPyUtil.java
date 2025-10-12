package dev.xeonkryptos.xeonrobotframeworkplugin.psi.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider.Result;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.jetbrains.python.psi.PyAssignmentStatement;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyDecorator;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import com.jetbrains.python.sdk.PythonSdkUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public final class RobotPyUtil {

    private static final Key<CachedValue<Boolean>> SYSTEM_PSI_FILE_KEY = new Key<>("ROBOT_PYTHON_SYSTEM_FILE_CACHE");

    @SuppressWarnings("UnstableApiUsage")
    public static Optional<String> getPythonKeywordName(@NotNull PyFunction pyFunction) {
        return findCustomKeywordNameDecoratorExpression(pyFunction).map(PyStringLiteralExpression::getStringValue)
                                                                   .or(() -> pyFunction.findAttributes()
                                                                                       .stream()
                                                                                       .filter(assignment -> "robot_name".equals(assignment.getName()))
                                                                                       .map(PyAssignmentStatement::getAssignedValue)
                                                                                       .filter(Objects::nonNull)
                                                                                       .map(value -> ((PyStringLiteralExpression) value).getStringValue())
                                                                                       .findAny())
                                                                   .or(() -> Optional.ofNullable(pyFunction.getName())
                                                                                     .map(functionName -> KeywordUtil.getInstance(pyFunction.getProject())
                                                                                                                     .functionToKeyword(functionName)));
    }

    @SuppressWarnings("UnstableApiUsage")
    public static Optional<PyStringLiteralExpression> findCustomKeywordNameDecoratorExpression(@NotNull PyFunction pyFunction) {
        return findCustomKeywordDecorator(pyFunction).map(decorator -> decorator.getArgument(0, "name", PyStringLiteralExpression.class));
    }

    public static Optional<PyDecorator> findCustomKeywordDecorator(@NotNull PyFunction pyFunction) {
        return findDecorator(pyFunction, "keyword");
    }

    public static Optional<PyDecorator> findDecorator(@NotNull PyFunction pyFunction, String decoratorName) {
        return Optional.ofNullable(pyFunction.getDecoratorList()).map(decoratorList -> decoratorList.findDecorator(decoratorName));
    }

    public static boolean isSystemLibrary(PyClass pyClass) {
        return isSystemLibrary(pyClass.getContainingFile());
    }

    public static boolean isSystemLibrary(PsiFile psiFile) {
        return CachedValuesManager.getCachedValue(psiFile, SYSTEM_PSI_FILE_KEY, () -> {
            Module module = ModuleUtilCore.findModuleForPsiElement(psiFile);
            if (module != null) {
                Sdk sdk = PythonSdkUtil.findPythonSdk(module);
                if (sdk != null) {
                    VirtualFile[] roots = sdk.getRootProvider().getFiles(OrderRootType.CLASSES);
                    VirtualFile fileVirtual = psiFile.getVirtualFile();
                    boolean result = Arrays.stream(roots).anyMatch(root -> VfsUtilCore.isAncestor(root, fileVirtual, false));
                    return Result.createSingleDependency(result, psiFile);
                }
            }
            return Result.createSingleDependency(false, PsiModificationTracker.MODIFICATION_COUNT);
        });
    }
}
