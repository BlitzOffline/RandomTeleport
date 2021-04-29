package com.blitzoffline.randomteleport.config

import com.blitzoffline.randomteleport.RandomTeleport
import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.config.holder.Settings
import me.mattstudios.config.SettingsManager

lateinit var settings: SettingsManager
lateinit var messages: SettingsManager

fun loadConfig(plugin: RandomTeleport) {
    val file = plugin.dataFolder.resolve("config.yml")
    if (!file.exists()) plugin.saveDefaultConfig()
    settings = SettingsManager
        .from(file)
        .configurationData(Settings::class.java)
        .create()
}

fun loadMessages(plugin: RandomTeleport) {
    val file = plugin.dataFolder.resolve("config.yml")
    if (!file.exists()) plugin.saveDefaultConfig()
    messages =  SettingsManager
        .from(file)
        .configurationData(Messages::class.java)
        .create()
}