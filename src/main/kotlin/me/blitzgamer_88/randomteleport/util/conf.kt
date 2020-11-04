package me.blitzgamer_88.randomteleport.util

import ch.jalu.configme.SettingsManager
import me.blitzgamer_88.randomteleport.RandomTeleport
import me.blitzgamer_88.randomteleport.conf.RandomTeleportConfiguration

private var conf = null as? SettingsManager?

fun loadConfig(plugin: RandomTeleport) {
    val file = plugin.dataFolder.resolve("config.yml")

    if (!file.exists())
    {
        file.parentFile.mkdirs()
        file.createNewFile()
    }

    conf = RandomTeleportConfiguration(file)
}

fun conf(): SettingsManager
{
    return checkNotNull(conf)
}