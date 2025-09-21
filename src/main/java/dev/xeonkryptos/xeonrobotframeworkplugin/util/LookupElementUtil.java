package dev.xeonkryptos.xeonrobotframeworkplugin.util;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.pyi.PyiFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.icons.RobotIcons;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType;

public final class LookupElementUtil {

    private LookupElementUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static LookupElementBuilder addReferenceType(PsiElement element, LookupElementBuilder builder) {
        PsiFile containingFile = element.getContainingFile();
        VirtualFile virtualFile = containingFile.getVirtualFile();
        if (virtualFile != null) {
            FileType fileType = virtualFile.getFileType();
            if (fileType == RobotResourceFileType.getInstance()) {
                builder = builder.withTypeText(getBaseName(virtualFile), RobotIcons.RESOURCE, true);
            } else if (fileType == RobotFeatureFileType.getInstance()) {
                builder = builder.withTypeText(getBaseName(virtualFile), RobotIcons.FILE, true);
            } else if (containingFile instanceof PyiFile pyiFile && pyiFile.getContainingDirectory() != null) {
                String directoryName = pyiFile.getContainingDirectory().getName();
                builder = builder.withTypeText(directoryName, RobotIcons.PYTHON, true);
            } else if (containingFile instanceof PyFile) {
                builder = builder.withTypeText(getBaseName(virtualFile), RobotIcons.PYTHON, true);
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
