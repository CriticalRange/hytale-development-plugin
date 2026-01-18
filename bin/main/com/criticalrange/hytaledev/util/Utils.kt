package com.criticalrange.hytaledev.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.libraries.LibraryKind
import com.intellij.openapi.roots.libraries.LibraryKindRegistry

/**
 * Creates or retrieves a LibraryKind by ID.
 * Following the pattern from Minecraft Development plugin.
 */
fun libraryKind(id: String): Lazy<LibraryKind> = lazy {
    LibraryKindRegistry.getInstance().findKindById(id) ?: LibraryKind.create(id)
}

/**
 * Checks if a class name belongs to Hytale API.
 */
fun isHytaleClass(className: String): Boolean {
    return className.startsWith(HytaleConstants.HYTALE_PACKAGE)
}

/**
 * Converts a class name to internal JVM format.
 * Example: com.hypixel.hytale.Foo -> com/hypixel/hytale/Foo
 */
fun toInternalName(className: String): String {
    return className.replace('.', '/')
}

/**
 * Converts an internal JVM class name to standard format.
 * Example: com/hypixel/hytale/Foo -> com.hypixel.hytale.Foo
 */
fun toClassName(internalName: String): String {
    return internalName.replace('/', '.')
}
