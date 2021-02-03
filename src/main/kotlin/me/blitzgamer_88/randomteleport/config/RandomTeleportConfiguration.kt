package me.blitzgamer_88.randomteleport.config

import ch.jalu.configme.SettingsManagerImpl
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder
import ch.jalu.configme.migration.PlainMigrationService
import ch.jalu.configme.resource.YamlFileResource
import java.io.File

class RandomTeleportConfiguration(file: File) : SettingsManagerImpl(
    YamlFileResource(file.toPath()),
    ConfigurationDataBuilder.createConfiguration(Config::class.java),
    PlainMigrationService())