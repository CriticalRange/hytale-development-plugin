package com.criticalrange.hytaledev.util

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Constants and paths for Hytale development.
 */
object HytaleConstants {

    // Hytale API package prefixes
    const val HYTALE_PACKAGE = "com.hypixel.hytale"
    const val HYTALE_SERVER_PACKAGE = "com.hypixel.hytale.server"
    const val HYTALE_PLUGIN_PACKAGE = "com.hypixel.hytale.server.core.plugin"

    // Key Hytale API classes - Plugin System
    const val PLUGIN_BASE_CLASS = "com.hypixel.hytale.server.core.plugin.PluginBase"
    const val JAVA_PLUGIN_CLASS = "com.hypixel.hytale.server.core.plugin.JavaPlugin"
    const val PLUGIN_MANAGER_CLASS = "com.hypixel.hytale.server.core.plugin.PluginManager"
    const val PLUGIN_EVENT_CLASS = "com.hypixel.hytale.server.core.plugin.event.PluginEvent"
    const val CLASS_TRANSFORMER_CLASS = "com.hypixel.hytale.plugin.early.ClassTransformer"

    // Event System (ECS-style, no annotations - uses EventRegistry.registerGlobal)
    const val EVENT_REGISTRY_CLASS = "com.hypixel.hytale.event.EventRegistry"
    const val EVENT_REGISTRATION_CLASS = "com.hypixel.hytale.event.EventRegistration"

    // Common Event Classes
    const val PLAYER_EVENT_CLASS = "com.hypixel.hytale.server.core.event.events.player.PlayerEvent"
    const val PLAYER_CONNECT_EVENT = "com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent"
    const val PLAYER_DISCONNECT_EVENT = "com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent"
    const val PLAYER_READY_EVENT = "com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent"
    const val PLAYER_CHAT_EVENT = "com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent"

    // Plugin base classes for detection
    val PLUGIN_BASE_CLASSES = listOf(PLUGIN_BASE_CLASS, JAVA_PLUGIN_CLASS)

    // Marker classes for library detection (internal path format for JAR checking)
    const val MARKER_CLASS_PATH = "com/hypixel/hytale/server/core/plugin/PluginBase.class"

    // Plugin manifest
    const val MANIFEST_FILE = "manifest.json"

    // Library identification
    const val HYTALE_LIBRARY_KIND_ID = "hytale-server-library"
    const val HYTALE_SERVER_JAR_NAME = "HytaleServer.jar"

    /**
     * Gets all possible paths where HytaleServer.jar might be located.
     * Supports Flatpak installation and standard installation paths.
     */
    fun getServerJarPaths(): List<Path> {
        val userHome = System.getProperty("user.home")
        return listOf(
            // Flatpak installation (primary for Linux)
            Paths.get(userHome, ".var/app/com.hypixel.HytaleLauncher/data/Hytale/install/release/package/game/latest/Server/HytaleServer.jar"),
            // Standard Linux installation
            Paths.get(userHome, ".local/share/Hytale/install/release/package/game/latest/Server/HytaleServer.jar"),
            // Windows installation (via WSL or native)
            Paths.get(userHome, "AppData/Local/Hytale/install/release/package/game/latest/Server/HytaleServer.jar"),
            // macOS installation
            Paths.get(userHome, "Library/Application Support/Hytale/install/release/package/game/latest/Server/HytaleServer.jar"),
        )
    }

    /**
     * Finds the first existing HytaleServer.jar path.
     */
    fun findServerJar(): Path? {
        return getServerJarPaths().firstOrNull { it.toFile().exists() }
    }

    /**
     * Gets paths where user mods are located.
     */
    fun getModsPaths(): List<Path> {
        val userHome = System.getProperty("user.home")
        return listOf(
            // Flatpak
            Paths.get(userHome, ".var/app/com.hypixel.HytaleLauncher/data/Hytale/UserData/Mods"),
            Paths.get(userHome, ".var/app/com.hypixel.HytaleLauncher/data/Hytale/install/UserData/Mods"),
            // Standard Linux
            Paths.get(userHome, ".local/share/Hytale/UserData/Mods"),
            // Windows
            Paths.get(userHome, "AppData/Local/Hytale/UserData/Mods"),
            // macOS
            Paths.get(userHome, "Library/Application Support/Hytale/UserData/Mods"),
        )
    }

    /**
     * Gets paths where early plugins are located.
     */
    fun getEarlyPluginsPaths(): List<Path> {
        val userHome = System.getProperty("user.home")
        return listOf(
            // Flatpak
            Paths.get(userHome, ".var/app/com.hypixel.HytaleLauncher/data/Hytale/install/earlyplugins"),
            // Standard Linux
            Paths.get(userHome, ".local/share/Hytale/install/earlyplugins"),
            // Windows
            Paths.get(userHome, "AppData/Local/Hytale/install/earlyplugins"),
            // macOS
            Paths.get(userHome, "Library/Application Support/Hytale/install/earlyplugins"),
        )
    }
}
