package com.criticalrange.hytaledev.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.criticalrange.hytaledev.HytaleBundle

@Service(Service.Level.PROJECT)
class HytaleProjectService(project: Project) {

    init {
        thisLogger().info(HytaleBundle.message("projectService", project.name))
    }
}
