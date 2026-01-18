package com.criticalrange.hytaledev.source

import com.criticalrange.hytaledev.util.HytaleConstants
import com.intellij.codeInsight.AttachSourcesProvider
import com.intellij.openapi.roots.LibraryOrderEntry
import com.intellij.openapi.util.ActionCallback
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile

/**
 * Provides "Attach Sources" action for Hytale API classes.
 * When viewing decompiled Hytale classes, this provider offers
 * to generate better decompiled sources.
 */
class HytaleAttachSourcesProvider : AttachSourcesProvider {

    override fun getActions(
        orderEntries: List<LibraryOrderEntry>,
        psiFile: PsiFile
    ): Collection<AttachSourcesProvider.AttachSourcesAction> {
        // Only provide actions for Java files
        if (psiFile !is PsiJavaFile) {
            return emptyList()
        }

        // Check if this is a Hytale API class
        val packageName = psiFile.packageName
        if (!packageName.startsWith(HytaleConstants.HYTALE_PACKAGE)) {
            return emptyList()
        }

        // Check if any of the order entries is the Hytale library
        val hasHytaleLibrary = orderEntries.any { entry ->
            entry.libraryName?.contains("Hytale") == true ||
                entry.library?.getFiles(com.intellij.openapi.roots.OrderRootType.CLASSES)?.any {
                    it.path.contains(HytaleConstants.HYTALE_SERVER_JAR_NAME)
                } == true
        }

        if (!hasHytaleLibrary) {
            return emptyList()
        }

        // Return action to view decompiled source
        return listOf(ViewDecompiledSourceAction())
    }

    /**
     * Action to view decompiled Hytale source.
     * Currently a placeholder - IntelliJ's built-in decompiler handles this.
     */
    private class ViewDecompiledSourceAction : AttachSourcesProvider.AttachSourcesAction {

        override fun getName(): String = "View Decompiled Source (Hytale)"

        override fun getBusyText(): String = "Decompiling Hytale class..."

        override fun perform(orderEntriesContainingFile: List<LibraryOrderEntry>): ActionCallback {
            // IntelliJ's built-in Fernflower decompiler automatically handles
            // decompilation when you navigate to a class without sources.
            // This action is a placeholder for future enhanced decompilation support.
            return ActionCallback.DONE
        }
    }
}
