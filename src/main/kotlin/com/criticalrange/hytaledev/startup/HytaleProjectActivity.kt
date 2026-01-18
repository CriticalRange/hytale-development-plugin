package com.criticalrange.hytaledev.startup

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class HytaleProjectActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        // Hytale Development plugin initialization
    }
}
