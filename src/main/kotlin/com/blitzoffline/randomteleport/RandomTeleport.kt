package com.blitzoffline.randomteleport

import com.blitzoffline.randomteleport.commands.CommandRTP
import com.blitzoffline.randomteleport.commands.CommandRTPWorld
import com.blitzoffline.randomteleport.commands.CommandReload
import com.blitzoffline.randomteleport.config.ConfigHandler
import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.config.holder.Settings
import com.blitzoffline.randomteleport.cooldown.CooldownHandler
import com.blitzoffline.randomteleport.listeners.DamageListener
import com.blitzoffline.randomteleport.listeners.InteractListener
import com.blitzoffline.randomteleport.listeners.MoveListener
import com.blitzoffline.randomteleport.placeholders.RandomTeleportPlaceholders
import com.blitzoffline.randomteleport.tasks.GenerateTeleportLocations
import com.blitzoffline.randomteleport.util.LocationHandler
import com.blitzoffline.randomteleport.util.msg
import java.util.concurrent.ArrayBlockingQueue
import me.mattstudios.config.SettingsManager
import me.mattstudios.mf.base.CommandBase
import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mf.base.components.CompletionResolver
import me.mattstudios.mf.base.components.MessageResolver
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class RandomTeleport : JavaPlugin() {
    private lateinit var commandManager: CommandManager
    private lateinit var configHandler: ConfigHandler

    lateinit var cooldownHandler: CooldownHandler
        private set
    lateinit var locationHandler: LocationHandler
        private set

    val hooks = mutableMapOf<String, Boolean>()
    val locations = hashMapOf<String, ArrayBlockingQueue<Location>>()

    lateinit var settings: SettingsManager
        private set
    lateinit var messages: SettingsManager
        private set
    lateinit var economy: Economy
        private set

    var isPaper = true

    override fun onEnable() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig")
        } catch (ignored: ClassNotFoundException) {
            isPaper = false
            warn("THIS PLUGIN SHOULD BE USED ON PAPERMC: papermc.io/download")
        }

        configHandler = ConfigHandler(this)
        settings = configHandler.loadConfig()
        messages = configHandler.loadMessages()

        locationHandler = LocationHandler(this)

        "PlaceholderAPI".hook()
        if (settings[Settings.HOOK_LANDS]) {
            "Lands".hook()
            locationHandler.startLandsIntegration()
            hooks["Lands"] = true
        }
        if (settings[Settings.HOOK_GD]) {
            "GriefDefender".hook()
            locationHandler.startGriefDefenderIntegration()
            hooks["GriefDefender"] = true
        }
        if (settings[Settings.HOOK_WG]) {
            "WorldGuard".hook()
            locationHandler.startWorldGuardIntegration()
            hooks["WorldGuard"] = true
        }
        if (settings[Settings.HOOK_TOWNY]) {
            "Towny".hook()
            locationHandler.startTownyIntegration()
            hooks["Towny"] = true
        }
        if (settings[Settings.HOOK_VAULT]) {
            "Vault".hook()
            economy = configHandler.loadEconomy() ?: return
            hooks["Vault"] = true
        }

        RandomTeleportPlaceholders(this).register()
        cooldownHandler = CooldownHandler(this)

        registerListeners(
            DamageListener(this),
            InteractListener(this),
            MoveListener(this)
        )

        commandManager = CommandManager(this, true)
        registerMessage("cmd.no.permission") { sender -> messages[Messages.NO_PERMISSION].msg(sender) }
        registerCompletion("#worlds") { Bukkit.getWorlds().map(World::getName) }
        registerCommands(
            CommandRTPWorld(this),
            CommandRTP(this),
            CommandReload(this)
        )

        val worlds = if (settings[Settings.ENABLED_WORLDS].contains("all")) Bukkit.getWorlds() else settings[Settings.ENABLED_WORLDS].distinct().mapNotNull { Bukkit.getWorld(it) }
        worlds.forEach { world ->
            if (locations[world.name] != null) {
                return@forEach
            }

            locations[world.name] = ArrayBlockingQueue(15)
        }

        GenerateTeleportLocations(this).runTaskTimerAsynchronously(this, 0, 150 * 20L)

        log("Plugin enabled successfully!")
    }

    private fun String.exists() = Bukkit.getPluginManager().getPlugin(this) != null && Bukkit.getPluginManager().getPlugin(this)?.isEnabled ?: false
    private fun String.hook() {
        if (!this.exists()) {
            log("Could not find $this. That plugin is required!")
            pluginLoader.disablePlugin(this@RandomTeleport)
        }
    }

    override fun onDisable() { log("Plugin disabled successfully!") }

    private fun registerListeners(vararg listeners: Listener) = listeners.forEach { server.pluginManager.registerEvents(it, this) }
    private fun registerCommands(vararg commands: CommandBase) = commands.forEach(commandManager::register)
    private fun registerCompletion(completionId: String, resolver: CompletionResolver) = commandManager.completionHandler.register(completionId, resolver)
    private fun registerMessage(messageId: String, resolver: MessageResolver) = commandManager.messageHandler.register(messageId, resolver)
    private fun warn(message: String) = logger.warning(message)
    private fun log(message: String) = logger.info(message)

    fun saveDefaultMessages() {
        if (dataFolder.resolve("messages.yml").exists()) return
        saveResource("messages.yml", false)
    }
}