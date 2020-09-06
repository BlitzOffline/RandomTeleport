package me.blitzgamer_88.randomteleport.conf

import ch.jalu.configme.SettingsManagerImpl
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder
import ch.jalu.configme.migration.PlainMigrationService
import ch.jalu.configme.resource.YamlFileResource
import java.io.File

class RandomTeleportConfiguration(file: File) : SettingsManagerImpl(YamlFileResource(file), ConfigurationDataBuilder.createConfiguration(SECTIONS), PlainMigrationService()) {

    private companion object
    {

        private val SECTIONS = listOf(
            Config::class.java
        )

    }

}