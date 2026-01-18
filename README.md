# Hytale Development Plugin

[![Build](https://github.com/criticalrange/hytale-development-plugin/workflows/Build/badge.svg)](https://github.com/criticalrange/hytale-development-plugin/actions)
[![Version](https://img.shields.io/jetbrains/plugin/v/com.criticalrange.hytaledev.svg)](https://plugins.jetbrains.com/plugin/com.criticalrange.hytaledev)

IntelliJ IDEA plugin for Hytale server plugin development, inspired by the [Minecraft Development](https://github.com/minecraft-dev/MinecraftDev) plugin.

<!-- Plugin description -->
**Hytale Development** brings IDE support for Hytale server plugin development to IntelliJ IDEA.

**Features:**
- Auto-detect `HytaleServer.jar` from your Hytale installation
- JSON schema validation and completion for `manifest.json`
- Ctrl+Click navigation to Hytale API source (decompiled)
- Code inspections for plugin classes and manifest files
- Gutter icons for plugin classes
- Template variable support (`${version}`, `${entry_point}`, etc.)
<!-- Plugin description end -->

## Features

### Library Detection
- Automatically detects `HytaleServer.jar` from your Hytale installation
- Registers Hytale API as a project library for code completion and navigation
- Supports Flatpak, Windows, and macOS installation paths

### Manifest Support
- JSON schema validation for `manifest.json`
- Auto-completion for all manifest fields
- Ctrl+Click navigation from `"Main"` field to your plugin class
- Template variable support (`${version}`, `${entry_point}`, etc.)

### Code Inspections
- **Main Class Validation**: Ensures your Main class exists and extends `JavaPlugin`/`PluginBase`
- **Required Fields**: Validates Group, Name, Version, and Main fields
- **Plugin Structure**: Checks plugin classes have proper constructors

### Navigation
- Ctrl+Click on Hytale API classes to view decompiled source
- Gutter icons for plugin classes extending `JavaPlugin` or `PluginBase`

### Project Support
- Auto-detection of Hytale plugin projects
- Hytale Facet for project configuration

## Installation

### From JetBrains Marketplace
1. Open IntelliJ IDEA
2. Go to `Settings` → `Plugins` → `Marketplace`
3. Search for "Hytale Development"
4. Click `Install`

### Manual Installation
1. Download the latest release from [GitHub Releases](https://github.com/criticalrange/hytale-development-plugin/releases)
2. Go to `Settings` → `Plugins` → `⚙️` → `Install Plugin from Disk...`
3. Select the downloaded `.zip` file

## Requirements

- IntelliJ IDEA 2025.2 or later (Ultimate or Community)
- Hytale installed (for `HytaleServer.jar` detection)

## Quick Start

1. Create a new Java/Kotlin project
2. Add `manifest.json` to your resources folder:

```json
{
  "Group": "com.example",
  "Name": "MyPlugin",
  "Version": "1.0.0",
  "Description": "My first Hytale plugin",
  "Main": "com.example.MyPlugin",
  "ServerVersion": "*"
}
```

3. Create your plugin class:

```java
package com.example;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

public class MyPlugin extends JavaPlugin {
    public MyPlugin(JavaPluginInit init) {
        super(init);
    }
    
    @Override
    public void onEnable() {
        getLogger().info("MyPlugin enabled!");
    }
}
```

4. The plugin will automatically detect your project and provide IDE support!

## Hytale API Classes

Key classes from `HytaleServer.jar`:

| Class | Description |
|-------|-------------|
| `com.hypixel.hytale.server.core.plugin.JavaPlugin` | Base class for Java plugins |
| `com.hypixel.hytale.server.core.plugin.PluginBase` | Abstract plugin base |
| `com.hypixel.hytale.server.core.plugin.JavaPluginInit` | Plugin initialization context |
| `com.hypixel.hytale.server.core.plugin.event.PluginEvent` | Event system |

## Roadmap

### v0.2.0 (Planned)
- Project creation wizard
- Live templates (`hplugin`, `hevent`, `hmanifest`)
- Run configurations for Hytale server
- Event completion for `EventRegistry.registerGlobal()`

### Future
- Plugin Hot Reload
- Hytale API Reference browser
- Hytale IDE Theme

## Development

```bash
# Build the plugin
./gradlew build

# Run in sandboxed IDE
./gradlew runIde

# Run tests
./gradlew test

# Verify plugin compatibility
./gradlew verifyPlugin
```

## Contributing

Contributions are welcome! Please feel free to submit issues and pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Links

- [GitHub Repository](https://github.com/criticalrange/hytale-development-plugin)
- [Issue Tracker](https://github.com/criticalrange/hytale-development-plugin/issues)
- [Hytale Modding Community](https://hytalemodding.dev/)
- [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html)
