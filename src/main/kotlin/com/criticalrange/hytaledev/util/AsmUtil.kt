package com.criticalrange.hytaledev.util

import com.intellij.byteCodeViewer.ByteCodeViewerManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiClass
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

/**
 * ASM utilities for bytecode analysis.
 */
object AsmUtil {

    private val logger = Logger.getInstance(AsmUtil::class.java)

    /**
     * Loads a ClassNode from a PsiClass using ByteCodeViewer.
     */
    fun findClassNode(psiClass: PsiClass): ClassNode? {
        return try {
            val bytes = ByteCodeViewerManager.loadClassFileBytes(psiClass)
            if (bytes != null) {
                val node = ClassNode(Opcodes.ASM9)
                ClassReader(bytes).accept(node, 0)
                node
            } else {
                logger.debug("Could not load class bytes for: ${psiClass.qualifiedName}")
                null
            }
        } catch (e: Exception) {
            logger.warn("Error loading class node for ${psiClass.qualifiedName}", e)
            null
        }
    }

    /**
     * Checks if a class is a Hytale API class based on its package.
     */
    fun isHytaleApiClass(psiClass: PsiClass): Boolean {
        val qualifiedName = psiClass.qualifiedName ?: return false
        return qualifiedName.startsWith(HytaleConstants.HYTALE_PACKAGE)
    }

    /**
     * Checks if a class extends Hytale's PluginBase.
     */
    fun isPluginClass(psiClass: PsiClass): Boolean {
        var current: PsiClass? = psiClass
        while (current != null) {
            val qualifiedName = current.qualifiedName
            if (qualifiedName == HytaleConstants.PLUGIN_BASE_CLASS ||
                qualifiedName == HytaleConstants.JAVA_PLUGIN_CLASS) {
                return true
            }
            current = current.superClass
        }
        return false
    }
}
