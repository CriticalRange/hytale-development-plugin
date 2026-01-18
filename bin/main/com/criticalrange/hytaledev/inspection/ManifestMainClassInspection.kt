package com.criticalrange.hytaledev.inspection

import com.criticalrange.hytaledev.util.HytaleConstants
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.json.psi.JsonElementVisitor
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiModifier
import com.intellij.psi.search.GlobalSearchScope

/**
 * Inspection that validates the "Main" field in Hytale plugin manifest.json files.
 * Checks that:
 * 1. The class exists
 * 2. The class extends PluginBase or JavaPlugin
 * 3. The class is not abstract
 * 4. The class is instantiable
 */
class ManifestMainClassInspection : LocalInspectionTool() {

    override fun getStaticDescription(): String =
        "Validates that the Main class specified in manifest.json exists and properly extends the Hytale plugin base class."

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        val fileName = holder.file.name
        if (fileName != HytaleConstants.MANIFEST_FILE) {
            return PsiElementVisitor.EMPTY_VISITOR
        }

        return object : JsonElementVisitor() {
            override fun visitProperty(property: JsonProperty) {
                super.visitProperty(property)

                if (property.name != "Main") return

                val value = property.value as? JsonStringLiteral ?: return
                val className = value.value

                if (className.isBlank()) {
                    holder.registerProblem(value, "Main class name cannot be empty")
                    return
                }

                // Skip validation for template variables (e.g., ${entry_point}, ${main_class})
                if (className.contains("\${")) {
                    return
                }

                val project = holder.project
                val scope = GlobalSearchScope.allScope(project)
                val facade = JavaPsiFacade.getInstance(project)

                // Check if class exists
                val psiClass = facade.findClass(className, scope)
                if (psiClass == null) {
                    holder.registerProblem(value, "Cannot resolve class '$className'")
                    return
                }

                // Check if class is abstract
                if (psiClass.hasModifierProperty(PsiModifier.ABSTRACT)) {
                    holder.registerProblem(value, "Main class '$className' cannot be abstract")
                    return
                }

                // Check if class extends PluginBase or JavaPlugin
                if (!isValidPluginClass(psiClass, facade, scope)) {
                    holder.registerProblem(
                        value,
                        "Class '$className' must extend ${HytaleConstants.JAVA_PLUGIN_CLASS} or ${HytaleConstants.PLUGIN_BASE_CLASS}"
                    )
                    return
                }

                // Check if class has a valid constructor (no-arg or JavaPluginInit)
                if (!hasValidConstructor(psiClass)) {
                    holder.registerProblem(
                        value,
                        "Main class '$className' must have a constructor that accepts JavaPluginInit"
                    )
                }
            }
        }
    }

    private fun isValidPluginClass(
        psiClass: PsiClass,
        facade: JavaPsiFacade,
        scope: GlobalSearchScope
    ): Boolean {
        for (baseClassName in HytaleConstants.PLUGIN_BASE_CLASSES) {
            val baseClass = facade.findClass(baseClassName, scope) ?: continue
            if (psiClass.isInheritor(baseClass, true)) {
                return true
            }
        }
        return false
    }

    private fun hasValidConstructor(psiClass: PsiClass): Boolean {
        val constructors = psiClass.constructors
        if (constructors.isEmpty()) {
            // Default constructor is fine if class extends JavaPlugin
            return true
        }

        for (constructor in constructors) {
            val params = constructor.parameterList.parameters
            // JavaPlugin requires constructor with JavaPluginInit parameter
            if (params.size == 1) {
                val paramType = params[0].type.canonicalText
                if (paramType.contains("JavaPluginInit")) {
                    return true
                }
            }
        }

        return false
    }
}
