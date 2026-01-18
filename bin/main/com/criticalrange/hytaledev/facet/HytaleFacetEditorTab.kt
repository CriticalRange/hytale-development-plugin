package com.criticalrange.hytaledev.facet

import com.criticalrange.hytaledev.HytaleBundle
import com.criticalrange.hytaledev.util.HytaleConstants
import com.intellij.facet.ui.FacetEditorContext
import com.intellij.facet.ui.FacetEditorTab
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

/**
 * Editor tab for Hytale facet configuration.
 */
class HytaleFacetEditorTab(
    private val configuration: HytaleFacetConfiguration,
    private val context: FacetEditorContext
) : FacetEditorTab() {

    private val autoDetectCheckBox = JBCheckBox("Auto-detect Hytale libraries")
    private val serverJarPathField = TextFieldWithBrowseButton()

    private var modified = false

    init {
        // Configure file chooser for JAR files
        val descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("jar")
            .withTitle("Select HytaleServer.jar")
            .withDescription("Choose the HytaleServer.jar file from your Hytale installation")

        serverJarPathField.addBrowseFolderListener(context.project, descriptor)

        // Update UI state when auto-detect changes
        autoDetectCheckBox.addActionListener {
            serverJarPathField.isEnabled = !autoDetectCheckBox.isSelected
            modified = true
        }

        serverJarPathField.textField.document.addDocumentListener(object : javax.swing.event.DocumentListener {
            override fun insertUpdate(e: javax.swing.event.DocumentEvent?) { modified = true }
            override fun removeUpdate(e: javax.swing.event.DocumentEvent?) { modified = true }
            override fun changedUpdate(e: javax.swing.event.DocumentEvent?) { modified = true }
        })
    }

    override fun getDisplayName(): String = "Hytale"

    override fun createComponent(): JComponent {
        return panel {
            group("Library Detection") {
                row {
                    cell(autoDetectCheckBox)
                }
                row("HytaleServer.jar path:") {
                    cell(serverJarPathField)
                        .comment("Leave empty for auto-detection. Found at: ${HytaleConstants.findServerJar() ?: "Not found"}")
                }
            }
        }
    }

    override fun isModified(): Boolean = modified

    override fun reset() {
        autoDetectCheckBox.isSelected = configuration.autoDetectLibraries
        serverJarPathField.text = configuration.customServerJarPath ?: ""
        serverJarPathField.isEnabled = !configuration.autoDetectLibraries
        modified = false
    }

    override fun apply() {
        configuration.autoDetectLibraries = autoDetectCheckBox.isSelected
        configuration.customServerJarPath = serverJarPathField.text.takeIf { it.isNotBlank() }
        modified = false
    }
}
