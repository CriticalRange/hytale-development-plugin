package com.criticalrange.hytaledev.facet

import com.intellij.facet.FacetConfiguration
import com.intellij.facet.ui.FacetEditorContext
import com.intellij.facet.ui.FacetEditorTab
import com.intellij.facet.ui.FacetValidatorsManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * Configuration for Hytale facet.
 * Stores facet-specific settings that persist across IDE sessions.
 */
class HytaleFacetConfiguration : FacetConfiguration, PersistentStateComponent<HytaleFacetConfiguration> {

    /**
     * Whether auto-detection of Hytale libraries is enabled.
     */
    var autoDetectLibraries: Boolean = true

    /**
     * Custom path to HytaleServer.jar (if not using auto-detection).
     */
    var customServerJarPath: String? = null

    override fun createEditorTabs(
        editorContext: FacetEditorContext,
        validatorsManager: FacetValidatorsManager
    ): Array<FacetEditorTab> {
        return arrayOf(HytaleFacetEditorTab(this, editorContext))
    }

    override fun getState(): HytaleFacetConfiguration = this

    override fun loadState(state: HytaleFacetConfiguration) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
