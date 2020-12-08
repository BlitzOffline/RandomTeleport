package me.blitzgamer_88.randomteleport

import me.blitzgamer_88.randomteleport.cmd.CommandRandomTeleport
import me.blitzgamer_88.randomteleport.placeholders.RandomTeleportPlaceholders
import me.blitzgamer_88.randomteleport.util.*
import me.bristermitten.pdm.PDMBuilder
import me.mattstudios.mf.base.CommandManager
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin
import kotlin.collections.HashMap
import java.util.UUID

class RandomTeleport : JavaPlugin() {

    val coolDowns = HashMap<UUID, Long>()

    override fun onLoad() { PDMBuilder(this).build().loadAllDependencies().join() }

    override fun onEnable() {

        this.saveDefaultConfig()
        loadConfig(this)

        registerValues()
        registerMessages()

        dependenciesHook("PlaceholderAPI")
        if (hookWorldGuard) dependenciesHook("WorldGuard")

        registerCommands()

        RandomTeleportPlaceholders(this).register()
        "&7[RandomTeleport] Plugin enabled successfully!".log()
    }

    override fun onDisable() { "&7[RandomTeleport] Plugin disabled successfully!".log() }

    fun reload() {
        conf().reload()
        registerValues()
        registerMessages()
    }

    private fun dependenciesHook(plugin: String) {
        if (Bukkit.getPluginManager().getPlugin(plugin) == null) {
            "&7Could not find: $plugin. Plugin disabled!".log()
            Bukkit.getPluginManager().disablePlugin(this)
        }
        else { "&7Successfully hooked into $plugin!".log() }
    }

    private fun registerCommands() {
        val cmdManager = CommandManager(this, true)
        cmdManager.completionHandler.register("#worlds") { Bukkit.getWorlds().map(World::getName) }
        cmdManager.register(CommandRandomTeleport(this))
    }
}