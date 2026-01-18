package com.criticalrange.hytaledev.facet

import com.intellij.facet.Facet
import com.intellij.facet.FacetManager
import com.intellij.facet.FacetType
import com.intellij.openapi.module.Module

/**
 * Hytale development facet.
 * Marks a module as a Hytale plugin project and provides configuration.
 */
class HytaleFacet(
    facetType: FacetType<HytaleFacet, HytaleFacetConfiguration>,
    module: Module,
    name: String,
    configuration: HytaleFacetConfiguration,
    underlyingFacet: Facet<*>?
) : Facet<HytaleFacetConfiguration>(facetType, module, name, configuration, underlyingFacet) {

    companion object {
        const val ID = "hytale"

        /**
         * Gets the Hytale facet for a module, if present.
         */
        fun getInstance(module: Module): HytaleFacet? {
            return FacetManager.getInstance(module).getFacetByType(HytaleFacetType.TYPE_ID)
        }

        /**
         * Checks if a module has the Hytale facet.
         */
        fun isHytaleModule(module: Module): Boolean {
            return getInstance(module) != null
        }
    }

    /**
     * Called when the facet is initialized.
     */
    override fun initFacet() {
        super.initFacet()
    }

    /**
     * Called when the facet is disposed.
     */
    override fun disposeFacet() {
        super.disposeFacet()
    }
}
