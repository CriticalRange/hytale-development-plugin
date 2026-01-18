package com.criticalrange.hytaledev.insight

import com.criticalrange.hytaledev.util.HytaleConstants
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiModifier
import com.intellij.psi.search.GlobalSearchScope

/**
 * Utility functions for code insight features (line markers, inspections, etc.)
 */
object InsightUtil {

    /**
     * Checks if the element is an identifier for a class that extends Hytale's plugin base classes.
     */
    fun isPluginClassIdentifier(element: PsiElement): Boolean {
        if (element !is PsiIdentifier) return false

        val parent = element.parent
        if (parent !is PsiClass) return false

        // The identifier should be the class name
        if (parent.nameIdentifier != element) return false

        return isPluginClass(parent)
    }

    /**
     * Checks if a PsiClass is a Hytale plugin class (extends PluginBase or JavaPlugin).
     */
    fun isPluginClass(psiClass: PsiClass): Boolean {
        // Skip abstract classes
        if (psiClass.hasModifierProperty(PsiModifier.ABSTRACT)) return false

        // Skip interfaces
        if (psiClass.isInterface) return false

        val project = psiClass.project
        val scope = GlobalSearchScope.allScope(project)
        val facade = JavaPsiFacade.getInstance(project)

        // Check if class extends any of the plugin base classes
        for (baseClassName in HytaleConstants.PLUGIN_BASE_CLASSES) {
            val baseClass = facade.findClass(baseClassName, scope) ?: continue
            if (psiClass.isInheritor(baseClass, true)) {
                return true
            }
        }

        return false
    }

    /**
     * Gets the PsiClass from an identifier element if it represents a plugin class.
     */
    fun getPluginClass(element: PsiElement): PsiClass? {
        if (!isPluginClassIdentifier(element)) return null
        return element.parent as? PsiClass
    }

    /**
     * Checks if a class is a Hytale event class.
     */
    fun isEventClass(psiClass: PsiClass): Boolean {
        val qualifiedName = psiClass.qualifiedName ?: return false
        return qualifiedName.startsWith("com.hypixel.hytale.server.core.event.events") ||
               qualifiedName.startsWith("com.hypixel.hytale.server.core.universe.world.events")
    }
}
