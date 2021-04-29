package com.blitzoffline.randomteleport

import com.blitzoffline.randomteleport.commands.MainCommand
import com.blitzoffline.randomteleport.commands.ReloadCommand
import com.blitzoffline.randomteleport.commands.WorldCommand
import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.config.holder.Settings
import com.blitzoffline.randomteleport.config.loadConfig
import com.blitzoffline.randomteleport.config.loadMessages
import com.blitzoffline.randomteleport.config.messages
import com.blitzoffline.randomteleport.config.settings
import com.blitzoffline.randomteleport.config.setupEconomy
import com.blitzoffline.randomteleport.listeners.DamageListener
import com.blitzoffline.randomteleport.listeners.InteractListener
import com.blitzoffline.randomteleport.listeners.MoveListener
import com.blitzoffline.randomteleport.placeholders.RandomTeleportPlaceholders
import com.blitzoffline.randomteleport.util.adventure
import com.blitzoffline.randomteleport.util.log
import com.blitzoffline.randomteleport.util.msg
import com.blitzoffline.randomteleport.util.registerLandsIntegration
import me.mattstudios.mf.base.CommandBase
import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mf.base.components.CompletionResolver
import me.mattstudios.mf.base.components.MessageResolver
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class RandomTeleport : JavaPlugin() {
    private lateinit var commandManager: CommandManager

    override fun onEnable() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig")
        } catch (ignored: ClassNotFoundException) {
            "&cTHIS PLUGIN SHOULD BE USED ON PAPER: papermc.io/download".log()
        }

        loadConfig(this)
        loadMessages(this)

        dependenciesHook("PlaceholderAPI")
        if (settings[Settings.HOOK_WG]) dependenciesHook("WorldGuard")
        if (settings[Settings.HOOK_VAULT]) dependenciesHook("Vault")
        if (settings[Settings.HOOK_LANDS]) { dependenciesHook("Lands"); registerLandsIntegration(this) }

        registerListeners(
            DamageListener(),
            InteractListener(),
            MoveListener()
        )
        adventure = BukkitAudiences.create(this)
        commandManager = CommandManager(this, true)
        registerMessage("cmd.no.permission") { sender -> messages[Messages.NO_PERMISSION].msg(sender) }
        registerCompletion("#worlds") { Bukkit.getWorlds().map(World::getName) }
        registerCommands(
            MainCommand(this),
            ReloadCommand(this),
            WorldCommand(this)
        )

        RandomTeleportPlaceholders(this).register()
        "[RandomTeleport] Plugin enabled successfully!".log()
    }

    override fun onDisable() { "[RandomTeleport] Plugin disabled successfully!".log() }

    fun reload() {
        settings.reload()
        messages.reload()
    }

    private fun dependenciesHook(plugin: String) {
        if (plugin == "Vault") {
            if (!setupEconomy()) {
                "&7[RandomTeleport] Could not find: $plugin. Plugin disabled!".log()
                Bukkit.getPluginManager().disablePlugin(this)
            }
            "&7[RandomTeleport] Successfully hooked into $plugin!".log()
            return
        }
        if (Bukkit.getPluginManager().getPlugin(plugin) == null) {
            "&7[RandomTeleport] Could not find: $plugin. Plugin disabled!".log()
            Bukkit.getPluginManager().disablePlugin(this)
        }
        else { "&7[RandomTeleport] Successfully hooked into $plugin!".log() }
    }

    private fun registerListeners(vararg listeners: Listener) = listeners.forEach { server.pluginManager.registerEvents(it, this) }
    private fun registerCommands(vararg commands: CommandBase) = commands.forEach(commandManager::register)
    private fun registerCompletion(completionId: String, resolver: CompletionResolver) = commandManager.completionHandler.register(completionId, resolver)
    private fun registerMessage(messageId: String, resolver: MessageResolver) = commandManager.messageHandler.register(messageId, resolver)
}