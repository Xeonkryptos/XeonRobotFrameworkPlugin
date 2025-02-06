package com.github.jnhyperion.hyperrobotframeworkplugin.ide;

import com.github.jnhyperion.hyperrobotframeworkplugin.MyLogger;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotFileImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref.RobotFileManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.DumbService.DumbModeListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex.SERVICE;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.jetbrains.python.PythonPluginDisposable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotListenerMgr {
    private static final AtomicBoolean isPythonFileChanged = new AtomicBoolean(false);
    private static final RobotListenerMgr INSTANCE = new RobotListenerMgr();

    private RobotListenerMgr() {
    }

    public static RobotListenerMgr getInstance() {
        return INSTANCE;
    }

    public final void initializeListeners(Project project) {
        project.getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            @Override
            public void after(@NotNull List<? extends VFileEvent> events) {
                for (VFileEvent event : events) {
                    if (!(event instanceof VFileContentChangeEvent)) {
                        VirtualFile file = event.getFile();
                        if (isRobotFile(file)) {
                            String projectBasePath = project.getBasePath();
                            String filePath = file.getPath();
                            if (projectBasePath != null && filePath.startsWith(projectBasePath)) {
                                MyLogger.logger.debug("Received event: " + file.getName() + " - " + project);
                                updateRobotFiles(project);
                                return;
                            }
                        }
                    }
                }
            }
        });
        project.getMessageBus().connect().subscribe(DumbService.DUMB_MODE, new DumbModeListener() {
            @Override
            public void exitDumbMode() {
                updateRobotFiles(project);
            }
        });
        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                Document document = event.getDocument();
                VirtualFile file;
                if ((file = FileDocumentManager.getInstance().getFile(document)) != null && file.getName().endsWith(".py")) {
                    isPythonFileChanged.set(true);
                }
            }
        }, PythonPluginDisposable.getInstance(project));
        project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {
                VirtualFile file;
                if ((file = event.getNewFile()) != null && ("robot".equals(file.getExtension()) || "resource".equals(file.getExtension()))
                    && RobotListenerMgr.isPythonFileChanged.getAndSet(false)) {
                    MyLogger.logger.debug("selectionChanged: " + file.getName());
                    updateRobotFiles(project);
                }
            }
        });
    }

    private void updateRobotFiles(Project project) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                ReadAction.nonBlocking(() -> {
                    List<VirtualFile> robotFiles = new ArrayList<>();
                    SERVICE.getInstance(project).iterateContent(file -> {
                        if (!file.isDirectory() && isRobotFile(file)) {
                            robotFiles.add(file);
                        }
                        return true;
                    });

                    for (VirtualFile file : robotFiles) {
                        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                        if (psiFile instanceof RobotFileImpl) {
                            RobotFileImpl robotFile = (RobotFileImpl) psiFile;
                            robotFile.reset();
                            robotFile.importsChanged();
                        }
                    }

                    MyLogger.logger.debug("Update robot file: " + robotFiles.size());
                    RobotFileManager.clearProjectCache(project);
                    return null;
                }).executeSynchronously();
            } catch (Throwable ignored) {
            }
        });
    }

    private boolean isRobotFile(@Nullable VirtualFile file) {
        if (file == null) {
            return false;
        }

        if (!file.isDirectory() || !(file instanceof VirtualDirectoryImpl)) {
            String extension = file.getExtension();
            return "robot".equals(extension) || "resource".equals(extension) || "py".equals(extension);
        }

        List<VirtualFile> children;
        try {
            children = ((VirtualDirectoryImpl) file).getCachedChildren();
        } catch (Throwable ignored) {
            return false;
        }

        for (VirtualFile child : children) {
            if (isRobotFile(child)) {
                return true;
            }
        }
        return false;
    }
}
