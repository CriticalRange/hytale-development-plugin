package com.criticalrange.hytaledev.util

import com.intellij.json.psi.JsonElement
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonValue
import com.intellij.patterns.PatternCondition
import com.intellij.patterns.PsiElementPattern
import com.intellij.util.ProcessingContext

/**
 * Pattern condition to check if a JSON element is the value of a property with a specific name.
 */
fun PsiElementPattern.Capture<out JsonValue>.isPropertyValue(property: String) = with(
    object : PatternCondition<JsonElement>("isPropertyValue") {
        override fun accepts(t: JsonElement, context: ProcessingContext?): Boolean {
            val parent = t.parent as? JsonProperty ?: return false
            return parent.value == t && parent.name == property
        }
    },
)
