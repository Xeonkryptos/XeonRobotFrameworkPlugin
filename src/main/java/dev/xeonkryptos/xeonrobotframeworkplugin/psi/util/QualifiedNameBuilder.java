package dev.xeonkryptos.xeonrobotframeworkplugin.psi.util;

import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider.Result;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotQualifiedNameOwner;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public final class QualifiedNameBuilder {

    private static final Key<CachedValue<String>> QUALIFIED_NAME_KEY = new Key<>("qualifiedName");

    private static final Key<CachedValue<String>> QUALIFIED_PATH_KEY = new Key<>("qualifiedPath");

    private QualifiedNameBuilder() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static String computeQualifiedName(PsiNamedElement element) {
        return CachedValuesManager.getCachedValue(element, QUALIFIED_NAME_KEY, () -> {
            String qualifiedPath = computeQualifiedPath(element);
            String qualifiedName = element.getName();
            if (qualifiedPath != null) {
                qualifiedName = qualifiedPath + "." + qualifiedName;
            }
            return new Result<>(qualifiedName, PsiModificationTracker.MODIFICATION_COUNT);
        });
    }

    @Nullable
    public static String computeQualifiedPath(PsiElement element) {
        return CachedValuesManager.getCachedValue(element, QUALIFIED_PATH_KEY, () -> {
            PsiFile containingFile = element.getContainingFile();
            VirtualFile virtualFile = containingFile.getOriginalFile().getVirtualFile();
            Project project = containingFile.getProject();
            RobotQualifiedNameOwner qualifiedNameOwner = PsiTreeUtil.getParentOfType(element, RobotQualifiedNameOwner.class);
            String qualifiedName;
            if (qualifiedNameOwner != null) {
                qualifiedName = qualifiedNameOwner.getQualifiedName();
            } else {
                qualifiedName = Arrays.stream(ModuleManager.getInstance(project).getModules())
                                      .filter(module -> ModuleRootManager.getInstance(module).getFileIndex().isInContent(virtualFile))
                                      .flatMap(module -> Arrays.stream(ModuleRootManager.getInstance(module).getContentRoots()))
                                      .filter(contentRoot -> VfsUtil.isAncestor(contentRoot, virtualFile, false))
                                      .map(contentRoot -> {
                                          String extension = virtualFile.getExtension();
                                          String relativePath = VfsUtil.getRelativePath(virtualFile, contentRoot, '.');
                                          if (relativePath != null && extension != null) {
                                              return relativePath.substring(0, relativePath.length() - extension.length() - 1);
                                          }
                                          return relativePath;
                                      })
                                      .filter(Objects::nonNull)
                                      .findFirst()
                                      .orElse(null);
            }
            return new Result<>(qualifiedName, PsiModificationTracker.MODIFICATION_COUNT);
        });
    }
}
