package com.criticalrange.hytaledev.platform

import com.criticalrange.hytaledev.assets.HytaleAssets
import com.criticalrange.hytaledev.facet.HytaleFacet
import com.criticalrange.hytaledev.util.HytaleConstants
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.PsiElement
import javax.swing.Icon

/**
 * Module type identifier for Hytale plugin projects.
 * This is a lightweight object that identifies Hytale modules without
 * implementing the full ModuleType interface (which requires a module builder).
 */
object HytaleModuleType {

    const val ID = "HYTALE_PLUGIN_MODULE"

    /**
     * Display name for Hytale modules.
     */
    const val NAME = "Hytale Plugin"

    /**
     * Description of Hytale module type.
     */
    const val DESCRIPTION = "Module for Hytale server plugin development"

    /**
     * Icon for Hytale modules.
     */
    val icon: Icon = HytaleAssets.HYTALE_ICON

    /**
     * Listener annotations that mark event handler methods.
     */
    val listenerAnnotations: List<String> = listOf(
        // Add Hytale event annotations when discovered
    )

    /**
     * Annotations that should be ignored by inspections.
     */
    val ignoredAnnotations: List<String> = listOf(
        // Add Hytale-specific annotations to ignore
    )

    /**
     * Checks if a PsiElement is within a Hytale module.
     */
    fun isInModule(element: PsiElement): Boolean {
        val module = ModuleUtilCore.findModuleForPsiElement(element) ?: return false
        return isHytaleModule(module)
    }

    /**
     * Checks if a module is a Hytale plugin module.
     */
    fun isHytaleModule(module: Module): Boolean {
        return HytaleFacet.isHytaleModule(module)
    }

    /**
     * Gets the main plugin class name for Hytale plugins.
     */
    val pluginBaseClass: String = HytaleConstants.PLUGIN_BASE_CLASS

    /**
     * Gets the Java plugin class name.
     */
    val javaPluginClass: String = HytaleConstants.JAVA_PLUGIN_CLASS
}
