package dev.xeonkryptos.xeonrobotframeworkplugin.ide;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.DumbService.DumbModeListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SimpleModificationTracker;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.jetbrains.python.PythonFileType;
import com.jetbrains.python.PythonPluginDisposable;
import dev.xeonkryptos.xeonrobotframeworkplugin.MyLogger;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotKeywordReferenceUpdater;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.ProjectFileCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RobotListenerMgr {
    private static final AtomicBoolean isPythonFileChanged = new AtomicBoolean(false);
    private static final RobotListenerMgr INSTANCE = new RobotListenerMgr();

    private final SimpleModificationTracker modificationTracker = new SimpleModificationTracker();

    private RobotListenerMgr() {
    }

    public static RobotListenerMgr getInstance() {
        return INSTANCE;
    }

    public final void initializeListeners(Project project) {
        project.getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            @Override
            public void after(@NotNull List<? extends VFileEvent> events) {
                boolean updateRobotFiles = false;
                boolean robotResourceFileFound = false;
                for (VFileEvent event : events) {
                    VirtualFile file = event.getFile();
                    if (isRobotFile(file)) {
                        String projectBasePath = project.getBasePath();
                        String filePath = file.getPath();
                        if (projectBasePath != null && filePath.startsWith(projectBasePath)) {
                            MyLogger.logger.debug("Received event: " + file.getName() + " - " + project);
                            updateRobotFiles = true;
                            robotResourceFileFound = file.getFileType() == RobotResourceFileType.getInstance();
                            break;
                        }
                    }
                }
                if (updateRobotFiles) {
                    updateRobotFiles(project, robotResourceFileFound);
                }
            }
        });
        project.getMessageBus().connect().subscribe(DumbService.DUMB_MODE, new DumbModeListener() {
            @Override
            public void exitDumbMode() {
                // Clearing library references after re-index
                ProjectFileCache.clearProjectCache(project);
                updateRobotFiles(project, false);
            }
        });
        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                Document document = event.getDocument();
                VirtualFile file = FileDocumentManager.getInstance().getFile(document);
                if (file != null && file.getFileType() == PythonFileType.INSTANCE) {
                    isPythonFileChanged.set(true);
                } else if (file != null && file.getFileType() == RobotResourceFileType.getInstance()) {
                    updateRobotFiles(project, true);
                }
            }
        }, PythonPluginDisposable.getInstance(project));
        project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {
                VirtualFile file = event.getNewFile();
                if (file != null && (file.getFileType() == RobotFeatureFileType.getInstance() || file.getFileType() == RobotResourceFileType.getInstance())
                    && RobotListenerMgr.isPythonFileChanged.getAndSet(false)) {
                    MyLogger.logger.debug("selectionChanged: " + file.getName());
                    updateRobotFiles(project, false);
                }
            }
        });
        PsiManager.getInstance(project).addPsiTreeChangeListener(new RobotKeywordReferenceUpdater(), PythonPluginDisposable.getInstance(project));
    }

    private void updateRobotFiles(Project project, boolean importUpdate) {
        modificationTracker.incModificationCount();
        final long currentModificationCount = modificationTracker.getModificationCount();
        ReadAction.nonBlocking(() -> {
                      ProgressManager.checkCanceled();
                      GlobalSearchScope projectScope = GlobalSearchScope.projectScope(project);

                      Collection<VirtualFile> featureRobotFiles = FileTypeIndex.getFiles(RobotFeatureFileType.getInstance(), projectScope);
                      List<VirtualFile> robotFiles = new ArrayList<>(featureRobotFiles);

                      ProgressManager.checkCanceled();
                      Collection<VirtualFile> resourceRobotFiles = FileTypeIndex.getFiles(RobotResourceFileType.getInstance(), projectScope);
                      robotFiles.addAll(resourceRobotFiles);

                      for (VirtualFile file : robotFiles) {
                          ProgressManager.checkCanceled();
                          PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                          if (psiFile instanceof RobotFile robotFile) {
                              robotFile.reset();
                          }
                      }
                      if (importUpdate) {
                          ProgressManager.checkCanceled();
                          DaemonCodeAnalyzer.getInstance(project).restart();
                      }

                      MyLogger.logger.debug("Update robot file: " + robotFiles.size());
                      return null;
                  })
                  .inSmartMode(project)
                  .coalesceBy(modificationTracker)
                  .expireWhen(() -> currentModificationCount != modificationTracker.getModificationCount())
                  .submit(AppExecutorUtil.getAppExecutorService());
    }

    private boolean isRobotFile(@Nullable VirtualFile file) {
        if (file == null) {
            return false;
        }

        if (!file.isDirectory() || !(file instanceof VirtualDirectoryImpl)) {
            FileType fileType = file.getFileType();
            return fileType == RobotFeatureFileType.getInstance() || fileType == RobotResourceFileType.getInstance() || fileType == PythonFileType.INSTANCE;
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
