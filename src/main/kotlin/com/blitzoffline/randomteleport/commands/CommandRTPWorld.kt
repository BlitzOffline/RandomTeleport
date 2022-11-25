package com.blitzoffline.randomteleport.commands

import com.blitzoffline.randomteleport.RandomTeleport
import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.config.holder.Settings
import com.blitzoffline.randomteleport.util.msg
import com.blitzoffline.randomteleport.util.teleport
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Optional
import dev.triumphteam.cmd.core.annotation.Requirement
import dev.triumphteam.cmd.core.annotation.SubCommand
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Command(value = "randomteleport", alias = ["rtp", "wild"])
class CommandRTPWorld(private val plugin: RandomTeleport) : BaseCommand() {
    private val settings = plugin.settings
    private val messages = plugin.messages

    @SubCommand("world")
    @Requirement("rtp-world-permissions")
    fun randomTeleportWorld(sender: CommandSender, teleportWorld: World, @Optional target: Player?) {
        val startTime = System.currentTimeMillis()

        if (target == null && sender !is Player) {
            return messages[Messages.NO_TARGET_SPECIFIED].msg(sender)
        }

        val player = target ?: sender as Player

        if (target == null && !player.hasPermission("randomteleport.world")) {
            return messages[Messages.NO_PERMISSION].msg(player)
        }

        if (target != null && !sender.hasPermission("randomteleport.world.others")) {
            return messages[Messages.NO_PERMISSION].msg(sender)
        }

        if (sender is Player && plugin.hooks["Vault"] == true && settings[Settings.TELEPORT_PRICE] > 0 && plugin.economy.getBalance(sender) < settings[Settings.TELEPORT_PRICE] && !player.hasPermission("randomteleport.cost.bypass")) {
            return messages[Messages.NOT_ENOUGH_MONEY].msg(sender)
        }

        if (plugin.cooldownHandler.inWarmup(player, target, sender)) return
        if (plugin.cooldownHandler.inCooldown(player, target, sender)) return

        val randomLocation = plugin.locationHandler.getRandomSafeLocation(teleportWorld, settings[Settings.USE_BORDER], settings[Settings.MAX_X], settings[Settings.MAX_Z], settings[Settings.MAX_ATTEMPTS])
            ?: return messages[Messages.NO_SAFE_LOCATION_FOUND].msg(sender)

        val centeredLocation = randomLocation.clone().add(0.5, 0.0, 0.5)

        if (settings[Settings.WARMUP] > 0 && !player.hasPermission("randomteleport.warmup.bypass")) {
            plugin.cooldownHandler.warmups.add(player.uniqueId)
            messages[Messages.WARMUP].msg(player)
            val warmupTime = settings[Settings.WARMUP] - (System.currentTimeMillis()- startTime) / 1000
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