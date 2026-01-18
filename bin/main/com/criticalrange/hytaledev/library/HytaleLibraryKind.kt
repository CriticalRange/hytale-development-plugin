package com.criticalrange.hytaledev.library

import com.criticalrange.hytaledev.util.HytaleConstants
import com.criticalrange.hytaledev.util.libraryKind
import com.intellij.openapi.roots.libraries.LibraryKind

/**
 * Library kind for HytaleServer.jar detection.
 */
val HYTALE_LIBRARY_KIND: LibraryKind by libraryKind(HytaleConstants.HYTALE_LIBRARY_KIND_ID)
