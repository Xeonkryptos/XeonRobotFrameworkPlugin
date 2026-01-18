package dev.xeonkryptos.xeonrobotframeworkplugin;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.Service.Level;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.DumbService.DumbModeListener;
import com.intellij.openapi.project.Project;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.util.messages.MessageBusConnection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.ProjectFileCache;

@Service(Level.PROJECT)
public final class RobotListenerMgr implements Disposable {

    private final Project project;

    private MessageBusConnection messageBusConnection;

    public RobotListenerMgr(Project project) {
        this.project = project;
    }

    public static RobotListenerMgr getInstance(Project project) {
        return project.getService(RobotListenerMgr.class);
    }

    public void initializeListeners() {
        messageBusConnection = project.getMessageBus().connect();
        messageBusConnection.subscribe(DumbService.DUMB_MODE, new DumbModeListener() {
            @Override
            public void exitDumbMode() {
                // Clearing library references after re-index
                ProjectFileCache.clearProjectCache(project);
                ResolveCache resolveCache = project.getService(ResolveCache.class);
                resolveCache.clearCache(true);
            }
        });
    }

    @Override
    public void dispose() {
        messageBusConnection.disconnect();
    }
}
