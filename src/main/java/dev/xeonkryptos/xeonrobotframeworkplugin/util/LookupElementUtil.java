package dev.xeonkryptos.xeonrobotframeworkplugin.util;

import dev.xeonkryptos.xeonrobotframeworkplugin.ide.icons.RobotIcons;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.PythonFileType;
import com.jetbrains.python.pyi.PyiFileType;

public final class LookupElementUtil {

    private LookupElementUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static LookupElementBuilder addReferenceType(PsiElement element, LookupElementBuilder builder) {
        VirtualFile virtualFile = element.getContainingFile().getVirtualFile();
        if (virtualFile != null) {
            FileType fileType = virtualFile.getFileType();
            if (fileType == RobotResourceFileType.getInstance()) {
                builder = builder.withTypeText(getBaseName(virtualFile), RobotIcons.RESOURCE, true);
            } else if (fileType == RobotFeatureFileType.getInstance()) {
                builder = builder.withTypeText(getBaseName(virtualFile), RobotIcons.FILE, true);
            } else if (fileType == PythonFileType.INSTANCE) {
                builder = builder.withTypeText(getBaseName(virtualFile), RobotIcons.PYTHON, true);
            } else if (fileType == PyiFileType.INSTANCE) {
                String directoryName = element.getContainingFile().getContainingDirectory().getName();
                builder = builder.withTypeText(directoryName, RobotIcons.PYTHON, true);
            } else {
                builder = builder.withTypeText(getBaseName(virtualFile), true);
            }
        }
        return builder;
    }

    private static String getBaseName(VirtualFile virtualFile) {
        if (virtualFile == null) {
            return null;
        }
        String fileName = virtualFile.getName();
        String extension = virtualFile.getExtension();
        if (extension != null) {
            return fileName.substring(0, fileName.length() - extension.length() - 1);
        }
        return fileName;
    }
}
