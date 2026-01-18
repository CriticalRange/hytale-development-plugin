package com.criticalrange.hytaledev.assets

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

/**
 * Icon assets for the Hytale Development plugin.
 */
object HytaleAssets {

    /**
     * Main Hytale icon (16x16).
     */
    val HYTALE_ICON: Icon = IconLoader.getIcon("/icons/hytale.svg", HytaleAssets::class.java)

    /**
     * Hytale plugin icon (16x16).
     */
    val PLUGIN_ICON: Icon = IconLoader.getIcon("/icons/hytale-plugin.svg", HytaleAssets::class.java)

    /**
     * Manifest file icon (16x16).
     */
    val MANIFEST_ICON: Icon = IconLoader.getIcon("/icons/manifest.svg", HytaleAssets::class.java)

    /**
     * Event listener icon (16x16).
     */
    val LISTENER_ICON: Icon = IconLoader.getIcon("/icons/listener.svg", HytaleAssets::class.java)
}
