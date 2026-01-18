package com.criticalrange.hytaledev.navigation

import com.criticalrange.hytaledev.util.HytaleConstants
import com.criticalrange.hytaledev.util.PsiUtil
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.psi.*

/**
 * Handles Ctrl+Click navigation for Hytale API classes.
 * When clicking on a Hytale API class reference, navigates to the decompiled source.
 */
class HytaleGotoDeclarationHandler : GotoDeclarationHandler {

    private val logger = Logger.getInstance(HytaleGotoDeclarationHandler::class.java)

    override fun getGotoDeclarationTargets(
        sourceElement: PsiElement?,
        offset: Int,
        editor: Editor?
    ): Array<PsiElement>? {
        if (sourceElement == null) return null

        // Get the reference at the element
        val reference = sourceElement.parent as? PsiJavaCodeReferenceElement
            ?: findReferenceAt(sourceElement)
            ?: return null

        // Resolve the reference
        val resolved = reference.resolve() ?: return null

        // Check if it's a Hytale API class/member
        val containingClass = when (resolved) {
            is PsiClass -> resolved
            is PsiMember -> resolved.containingClass
            else -> return null
        }

        if (containingClass == null) return null

        val qualifiedName = containingClass.qualifiedName ?: return null
        if (!qualifiedName.startsWith(HytaleConstants.HYTALE_PACKAGE)) {
            return null
        }

        logger.info("Navigating to Hytale API: $qualifiedName")

        // Find the navigable version (decompiled if necessary)
        val navigableElement = PsiUtil.findNavigableElement(resolved)
        return if (navigableElement != null) {
            arrayOf(navigableElement)
        } else {
            null
        }
    }

    /**
     * Finds a reference element at or near the source element.
     */
    private fun findReferenceAt(element: PsiElement): PsiJavaCodeReferenceElement? {
        // Check parent hierarchy for a reference
        var current: PsiElement? = element
        repeat(3) {
            when (current) {
                is PsiJavaCodeReferenceElement -> return current
                is PsiReferenceExpression -> return current
            }
            current = current?.parent
        }
        return null
    }

    override fun getActionText(context: DataContext): String? {
        return "Go to Hytale API Declaration"
    }
}
