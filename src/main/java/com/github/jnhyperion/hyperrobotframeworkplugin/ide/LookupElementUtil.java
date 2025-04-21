package com.github.jnhyperion.hyperrobotframeworkplugin.ide;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.icons.RobotIcons;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;

public final class LookupElementUtil {

    private LookupElementUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static LookupElementBuilder addReferenceType(PsiElement element, LookupElementBuilder builder) {
        VirtualFile virtualFile = element.getContainingFile().getVirtualFile();
        if (virtualFile != null) {
            String fileName = virtualFile.getName();
            if (fileName.endsWith(".resource")) {
                builder = builder.withTypeText(getBaseName(fileName), RobotIcons.RESOURCE, true);
            } else if (fileName.endsWith(".robot")) {
                builder = builder.withTypeText(getBaseName(fileName), RobotIcons.FILE, true);
            } else if (fileName.endsWith(".py")) {
                builder = builder.withTypeText(getBaseName(fileName), RobotIcons.PYTHON, true);
            } else if (fileName.endsWith(".pyi")) {
                String directoryName = element.getContainingFile().getContainingDirectory().getName();
                builder = builder.withTypeText(directoryName, RobotIcons.PYTHON, true);
            } else {
                builder = builder.withTypeText(getBaseName(fileName), true);
            }
        }
        return builder;
    }

    private static String getBaseName(String fileName) {
        if (fileName == null) {
            return null;
        }
        int lastIndex = fileName.lastIndexOf('.');
        return (lastIndex == -1) ? fileName : fileName.substring(0, lastIndex);
    }
}
