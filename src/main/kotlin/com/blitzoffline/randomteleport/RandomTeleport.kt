package com.blitzoffline.randomteleport

import com.blitzoffline.randomteleport.commands.CommandRTP
import com.blitzoffline.randomteleport.commands.CommandReload
import com.blitzoffline.randomteleport.commands.CommandRTPWorld
import com.blitzoffline.randomteleport.config.ConfigHandler
import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.config.holder.Settings
import com.blitzoffline.randomteleport.cooldown.CooldownHandler
import com.blitzoffline.randomteleport.listeners.DamageListener
import com.blitzoffline.randomteleport.listeners.InteractListener
import com.blitzoffline.randomteleport.listeners.MoveListener
import com.blitzoffline.randomteleport.placeholders.RandomTeleportPlaceholders
import com.blitzoffline.randomteleport.util.msg
import com.blitzoffline.randomteleport.util.registerLandsIntegration
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

    lateinit var settings: SettingsManager
        private set
    lateinit var messages: SettingsManager
        private set
    lateinit var economy: Economy
        private set

    override fun onEnable() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig")
        } catch (ignored: ClassNotFoundException) {
            warn("THIS PLUGIN SHOULD BE USED ON PAPERMC: papermc.io/download")
        }

        configHandler = ConfigHandler(this)
        settings = configHandler.loadConfig()
        messages = configHandler.loadMessages()

        setupHooks("PlaceholderAPI")
        if (settings[Settings.HOOK_WG]) setupHooks("WorldGuard")
        if (settings[Settings.HOOK_VAULT]) setupHooks("Vault")
        if (settings[Settings.HOOK_LANDS]) { setupHooks("Lands"); registerLandsIntegration(this) }

        cooldownHandler = CooldownHandler(this)

        registerListeners(
            DamageListener(this),
            InteractListener(this),
            MoveListener(this)
        )

        commandManager = CommandManager(this, true)
        registerMessage("cmd.no.permission") { sender -> settings[Messages.NO_PERMISSION].msg(sender) }
        registerCompletion("#worlds") { Bukkit.getWorlds().map(World::getName) }
        registerCommands(
            CommandRTP(this),
            CommandReload(this),
            CommandRTPWorld(this)
        )

        RandomTeleportPlaceholders(this).register()
        log("Plugin enabled successfully!")
    }

    override fun onDisable() { log("Plugin disabled successfully!") }

    fun setupHooks(plugin: String) {
        when {
            plugin == "Vault" -> {
                economy = configHandler.loadEconomy() ?: run {
                    log("Could not find Vault! This plugin is required")
                    pluginLoader.disablePlugin(this)
                    return
                }
                log("Successfully hooked into $plugin!")
                return
            }
            Bukkit.getPluginManager().getPlugin(plugin) == null -> {
                log("Could not find: $plugin. Plugin disabled!")
                pluginLoader.disablePlugin(this)
            }
            else -> log("Successfully hooked into $plugin!")
        }
    }

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