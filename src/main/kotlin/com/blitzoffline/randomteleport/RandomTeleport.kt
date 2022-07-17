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
import dev.triumphteam.cmd.bukkit.BukkitCommandManager
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.requirement.RequirementKey
import me.mattstudios.config.SettingsManager
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class RandomTeleport : JavaPlugin() {
    private lateinit var placeholders: RandomTeleportPlaceholders
    private lateinit var commandManager: BukkitCommandManager<CommandSender>
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

        commandManager = BukkitCommandManager.create(this)
        registerRequirements()
        registerMessages()

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
        if (settings[Settings.HOOK_LANDS]) {
            if (!hook("Lands")) {
                return
            }
            locationHandler.startLandsIntegration()
            hooks["Lands"] = true
            log("Successfully hooked into Lands!")
        }
        if (settings[Settings.HOOK_GD]) {
            if (!hook("GriefDefender")) {
                return
            }
            locationHandler.startGriefDefenderIntegration()
            hooks["GriefDefender"] = true
            log("Successfully hooked into GriefDefender!")
        }
        if (settings[Settings.HOOK_WG]) {
            if (!hook("WorldGuard")) {
                return
            }
            locationHandler.startWorldGuardIntegration()
            hooks["WorldGuard"] = true
            log("Successfully hooked into WorldGuard!")
        }
        if (settings[Settings.HOOK_TOWNY]) {
            if (!hook("Towny")) {
                return
            }
            locationHandler.startTownyIntegration()
            hooks["Towny"] = true
            log("Successfully hooked into Towny!")
        }
        if (settings[Settings.HOOK_VAULT]) {
            if (!hook("Vault")) {
                return
            }
            economy = configHandler.loadEconomy() ?: return
            hooks["Vault"] = true
            log("Successfully hooked into Vault!")
        }

        if (hook("PlaceholderAPI")) {
            placeholders = RandomTeleportPlaceholders(this)
            placeholders.register()
            log("Successfully hooked into PlaceholderAPI!")
        }
    }

    private fun String.exists() = Bukkit.getPluginManager().getPlugin(this) != null && Bukkit.getPluginManager().getPlugin(this)?.isEnabled ?: false
    private fun hook(name: String): Boolean {
        if (!name.exists()) {
            warn("Could not find $this. Disabling RandomTeleport!")
            warn("If you don't want RandomTeleport to hook into $name then disable the option in plugins/RandomTeleport/config.yml at hooks.${name.lowercase()}")
            pluginLoader.disablePlugin(this)
            return false
        }
        return true
    }


    private fun registerCommands(vararg commands: BaseCommand) = commands.forEach(commandManager::registerCommand)
    private fun registerListeners(vararg listeners: Listener): Unit = listeners.forEach { server.pluginManager.registerEvents(it, this) }
    private fun registerRequirements() {
        commandManager.registerRequirement(RequirementKey.of("rtp-permissions")) { sender ->
            sender.hasPermission("randomteleport.self") || sender.hasPermission("randomteleport.others")
        }

        commandManager.registerRequirement(RequirementKey.of("rtp-world-permissions")) { sender ->
            sender.hasPermission("randomteleport.world") || sender.hasPermission("randomteleport.world.others")
        }
    }
    private fun registerMessages() {
        commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION) { sender, _ ->
            messages[Messages.NO_PERMISSION].msg(sender)
        }
    }

    private fun warn(message: String) = logger.warning(message)
    private fun log(message: String) = logger.info(message)

    fun saveDefaultMessages() {
        if (dataFolder.resolve("messages.yml").exists()) return
        saveResource("messages.yml", false)
    }
}