package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotFeatureFileType;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotLanguage;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.ImportType;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class RobotFileImpl extends PsiFileBase implements KeywordFile, RobotFile {

    private final FileType fileType;

    private Collection<Heading> headings;
    private Collection<DefinedVariable> robotInitVariables;

    public RobotFileImpl(FileViewProvider fileViewProvider) {
        super(fileViewProvider, RobotLanguage.INSTANCE);

        this.fileType = fileViewProvider.getFileType();
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return this.fileType;
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        this.reset();
    }

    @NotNull
    @Override
    public final Collection<DefinedVariable> getDefinedVariables() {
        Set<DefinedVariable> results = new LinkedHashSet<>();
        for (Heading heading : collectHeadings()) {
            results.addAll(heading.getDefinedVariables());
        }
//        results.addAll(collectRobotInitVariables());
        return results;
    }

    @NotNull
    public final Collection<DefinedVariable> collectRobotInitVariables() {
        Collection<DefinedVariable> results = this.robotInitVariables;
        if (results == null) {
            try {
                results = new LinkedHashSet<>();
                PsiFile[] psiFiles = FilenameIndex.getFilesByName(this.getProject(),
                                                                  "__init__.robot",
                                                                  GlobalSearchScope.getScopeRestrictedByFileTypes(GlobalSearchScope.projectScope(this.getProject()),
                                                                                                                  RobotFeatureFileType.getInstance()));
                for (PsiFile psiFile : psiFiles) {
                    if (psiFile instanceof RobotFile) {
                        results.addAll(((RobotFile) psiFile).getDefinedVariables());
                    }
                }
            } catch (Throwable t) {
                return new LinkedHashSet<>();
            }
            this.robotInitVariables = results;
        }
        return results;
    }

    @NotNull
    @Override
    public final ImportType getImportType() {
        return ImportType.RESOURCE;
    }

    @NotNull
    @Override
    public final Collection<DefinedKeyword> getDefinedKeywords() {
        return collectHeadings().stream().flatMap(heading -> heading.collectDefinedKeywords().stream()).collect(Collectors.toSet());
    }

    @Override
    public final void reset() {
        this.headings = null;
        this.robotInitVariables = null;
    }

    @NotNull
    @Override
    public final Collection<PsiFile> getFilesFromInvokedKeywordsAndVariables() {
        Set<PsiFile> results = new HashSet<>();
        try {
            for (Heading var3 : this.collectHeadings()) {
                results.addAll(var3.getFilesFromInvokedKeywordsAndVariables());
            }

            Collection<KeywordFile> importedFiles = this.getImportedFiles(false);
            Collection<VirtualFile> virtualFiles = this.getVirtualFiles(false);

            Set<PsiFile> psiFilesCopy = new HashSet<>(results);
            for (PsiFile psiFile : psiFilesCopy) {
                if (!virtualFiles.contains(psiFile.getVirtualFile())) {
                    for (KeywordFile importedFile : importedFiles) {
                        if (importedFile.getVirtualFiles(true).contains(psiFile.getVirtualFile())) {
                            results.add(importedFile.getPsiFile());
                            break;
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        return results;
    }

    @NotNull
    @Override
    public final Collection<KeywordFile> getImportedFiles(boolean includeTransitive) {
        Set<KeywordFile> results = new LinkedHashSet<>();
        Collection<Heading> headings = collectHeadings();
        for (Heading heading : headings) {
            for (KeywordFile keywordFile : heading.collectImportFiles()) {
                collectTransitiveKeywordFiles(results, keywordFile, includeTransitive);
            }
        }
        return results;
    }

    @NotNull
    @Override
    public final Collection<VirtualFile> getVirtualFiles(boolean includeTransitive) {
        Set<VirtualFile> files = new LinkedHashSet<>();
        for (KeywordFile keywordFile : getImportedFiles(includeTransitive)) {
            files.add(keywordFile.getVirtualFile());
        }
        return files;
    }

    @Override
    public final PsiFile getPsiFile() {
        return this;
    }

    @Override
    public final boolean isDifferentNamespace() {
        return false;
    }

    private void collectTransitiveKeywordFiles(Collection<KeywordFile> results, KeywordFile keywordFile, boolean includeTransitive) {
        if (results.add(keywordFile) && includeTransitive) {
            for (KeywordFile child : keywordFile.getImportedFiles(false)) {
                collectTransitiveKeywordFiles(results, child, true);
            }
        }
    }

    @Override
    public final void importsChanged() {
        for (Heading heading : collectHeadings()) {
            heading.importsChanged();
        }
    }

    @NotNull
    @Override
    public final Collection<KeywordInvokable> getKeywordReferences(@Nullable KeywordDefinition keyword) {
        return collectHeadings().stream().flatMap(heading -> heading.getInvokableKeywords(keyword).stream()).collect(Collectors.toList());
    }

    @NotNull
    private Collection<Heading> collectHeadings() {
        Collection<Heading> results = this.headings;
        if (results == null) {
            try {
                results = new LinkedHashSet<>();
                for (PsiElement child : getChildren()) {
                    if (child instanceof Heading) {
                        results.add((Heading) child);
                    }
                }
            } catch (Throwable var6) {
                return Collections.emptyList();
            }
            this.headings = results;
        }
        return results;
    }
}
