package com.criticalrange.hytaledev.manifest

import com.criticalrange.hytaledev.util.HytaleConstants
import com.criticalrange.hytaledev.util.isPropertyValue
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.ProcessingContext

/**
 * Provides references from manifest.json to Java classes.
 * Enables Ctrl+Click navigation from "Main" field to the plugin class.
 */
class ManifestJsonReferenceContributor : PsiReferenceContributor() {

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        // Pattern: JsonStringLiteral that is the value of "Main" property in manifest.json
        val mainClassPattern = PlatformPatterns.psiElement(JsonStringLiteral::class.java)
            .inVirtualFile(PlatformPatterns.virtualFile().withName(HytaleConstants.MANIFEST_FILE))
            .isPropertyValue("Main")

        registrar.registerReferenceProvider(mainClassPattern, MainClassReferenceProvider())
    }
}

/**
 * Reference provider for the "Main" class field in manifest.json.
 */
class MainClassReferenceProvider : PsiReferenceProvider() {

    private val logger = com.intellij.openapi.diagnostic.Logger.getInstance(MainClassReferenceProvider::class.java)

    override fun getReferencesByElement(
        element: PsiElement,
        context: ProcessingContext
    ): Array<PsiReference> {
        if (element !is JsonStringLiteral) {
            return PsiReference.EMPTY_ARRAY
        }

        val value = element.value
        if (value.isBlank()) {
            return PsiReference.EMPTY_ARRAY
        }

        logger.info("Creating reference for Main class: $value")

        // Calculate the text range (exclude quotes)
        val textRange = TextRange(1, element.textLength - 1)

        return arrayOf(MainClassReference(element, textRange, value))
    }
}

/**
 * Reference from manifest.json "Main" field to the Java plugin class.
 */
class MainClassReference(
    element: JsonStringLiteral,
    textRange: TextRange,
    private val className: String
) : PsiReferenceBase<JsonStringLiteral>(element, textRange, true) {

    private val logger = com.intellij.openapi.diagnostic.Logger.getInstance(MainClassReference::class.java)

    override fun resolve(): PsiElement? {
        if (className.isBlank()) return null

        val project = element.project
        // Use resolveScope from the element itself, which includes the proper context
        val scope = element.resolveScope
        val javaPsiFacade = JavaPsiFacade.getInstance(project)

        logger.info("Resolving Main class: $className in scope: $scope")

        // Try to find the class directly
        var psiClass = javaPsiFacade.findClass(className, scope)
        logger.info("Direct lookup result: ${psiClass?.qualifiedName ?: "null"}")

        // If not found, try with allScope as fallback
        if (psiClass == null) {
            val allScope = GlobalSearchScope.allScope(project)
            psiClass = javaPsiFacade.findClass(className, allScope)
            logger.info("All scope lookup result: ${psiClass?.qualifiedName ?: "null"}")
        }

        // If not found, try replacing $ with . for inner classes
        if (psiClass == null && className.contains('$')) {
            val altName = className.replace('$', '.')
            psiClass = javaPsiFacade.findClass(altName, scope)
            logger.info("Inner class lookup ($altName) result: ${psiClass?.qualifiedName ?: "null"}")
        }

        return psiClass
    }

    override fun getVariants(): Array<Any> {
        // Provide completion variants - could list all classes extending PluginBase
        val project = element.project
        val scope = GlobalSearchScope.allScope(project)
        val javaPsiFacade = JavaPsiFacade.getInstance(project)

        // Find the PluginBase class to suggest subclasses
        val pluginBaseClass = javaPsiFacade.findClass(HytaleConstants.PLUGIN_BASE_CLASS, scope)
        if (pluginBaseClass != null) {
            val variants = mutableListOf<Any>()
            // Could use ClassInheritorsSearch here for better suggestions
            // For now, return empty to allow basic completion
            return variants.toTypedArray()
        }

        return emptyArray()
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        val manipulator = ElementManipulators.getManipulator(element)
        return manipulator?.handleContentChange(element, rangeInElement, newElementName) ?: element
    }

    override fun bindToElement(newElement: PsiElement): PsiElement {
        if (newElement is PsiClass) {
            val qualifiedName = newElement.qualifiedName ?: return element
            return handleElementRename(qualifiedName)
        }
        return element
    }
}
