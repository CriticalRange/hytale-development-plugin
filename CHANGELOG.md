# Hytale Development Plugin Changelog


## [0.1.0] - 2026-01-18

### Added

#### Library Detection & Management
- Auto-detect `HytaleServer.jar` from Flatpak installation (`~/.var/app/com.hypixel.HytaleLauncher/...`)
- Automatic library registration as project dependency
- Support for standard Windows and macOS installation paths

#### Manifest Support (manifest.json)
- Full JSON schema with validation and auto-completion
- Field validation: Group (package format), Name (alphanumeric), Version (semver), Main (class reference)
- Ctrl+Click navigation from "Main" field to the plugin class
- Template variable support (`${version}`, `${entry_point}`, etc.) - validation skipped for placeholders

#### Code Inspections
- **Main Class Validation** - Verifies Main class exists, extends PluginBase/JavaPlugin, is not abstract
- **Required Fields Validation** - Ensures Group, Name, Version, Main are present and properly formatted
- **Plugin Class Structure** - Validates plugin classes have correct constructors and visibility

#### Navigation & Code Insight
- Ctrl+Click on Hytale API classes navigates to decompiled source
- Gutter icons (line markers) for classes extending `JavaPlugin` or `PluginBase`
- Source attachment provider for decompilation

#### Project Support
- Hytale Facet with auto-detection for projects containing `manifest.json`
- Automatic library dependency injection when Hytale project is detected
- Project startup activity for initialization

#### Assets
- Custom Hytale icons for plugin classes, manifest files, and event listeners
- Plugin branding with Hytale theme

## [Unreleased]

### Technical
- Package: `com.criticalrange.hytaledev`
- Dependencies: Java, ByteCodeViewer, JSON, ASM
- Supports IntelliJ IDEA 2025.2+
- Kotlin coroutine-based threading for proper EDT/background handling
