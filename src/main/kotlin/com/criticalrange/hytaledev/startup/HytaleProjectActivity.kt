package com.criticalrange.hytaledev.startup

import com.criticalrange.hytaledev.library.HytaleLibraryManager
import com.criticalrange.hytaledev.util.HytaleConstants
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

/**
 * Project startup activity for Hytale Development plugin.
 * Initializes the plugin when a project is opened.
 */
class HytaleProjectActivity : ProjectActivity {

    private val logger = thisLogger()

    override suspend fun execute(project: Project) {
        logger.info("Hytale Development plugin initializing for project: ${project.name}")

        // Check if HytaleServer.jar exists
        val serverJar = HytaleConstants.findServerJar()
        if (serverJar != null) {
            logger.info("Found HytaleServer.jar at: $serverJar")

            // Try to register the library
            val libraryManager = HytaleLibraryManager.getInstance(project)
            if (!libraryManager.isLibraryRegistered()) {
                logger.info("Hytale library not yet registered, will be registered on demand")
            }

            // Show notification about detected Hytale installation
            showNotification(
                project,
                "Hytale Installation Detected",
                "Found HytaleServer.jar at:\n$serverJar",
                NotificationType.INFORMATION
            )
        } else {
            logger.info("HytaleServer.jar not found in standard locations")
            logger.info("Checked paths: ${HytaleConstants.getServerJarPaths().joinToString("\n")}")
        }
    }

    private fun showNotification(
        project: Project,
        title: String,
        content: String,
        type: NotificationType
    ) {
        try {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Hytale Development")
                .createNotification(title, content, type)
                .notify(project)
        } catch (e: Exception) {
            // Notification group might not be registered yet, just log
            logger.info("$title: $content")
        }
    }
}
