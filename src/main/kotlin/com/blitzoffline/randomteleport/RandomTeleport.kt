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
import com.blitzoffline.randomteleport.listeners.ServerLoadListener
import com.blitzoffline.randomteleport.placeholders.RandomTeleportPlaceholders
import com.blitzoffline.randomteleport.util.LocationHandler
import com.blitzoffline.randomteleport.util.msg
import me.mattstudios.config.SettingsManager
import me.mattstudios.mf.base.CommandBase
import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mf.base.components.CompletionResolver
import me.mattstudios.mf.base.components.MessageResolver
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
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

    lateinit var settings: SettingsManager
        private set
    lateinit var messages: SettingsManager
        private set
    lateinit var economy: Economy
        private set

    var isPaper = true
        private set

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
        cooldownHandler = CooldownHandler(this)

        registerListeners(
            ServerLoadListener(this),
            DamageListener(this),
            InteractListener(this),
            MoveListener(this)
        )

        commandManager = CommandManager(this, true)
        registerMessages(
            "cmd.no.permission" to MessageResolver { sender -> messages[Messages.NO_PERMISSION].msg(sender) }
        )
        registerCompletions(
            "#worlds" to CompletionResolver { Bukkit.getWorlds().map(World::getName) }
        )
        registerCommands(
            CommandRTPWorld(this),
            CommandRTP(this),
            CommandReload(this)
        )

        log("Plugin enabled successfully!")
    }

    override fun onDisable() {
        log("Plugin disabled successfully!")
    }

    fun enableHooks() {
        "PlaceholderAPI".hook()
        RandomTeleportPlaceholders(this).register()
        log("Successfully hooked into PlaceholderAPI!")
        if (settings[Settings.HOOK_LANDS]) {
            "Lands".hook()
            locationHandler.startLandsIntegration()
            hooks["Lands"] = true
            log("Successfully hooked into Lands!")
        }
        if (settings[Settings.HOOK_GD]) {
            "GriefDefender".hook()
            locationHandler.startGriefDefenderIntegration()
            hooks["GriefDefender"] = true
            log("Successfully hooked into GriefDefender!")
        }
        if (settings[Settings.HOOK_WG]) {
            "WorldGuard".hook()
            locationHandler.startWorldGuardIntegration()
            hooks["WorldGuard"] = true
            log("Successfully hooked into WorldGuard!")
        }
        if (settings[Settings.HOOK_TOWNY]) {
            "Towny".hook()
            locationHandler.startTownyIntegration()
            hooks["Towny"] = true
            log("Successfully hooked into Towny!")
        }
        if (settings[Settings.HOOK_VAULT]) {
            "Vault".hook()
            economy = configHandler.loadEconomy() ?: return
            hooks["Vault"] = true
            log("Successfully hooked into Vault!")
        }
    }

    private fun String.exists() = Bukkit.getPluginManager().getPlugin(this) != null && Bukkit.getPluginManager().getPlugin(this)?.isEnabled ?: false
    private fun String.hook() {
        if (!this.exists()) {
            log("Could not find $this. That plugin is required!")
            pluginLoader.disablePlugin(this@RandomTeleport)
        }
    }


    private fun registerCommands(vararg commands: CommandBase) = commands.forEach(commandManager::register)
    private fun registerMessages(vararg pairs: Pair<String, MessageResolver>) = pairs.forEach { pair -> commandManager.messageHandler.register(pair.first, pair.second) }
    private fun registerListeners(vararg listeners: Listener) = listeners.forEach { server.pluginManager.registerEvents(it, this) }
    private fun registerCompletions(vararg pairs: Pair<String, CompletionResolver>) = pairs.forEach { pair -> commandManager.completionHandler.register(pair.first, pair.second) }
    private fun warn(message: String) = logger.warning(message)
    private fun log(message: String) = logger.info(message)

    fun saveDefaultMessages() {
        if (dataFolder.resolve("messages.yml").exists()) return
        saveResource("messages.yml", false)
    }
}