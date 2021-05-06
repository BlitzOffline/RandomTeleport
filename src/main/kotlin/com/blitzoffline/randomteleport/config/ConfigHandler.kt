package com.blitzoffline.randomteleport.config

import com.blitzoffline.randomteleport.RandomTeleport
import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.config.holder.Settings
import me.mattstudios.config.SettingsManager
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit

lateinit var settings: SettingsManager
lateinit var econ: Economy

fun loadConfig(plugin: RandomTeleport) {
    val file = plugin.dataFolder.resolve("config.yml")
    if (!file.exists()) plugin.saveDefaultConfig()
    settings = SettingsManager
        .from(file)
        .configurationData(Settings::class.java, Messages::class.java)
        .create()
}

fun setupEconomy(): Boolean {
    if (Bukkit.getServer().pluginManager.getPlugin("Vault") == null) return false
    val rsp = Bukkit.getServer().servicesManager.getRegistration(Economy::class.java) ?: return false
    econ = rsp.provider
    return true
}