package com.criticalrange.hytaledev.inspection

import com.criticalrange.hytaledev.insight.InsightUtil
import com.criticalrange.hytaledev.util.HytaleConstants
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiModifier
import com.intellij.psi.search.GlobalSearchScope

/**
 * Inspection that validates Hytale plugin classes.
 * Checks that:
 * 1. Plugin class is not abstract
 * 2. Plugin class has proper constructor
 * 3. Plugin class extends the correct base class
 */
class PluginClassInspection : AbstractBaseJavaLocalInspectionTool() {

    override fun getStaticDescription(): String =
        "Validates that Hytale plugin classes are properly structured and extend the correct base class."

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitClass(aClass: PsiClass) {
                super.visitClass(aClass)

                // Check if this looks like it should be a plugin class
                if (!shouldCheckClass(aClass)) return

                val project = holder.project
                val scope = GlobalSearchScope.allScope(project)
                val facade = JavaPsiFacade.getInstance(project)

                // Check if extending plugin base class
                val extendsPluginBase = HytaleConstants.PLUGIN_BASE_CLASSES.any { baseClassName ->
                    val baseClass = facade.findClass(baseClassName, scope)
                    baseClass != null && aClass.isInheritor(baseClass, true)
                }

                if (!extendsPluginBase) {
                    // Not a plugin class, skip
                    return
                }

                // Validate the plugin class
                validatePluginClass(aClass, holder)
            }
        }
    }

    private fun shouldCheckClass(aClass: PsiClass): Boolean {
        // Skip interfaces and annotations
        if (aClass.isInterface || aClass.isAnnotationType) return false

        // Skip anonymous and local classes
        if (aClass.name == null) return false

        // Check if it's in a source file (not library)
        val containingFile = aClass.containingFile ?: return false
        return containingFile.virtualFile?.isInLocalFileSystem == true
    }

    private fun validatePluginClass(aClass: PsiClass, holder: ProblemsHolder) {
        val nameIdentifier = aClass.nameIdentifier ?: return

        // Check if abstract
        if (aClass.hasModifierProperty(PsiModifier.ABSTRACT)) {
            holder.registerProblem(
                nameIdentifier,
                "Plugin class '${aClass.name}' should not be abstract"
            )
            return
        }

        // Check constructor
        val constructors = aClass.constructors
        if (constructors.isNotEmpty()) {
            val hasValidConstructor = constructors.any { constructor ->
                val params = constructor.parameterList.parameters
                params.size == 1 && params[0].type.canonicalText.contains("JavaPluginInit")
            }

            if (!hasValidConstructor) {
                holder.registerProblem(
                    nameIdentifier,
                    "Plugin class '${aClass.name}' must have a constructor that accepts JavaPluginInit parameter"
                )
            }
        }

        // Check visibility
        if (aClass.hasModifierProperty(PsiModifier.PRIVATE)) {
            holder.registerProblem(
                nameIdentifier,
                "Plugin class '${aClass.name}' cannot be private"
            )
        }
    }
}
