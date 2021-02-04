package me.blitzgamer_88.randomteleport

import me.angeschossen.lands.api.integration.LandsIntegration
import me.blitzgamer_88.randomteleport.commands.CommandRandomTeleport
import me.blitzgamer_88.randomteleport.listeners.DamageListener
import me.blitzgamer_88.randomteleport.listeners.InteractListener
import me.blitzgamer_88.randomteleport.listeners.MoveListener
import me.blitzgamer_88.randomteleport.placeholders.RandomTeleportPlaceholders
import me.blitzgamer_88.randomteleport.util.*
import me.bristermitten.pdm.PluginDependencyManager
import me.mattstudios.mf.base.CommandBase
import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mf.base.components.CompletionResolver
import me.mattstudios.mf.base.components.MessageResolver
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class RandomTeleport : JavaPlugin() {
    override fun onLoad() { PluginDependencyManager.of(this).loadAllDependencies() }
    private lateinit var commandManager: CommandManager

    override fun onEnable() {
        this.saveDefaultConfig()
        loadConfig(this)

        loadSettings()
        loadMessages()

        dependenciesHook("PlaceholderAPI")
        if (useLands) { dependenciesHook("Lands"); registerLandsIntegration(this) }
        if (useWorldGuard) dependenciesHook("WorldGuard")

        registerListeners(
            DamageListener(),
            InteractListener(),
            MoveListener()
        )
        commandManager = CommandManager(this, true)
        registerMessage("cmd.no.permission") { sender -> noPermission.msg(sender) }
        registerCompletion("#worlds") { Bukkit.getWorlds().map(World::getName) }
        registerCommands(CommandRandomTeleport(this))

        RandomTeleportPlaceholders(this).register()
        "[RandomTeleport] Plugin enabled successfully!".log()
    }

    override fun onDisable() { "[RandomTeleport] Plugin disabled successfully!".log() }

    fun reload() {
        conf.reload()
        loadSettings()
        loadMessages()
    }

    private fun dependenciesHook(plugin: String) {
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