package com.criticalrange.hytaledev.inspection

import com.criticalrange.hytaledev.util.HytaleConstants
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.json.psi.JsonElementVisitor
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.psi.PsiElementVisitor

/**
 * Inspection that validates required fields in Hytale plugin manifest.json files.
 * Required fields: Group, Name, Version, Main
 */
class ManifestRequiredFieldsInspection : LocalInspectionTool() {

    companion object {
        val REQUIRED_FIELDS = listOf("Group", "Name", "Version", "Main")
    }

    override fun getStaticDescription(): String =
        "Validates that all required fields (Group, Name, Version, Main) are present in manifest.json."

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        val fileName = holder.file.name
        if (fileName != HytaleConstants.MANIFEST_FILE) {
            return PsiElementVisitor.EMPTY_VISITOR
        }

        return object : JsonElementVisitor() {
            override fun visitObject(obj: JsonObject) {
                super.visitObject(obj)

                // Only check the root object
                if (obj.parent != holder.file) return

                val presentFields = obj.propertyList.map { it.name }.toSet()
                val missingFields = REQUIRED_FIELDS.filter { it !in presentFields }

                if (missingFields.isNotEmpty()) {
                    val message = if (missingFields.size == 1) {
                        "Missing required field: ${missingFields.first()}"
                    } else {
                        "Missing required fields: ${missingFields.joinToString(", ")}"
                    }
                    holder.registerProblem(obj.firstChild, message)
                }
            }

            override fun visitProperty(property: JsonProperty) {
                super.visitProperty(property)

                // Validate field values are not empty (but allow template variables)
                val name = property.name
                if (name in REQUIRED_FIELDS) {
                    val value = property.value
                    when {
                        value == null -> {
                            holder.registerProblem(property, "Field '$name' must have a value")
                        }
                        value is JsonStringLiteral && value.value.isBlank() -> {
                            holder.registerProblem(value, "Field '$name' cannot be empty")
                        }
                        // Skip further validation if value contains template variable
                        value is JsonStringLiteral && value.value.contains("\${") -> {
                            return
                        }
                    }
                }

                // Validate field formats
                when (property.name) {
                    "Group" -> validateGroup(property, holder)
                    "Name" -> validateName(property, holder)
                    "Version" -> validateVersion(property, holder)
                }
            }
        }
    }

    private fun validateGroup(property: JsonProperty, holder: ProblemsHolder) {
        val value = (property.value as? JsonStringLiteral)?.value ?: return

        // Skip validation for template variables (e.g., ${group}, ${package})
        if (value.contains("\${")) return

        // Group should be lowercase with dots (like Java package)
        val groupRegex = Regex("^[a-z][a-z0-9]*(\\.[a-z][a-z0-9]*)*$")
        if (!groupRegex.matches(value)) {
            holder.registerProblem(
                property.value!!,
                "Group should be a valid package name (lowercase, dot-separated, e.g., 'com.example')"
            )
        }
    }

    private fun validateName(property: JsonProperty, holder: ProblemsHolder) {
        val value = (property.value as? JsonStringLiteral)?.value ?: return

        // Skip validation for template variables (e.g., ${name}, ${plugin_name})
        if (value.contains("\${")) return

        // Name should be alphanumeric with optional dashes/underscores
        val nameRegex = Regex("^[A-Za-z][A-Za-z0-9_-]*$")
        if (!nameRegex.matches(value)) {
            holder.registerProblem(
                property.value!!,
                "Name should start with a letter and contain only letters, numbers, dashes, or underscores"
            )
        }
    }

    private fun validateVersion(property: JsonProperty, holder: ProblemsHolder) {
        val value = (property.value as? JsonStringLiteral)?.value ?: return

        // Skip validation for template variables (e.g., ${version}, ${project_version})
        if (value.contains("\${")) return

        // Version should follow semver (basic check)
        val versionRegex = Regex("^[0-9]+\\.[0-9]+\\.[0-9]+(-[A-Za-z0-9.]+)?$")
        if (!versionRegex.matches(value)) {
            holder.registerProblem(
                property.value!!,
                "Version should follow semantic versioning (e.g., '1.0.0' or '1.0.0-beta.1')"
            )
        }
    }
}
