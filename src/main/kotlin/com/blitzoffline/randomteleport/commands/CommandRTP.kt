package com.blitzoffline.randomteleport.commands

import com.blitzoffline.randomteleport.RandomTeleport
import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.config.holder.Settings
import com.blitzoffline.randomteleport.util.msg
import com.blitzoffline.randomteleport.util.teleport
import me.mattstudios.mf.annotations.Alias
import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Completion
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.annotations.Optional
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.base.CommandBase
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Command("randomteleport")
@Alias("rtp", "wild")
class CommandRTP(private val plugin: RandomTeleport) : CommandBase() {
    private val settings = plugin.settings
    private val messages = plugin.messages

    @Default
    @Permission("randomteleport.self", "randomteleport.others")
    fun randomTeleport(sender: CommandSender, @Completion("#players") @Optional target: Player?) {
        val startTime = System.currentTimeMillis()

        if (target == null && sender !is Player) {
            return messages[Messages.NO_TARGET_SPECIFIED].msg(sender)
        }

        val player = target ?: sender as Player

        if (target == null && !player.hasPermission("randomteleport.self")) {
            return messages[Messages.NO_PERMISSION].msg(player)
        }

        if (target != null && !sender.hasPermission("randomteleport.others")) {
            return messages[Messages.NO_PERMISSION].msg(sender)
        }

        if (sender is Player && plugin.hooks["Vault"] == true && settings[Settings.TELEPORT_PRICE] > 0 && plugin.economy.getBalance(sender) < settings[Settings.TELEPORT_PRICE] && !player.hasPermission("randomteleport.cost.bypass")) {
            return messages[Messages.NOT_ENOUGH_MONEY].msg(sender)
        }

        if (plugin.cooldownHandler.inWarmup(player, target, sender)) return
        if (plugin.cooldownHandler.inCooldown(player, target, sender)) return

        val worlds = if(settings[Settings.ENABLED_WORLDS].contains("all")) Bukkit.getWorlds() else settings[Settings.ENABLED_WORLDS].mapNotNull { Bukkit.getWorld(it) }
        if (worlds.isEmpty()) {
            return messages[Messages.CONFIG_WORLDS_WRONG].msg(player)
        }

        val teleportWorld = worlds.random()


        val randomLocation = plugin.locationHandler.getRandomLocation(teleportWorld, settings[Settings.USE_BORDER], settings[Settings.MAX_X], settings[Settings.MAX_Z], settings[Settings.MAX_ATTEMPTS])
            ?: return messages[Messages.NO_SAFE_LOCATION_FOUND].msg(sender)

        val centeredLocation = randomLocation.clone().add(0.5, 0.0, 0.5)

        if (settings[Settings.WARMUP] > 0 && !player.hasPermission("randomteleport.warmup.bypass")) {
            plugin.cooldownHandler.warmups.add(player.uniqueId)
            messages[Messages.WARMUP].msg(player)
            val warmupTime = settings[Settings.WARMUP] - (System.currentTimeMillis() - startTime) / 1000
            if (warmupTime > 0) {
                plugin.cooldownHandler.tasks[player.uniqueId] = Bukkit.getScheduler().runTaskLater(
                    plugin,
                    Runnable {
                        teleport(plugin, sender, player, target, centeredLocation)
                    }, 20 * warmupTime
                )
                return
            }
        }

        teleport(plugin, sender, player, target, centeredLocation)
    }
}