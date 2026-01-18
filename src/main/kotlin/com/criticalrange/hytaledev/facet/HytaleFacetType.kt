package com.criticalrange.hytaledev.facet

import com.criticalrange.hytaledev.HytaleBundle
import com.criticalrange.hytaledev.assets.HytaleAssets
import com.intellij.facet.Facet
import com.intellij.facet.FacetType
import com.intellij.facet.FacetTypeId
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import javax.swing.Icon

/**
 * Facet type for Hytale plugin development.
 */
class HytaleFacetType : FacetType<HytaleFacet, HytaleFacetConfiguration>(
    TYPE_ID,
    ID,
    "Hytale"
) {

    companion object {
        const val ID = "hytale"
        val TYPE_ID = FacetTypeId<HytaleFacet>(ID)

        fun getInstance(): HytaleFacetType {
            return findInstance(HytaleFacetType::class.java)
        }
    }

    override fun createDefaultConfiguration(): HytaleFacetConfiguration {
        return HytaleFacetConfiguration()
    }

    override fun createFacet(
        module: Module,
        name: String,
        configuration: HytaleFacetConfiguration,
        underlyingFacet: Facet<*>?
    ): HytaleFacet {
        return HytaleFacet(this, module, name, configuration, underlyingFacet)
    }

    override fun isSuitableModuleType(moduleType: ModuleType<*>?): Boolean {
        // Hytale facet is suitable for Java modules
        return moduleType?.id == "JAVA_MODULE"
    }

    override fun getIcon(): Icon = HytaleAssets.HYTALE_ICON
}
