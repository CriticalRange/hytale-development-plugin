package com.criticalrange.hytaledev.library

import com.criticalrange.hytaledev.util.HytaleConstants
import com.intellij.openapi.roots.libraries.LibraryPresentationProvider
import com.intellij.openapi.roots.libraries.LibraryProperties
import com.intellij.openapi.vfs.VirtualFile
import java.util.jar.JarFile

/**
 * Detects HytaleServer.jar library in projects.
 * Checks for the presence of marker class in JAR files.
 */
class HytalePresentationProvider : LibraryPresentationProvider<LibraryProperties<*>>(HYTALE_LIBRARY_KIND) {

    override fun detect(classesRoots: List<VirtualFile>): LibraryProperties<*>? {
        for (classesRoot in classesRoots) {
            // Check if this is the HytaleServer.jar by looking for marker class
            if (isHytaleServerJar(classesRoot)) {
                return null // Return null for properties, but non-null detection
            }
        }
        return null
    }

    private fun isHytaleServerJar(root: VirtualFile): Boolean {
        // Get the local file path
        val path = root.path

        // Handle JAR files (path might end with !/ for JAR roots)
        val jarPath = if (path.contains("!")) {
            path.substringBefore("!")
        } else if (path.endsWith(".jar")) {
            path
        } else {
            return false
        }

        return try {
            JarFile(jarPath).use { jar ->
                // Check for the marker class that identifies HytaleServer.jar
                jar.getEntry(HytaleConstants.MARKER_CLASS_PATH) != null
            }
        } catch (e: Exception) {
            false
        }
    }

    override fun getDescription(properties: LibraryProperties<*>): String {
        return "Hytale Server API"
    }
}
