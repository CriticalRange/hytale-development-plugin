package com.criticalrange.hytaledev.manifest

import com.criticalrange.hytaledev.util.HytaleConstants
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory
import com.jetbrains.jsonSchema.extension.SchemaType

/**
 * Provides JSON schema for Hytale plugin manifest.json files.
 */
class ManifestJsonSchemaProviderFactory : JsonSchemaProviderFactory {

    override fun getProviders(project: Project): List<JsonSchemaFileProvider> {
        return listOf(ManifestJsonSchemaProvider())
    }
}

/**
 * JSON schema provider for manifest.json.
 */
class ManifestJsonSchemaProvider : JsonSchemaFileProvider {

    override fun isAvailable(file: VirtualFile): Boolean {
        return file.name == HytaleConstants.MANIFEST_FILE
    }

    override fun getName(): String = "Hytale Plugin Manifest"

    override fun getSchemaFile(): VirtualFile? {
        return javaClass.classLoader.getResource("schemas/manifest-schema.json")?.let { url ->
            com.intellij.openapi.vfs.VfsUtil.findFileByURL(url)
        }
    }

    override fun getSchemaType(): SchemaType = SchemaType.embeddedSchema
}
