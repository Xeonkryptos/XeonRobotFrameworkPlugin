package dev.xeonkryptos.xeonrobotframeworkplugin.ide;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.DumbService.DumbModeListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.jetbrains.python.PythonFileType;
import com.jetbrains.python.PythonPluginDisposable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotKeywordReferenceUpdater;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.ProjectFileCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RobotListenerMgr {

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
                    VirtualFile file = event.getFile();
                    if (isRobotFile(file)) {
                        String projectBasePath = project.getBasePath();
                        String filePath = file.getPath();
                        if (projectBasePath != null && filePath.startsWith(projectBasePath) && file.getFileType() == RobotResourceFileType.getInstance()) {
                            DaemonCodeAnalyzer.getInstance(project).restart();
                            break;
                        }
                    }
                }
            }
        });
        project.getMessageBus().connect().subscribe(DumbService.DUMB_MODE, new DumbModeListener() {
            @Override
            public void exitDumbMode() {
                // Clearing library references after re-index
                ProjectFileCache.clearProjectCache(project);
                ResolveCache resolveCache = project.getService(ResolveCache.class);
                resolveCache.clearCache(true);
                resolveCache.clearCache(false);
                DaemonCodeAnalyzer.getInstance(project).restart();
            }
        });
        PsiManager.getInstance(project).addPsiTreeChangeListener(new RobotKeywordReferenceUpdater(), PythonPluginDisposable.getInstance(project));
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
