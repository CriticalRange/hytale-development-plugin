package com.criticalrange.hytaledev.library

import com.criticalrange.hytaledev.util.HytaleConstants
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.JarFileSystem
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Path

/**
 * Service for managing Hytale server library detection and registration.
 */
@Service(Service.Level.PROJECT)
class HytaleLibraryManager(private val project: Project) {

    private val logger = thisLogger()

    companion object {
        const val HYTALE_LIBRARY_NAME = "Hytale Server API"

        fun getInstance(project: Project): HytaleLibraryManager {
            return project.getService(HytaleLibraryManager::class.java)
        }
    }

    /**
     * Detects and returns the path to HytaleServer.jar if found.
     */
    fun detectServerJar(): Path? {
        val serverJar = HytaleConstants.findServerJar()
        if (serverJar != null) {
            logger.info("Found HytaleServer.jar at: $serverJar")
        } else {
            logger.info("HytaleServer.jar not found in standard locations")
        }
        return serverJar
    }

    /**
     * Checks if the Hytale library is already registered in the project.
     */
    fun isLibraryRegistered(): Boolean {
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        return libraryTable.getLibraryByName(HYTALE_LIBRARY_NAME) != null
    }

    /**
     * Gets the registered Hytale library if it exists.
     */
    fun getHytaleLibrary(): Library? {
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        return libraryTable.getLibraryByName(HYTALE_LIBRARY_NAME)
    }

    /**
     * Registers HytaleServer.jar as a project library.
     * Returns the created library, or null if registration failed.
     * Must be called from EDT or will invoke on EDT.
     */
    fun registerLibrary(serverJarPath: Path): Library? {
        if (isLibraryRegistered()) {
            logger.info("Hytale library already registered")
            return getHytaleLibrary()
        }

        val jarFile = serverJarPath.toFile()
        if (!jarFile.exists()) {
            logger.warn("HytaleServer.jar does not exist at: $serverJarPath")
            return null
        }

        var result: Library? = null

        // Use invokeAndWait to ensure we're on EDT for write action
        ApplicationManager.getApplication().invokeAndWait {
            WriteAction.run<Throwable> {
                try {
                    val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
                    val library = libraryTable.createLibrary(HYTALE_LIBRARY_NAME)
                    val modifiableModel = library.modifiableModel

                    // Add the JAR as a classes root
                    val jarVirtualFile = LocalFileSystem.getInstance().findFileByPath(jarFile.absolutePath)
                    if (jarVirtualFile != null) {
                        val jarRoot = JarFileSystem.getInstance().getJarRootForLocalFile(jarVirtualFile)
                        if (jarRoot != null) {
                            modifiableModel.addRoot(jarRoot, OrderRootType.CLASSES)
                            logger.info("Added HytaleServer.jar as library root")
                        }
                    }

                    modifiableModel.commit()
                    logger.info("Successfully registered Hytale library")
                    result = library
                } catch (e: Exception) {
                    logger.error("Failed to register Hytale library", e)
                }
            }
        }

        return result
    }

    /**
     * Checks if a module has the Hytale library as a dependency.
     */
    fun hasHytaleDependency(module: com.intellij.openapi.module.Module): Boolean {
        val rootManager = ModuleRootManager.getInstance(module)
        for (orderEntry in rootManager.orderEntries) {
            if (orderEntry is com.intellij.openapi.roots.LibraryOrderEntry) {
                val library = orderEntry.library
                if (library?.name == HYTALE_LIBRARY_NAME) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Auto-detects and registers the Hytale library if found.
     * Returns true if the library was found and registered (or already exists).
     */
    fun autoDetectAndRegister(): Boolean {
        if (isLibraryRegistered()) {
            return true
        }

        val serverJar = detectServerJar() ?: return false
        return registerLibrary(serverJar) != null
    }

    /**
     * Gets the VirtualFile for the HytaleServer.jar if registered.
     */
    fun getServerJarVirtualFile(): VirtualFile? {
        val library = getHytaleLibrary() ?: return null
        val roots = library.getFiles(OrderRootType.CLASSES)
        return roots.firstOrNull()
    }
}
