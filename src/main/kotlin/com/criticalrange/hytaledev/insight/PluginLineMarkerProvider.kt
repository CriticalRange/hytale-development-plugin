package com.criticalrange.hytaledev.insight

import com.criticalrange.hytaledev.assets.HytaleAssets
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import javax.swing.Icon

/**
 * Provides line markers (gutter icons) for Hytale plugin main classes.
 * Shows the Hytale icon next to classes that extend PluginBase or JavaPlugin.
 */
class PluginLineMarkerProvider : LineMarkerProviderDescriptor() {

    override fun getName(): String = "Hytale plugin main class"

    override fun getIcon(): Icon = HytaleAssets.HYTALE_ICON

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        // Only process identifiers
        if (element !is PsiIdentifier) return null

        // Check if this is a plugin class identifier
        if (!InsightUtil.isPluginClassIdentifier(element)) return null

        val psiClass = element.parent as? PsiClass ?: return null

        return LineMarkerInfo(
            element,
            element.textRange,
            HytaleAssets.PLUGIN_ICON,
            { _: PsiIdentifier -> "Hytale Plugin: ${psiClass.name}" },
            null, // No navigation handler
            GutterIconRenderer.Alignment.LEFT,
            { "Hytale plugin indicator" }
        )
    }
}
