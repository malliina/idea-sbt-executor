# SBT Executor

This is a plugin for IntelliJ IDEA that adds a menu to execute Simple Build Tool (SBT) commands.

Define your favorite commands under Project Settings > SBT Executor.

## Installation

Install *SBT Executor* from IntelliJ's Plugins section.

## Development

1. Open this project in IntelliJ.
2. Make sure the project SDK is IntelliJ IDEA (in Project Structure -> Project Settings -> Project).
3. Make sure the SDK uses JDK version 11.
4. A run configuration should exist that launches the plugin in another IntelliJ window.

### Releasing a New Version

1. In IntelliJ, select *Build* -> *Prepare Plugin Module...*. Observe the generated zip file.
1. Log in to https://plugins.jetbrains.com/.
1. Select the plugin, click the edit icon, then upload the zip file from step 1.