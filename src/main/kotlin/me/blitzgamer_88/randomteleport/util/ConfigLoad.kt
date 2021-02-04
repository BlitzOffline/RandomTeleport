package me.blitzgamer_88.randomteleport.util

import ch.jalu.configme.SettingsManager
import me.blitzgamer_88.randomteleport.RandomTeleport
import me.blitzgamer_88.randomteleport.config.RandomTeleportConfiguration

lateinit var conf: SettingsManager

fun loadConfig(plugin: RandomTeleport) {
    val file = plugin.dataFolder.resolve("config.yml")
    if (!file.exists()) {
        file.parentFile.mkdirs()
        file.createNewFile()
    }
    conf = RandomTeleportConfiguration(file)
}