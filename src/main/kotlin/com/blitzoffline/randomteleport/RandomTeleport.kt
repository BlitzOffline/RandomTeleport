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
import com.blitzoffline.randomteleport.util.log
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

    lateinit var settings: SettingsManager
    lateinit var messages: SettingsManager
    lateinit var economy: Economy

    override fun onEnable() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig")
        } catch (ignored: ClassNotFoundException) {
            "&cTHIS PLUGIN SHOULD BE USED ON PAPER: papermc.io/download".log()
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
            DamageListener(),
            InteractListener(),
            MoveListener()
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
        "[RandomTeleport] Plugin enabled successfully!".log()
    }

    override fun onDisable() { "[RandomTeleport] Plugin disabled successfully!".log() }

    fun setupHooks(plugin: String) {
        when {
            plugin == "Vault" -> {
                economy = configHandler.loadEconomy() ?: run {
                    "[RandomTeleport] Could not find Vault! This plugin is required".log()
                    pluginLoader.disablePlugin(this)
                    return
                }
                "&7[RandomTeleport] Successfully hooked into $plugin!".log()
                return
            }
            Bukkit.getPluginManager().getPlugin(plugin) == null -> {
                "&7[RandomTeleport] Could not find: $plugin. Plugin disabled!".log()
                Bukkit.getPluginManager().disablePlugin(this)
            }
            else -> "&7[RandomTeleport] Successfully hooked into $plugin!".log()
        }
    }

    private fun registerListeners(vararg listeners: Listener) = listeners.forEach { server.pluginManager.registerEvents(it, this) }
    private fun registerCommands(vararg commands: CommandBase) = commands.forEach(commandManager::register)
    private fun registerCompletion(completionId: String, resolver: CompletionResolver) = commandManager.completionHandler.register(completionId, resolver)
    private fun registerMessage(messageId: String, resolver: MessageResolver) = commandManager.messageHandler.register(messageId, resolver)

    fun saveDefaultMessages() {
        if (dataFolder.resolve("messages.yml").exists()) return
        saveResource("messages.yml", false)
    }
}