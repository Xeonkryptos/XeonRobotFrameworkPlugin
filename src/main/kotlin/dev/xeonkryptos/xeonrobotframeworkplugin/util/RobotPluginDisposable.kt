package dev.xeonkryptos.xeonrobotframeworkplugin.util

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project

@Service(value = [Service.Level.APP, Service.Level.PROJECT])
class RobotPluginDisposable : Disposable {

    fun getInstance(): Disposable? {
        return ApplicationManager.getApplication().getService(RobotPluginDisposable::class.java)
    }

    fun getInstance(project: Project): Disposable? {
        return project.getService(RobotPluginDisposable::class.java)
    }

    override fun dispose() {}
}
