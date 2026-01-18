package com.criticalrange.hytaledev.facet

import com.criticalrange.hytaledev.library.HytaleLibraryManager
import com.criticalrange.hytaledev.util.HytaleConstants
import com.intellij.facet.FacetManager
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.OrderEnumerator
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.VirtualFile
import java.util.jar.JarFile

/**
 * Detects Hytale projects on startup and adds the facet automatically.
 */
class HytaleFacetDetector : ProjectActivity {

    private val logger = thisLogger()

    override suspend fun execute(project: Project) {
        logger.info("Starting Hytale facet detection for project: ${project.name}")

        runReadAction {
            for (module in ModuleManager.getInstance(project).modules) {
                // Check if module already has Hytale facet
                if (HytaleFacet.isHytaleModule(module)) {
                    logger.info("Module ${module.name} already has Hytale facet")
                    continue
                }

                // Check if this looks like a Hytale project
                if (detectHytaleProject(module)) {
                    logger.info("Detected Hytale project in module: ${module.name}")
                    // Note: Adding facet requires write action, which we'll do separately
                }
            }
        }

        // Try to auto-detect and register the Hytale library
        val libraryManager = HytaleLibraryManager.getInstance(project)
        if (libraryManager.autoDetectAndRegister()) {
            logger.info("Hytale library auto-detected and registered")
        }
    }

    /**
     * Detects if a module is a Hytale plugin project.
     */
    private fun detectHytaleProject(module: com.intellij.openapi.module.Module): Boolean {
        // Check 1: Look for manifest.json in resources
        if (hasManifestJson(module)) {
            return true
        }

        // Check 2: Look for Hytale dependencies in module libraries
        if (hasHytaleDependency(module)) {
            return true
        }

        return false
    }

    /**
     * Checks if the module has a manifest.json file (Hytale plugin manifest).
     */
    private fun hasManifestJson(module: com.intellij.openapi.module.Module): Boolean {
        val rootManager = ModuleRootManager.getInstance(module)

        // Check source roots for manifest.json
        for (sourceRoot in rootManager.sourceRoots) {
            if (findManifestJson(sourceRoot) != null) {
                return true
            }
        }

        // Check resource roots
        for (contentRoot in rootManager.contentRoots) {
            val resourcesDir = contentRoot.findChild("src")?.findChild("main")?.findChild("resources")
            if (resourcesDir != null && findManifestJson(resourcesDir) != null) {
                return true
            }
        }

        return false
    }

    /**
     * Recursively searches for manifest.json file.
     */
    private fun findManifestJson(dir: VirtualFile): VirtualFile? {
        if (!dir.isDirectory) return null

        for (child in dir.children) {
            if (child.name == HytaleConstants.MANIFEST_FILE && !child.isDirectory) {
                return child
            }
        }
        return null
    }

    /**
     * Checks if the module has Hytale API as a dependency.
     */
    private fun hasHytaleDependency(module: com.intellij.openapi.module.Module): Boolean {
        var found = false

        OrderEnumerator.orderEntries(module).forEachLibrary { library ->
            val files = library.getFiles(com.intellij.openapi.roots.OrderRootType.CLASSES)
            for (file in files) {
                if (isHytaleLibrary(file)) {
                    found = true
                    return@forEachLibrary false // Stop iteration
                }
            }
            true // Continue iteration
        }

        return found
    }

    /**
     * Checks if a file is the HytaleServer.jar by looking for marker class.
     */
    private fun isHytaleLibrary(file: VirtualFile): Boolean {
        val path = file.path
        val jarPath = if (path.contains("!")) {
            path.substringBefore("!")
        } else if (path.endsWith(".jar")) {
            path
        } else {
            return false
        }

        return try {
            JarFile(jarPath).use { jar ->
                jar.getEntry(HytaleConstants.MARKER_CLASS_PATH) != null
            }
        } catch (e: Exception) {
            false
        }
    }
}
