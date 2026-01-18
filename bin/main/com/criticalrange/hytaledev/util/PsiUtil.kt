package com.criticalrange.hytaledev.util

import com.intellij.codeEditor.JavaEditorFileSwapper
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope

/**
 * PSI utilities for navigation and resolution.
 */
object PsiUtil {

    /**
     * Finds a class by its fully qualified name.
     */
    fun findClass(project: Project, qualifiedName: String, scope: GlobalSearchScope? = null): PsiClass? {
        val searchScope = scope ?: GlobalSearchScope.allScope(project)
        return JavaPsiFacade.getInstance(project).findClass(qualifiedName, searchScope)
    }

    /**
     * Finds the source or decompiled version of a class for navigation.
     * Prefers source files but falls back to decompiled if necessary.
     */
    fun findNavigableClass(psiClass: PsiClass, canDecompile: Boolean = true): PsiClass? {
        val project = psiClass.project
        val containingFile = psiClass.containingFile ?: return psiClass
        val virtualFile = containingFile.virtualFile ?: return psiClass

        // Try to find library source first
        val sourceFile = JavaEditorFileSwapper.findSourceFile(project, virtualFile)
        if (sourceFile != null) {
            val psiFile = PsiManager.getInstance(project).findFile(sourceFile)
            if (psiFile is PsiJavaFile) {
                val sourceClass = psiFile.classes.firstOrNull { it.qualifiedName == psiClass.qualifiedName }
                if (sourceClass != null) {
                    return sourceClass
                }
            }
        }

        // Fall back to decompiled version
        if (canDecompile && containingFile is PsiCompiledFile) {
            val decompiledFile = containingFile.decompiledPsiFile
            if (decompiledFile is PsiJavaFile) {
                return decompiledFile.classes.firstOrNull { it.qualifiedName == psiClass.qualifiedName }
            }
        }

        return psiClass
    }

    /**
     * Finds a navigable element (method, field, class) for Ctrl+Click navigation.
     */
    fun findNavigableElement(element: PsiElement): PsiElement? {
        return when (element) {
            is PsiClass -> findNavigableClass(element)
            is PsiMethod -> {
                val containingClass = element.containingClass ?: return element
                val navigableClass = findNavigableClass(containingClass) ?: return element
                navigableClass.findMethodBySignature(element, false) ?: element
            }
            is PsiField -> {
                val containingClass = element.containingClass ?: return element
                val navigableClass = findNavigableClass(containingClass) ?: return element
                navigableClass.findFieldByName(element.name, false) ?: element
            }
            else -> element
        }
    }
}
