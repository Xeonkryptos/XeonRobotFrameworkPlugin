package dev.xeonkryptos.xeonrobotframeworkplugin;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.DumbService.DumbModeListener;
import com.intellij.openapi.project.Project;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.ProjectFileCache;

public class RobotListenerMgr {

    private static final RobotListenerMgr INSTANCE = new RobotListenerMgr();

    private RobotListenerMgr() {
    }

    public static RobotListenerMgr getInstance() {
        return INSTANCE;
    }

    public final void initializeListeners(Project project) {
        project.getMessageBus().connect().subscribe(DumbService.DUMB_MODE, new DumbModeListener() {
            @Override
            public void exitDumbMode() {
                // Clearing library references after re-index
                ProjectFileCache.clearProjectCache(project);
                ResolveCache resolveCache = project.getService(ResolveCache.class);
                resolveCache.clearCache(true);
            }
        });
    }
}
