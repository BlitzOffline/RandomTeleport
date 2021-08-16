package com.blitzoffline.randomteleport.config

import com.blitzoffline.randomteleport.RandomTeleport
import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.config.holder.Settings
import me.mattstudios.config.SettingsManager
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit

class ConfigHandler(private val plugin: RandomTeleport) {
    fun loadConfig(): SettingsManager {
        val confFile = plugin.dataFolder.resolve("config.yml")
        if (!confFile.exists()) plugin.saveDefaultConfig()
        return SettingsManager
            .from(confFile)
            .configurationData(Settings::class.java)
            .create()
    }

    fun loadMessages(): SettingsManager {
        val msgFile = plugin.dataFolder.resolve("messages.yml")
        if (!msgFile.exists()) plugin.saveDefaultMessages()
        return SettingsManager
            .from(msgFile)
            .configurationData(Messages::class.java)
            .create()
    }

    fun loadEconomy(): Economy? {
        val rsp = Bukkit.getServer().servicesManager.getRegistration(Economy::class.java) ?: return null
        return rsp.provider
    }
}