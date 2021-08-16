package com.blitzoffline.randomteleport.commands

import com.blitzoffline.randomteleport.RandomTeleport
import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.config.holder.Settings
import com.blitzoffline.randomteleport.util.msg
import com.blitzoffline.randomteleport.util.teleportAsync
import me.mattstudios.mf.annotations.Alias
import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Completion
import me.mattstudios.mf.annotations.Optional
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.annotations.SubCommand
import me.mattstudios.mf.base.CommandBase
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Command("randomteleport")
@Alias("rtp", "wild")
class CommandRTPWorld(private val plugin: RandomTeleport) : CommandBase() {
    private val settings = plugin.settings
    private val messages = plugin.messages

    @SubCommand("world")
    @Permission("randomteleport.world", "randomteleport.world.others")
    fun randomTeleportWorld(sender: CommandSender, @Completion("#worlds") teleportWorld: World?, @Completion("#players") @Optional target: Player?) {
        if (teleportWorld == null) return sendMessage("cmd.wrong.usage", sender)
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

        lateinit var randomLocation: Location
        var ok = false
        var attempts = 0
        while (!ok && attempts < settings[Settings.MAX_ATTEMPTS]) {
            randomLocation = plugin.locationHandler.getRandomLocation(teleportWorld, settings[Settings.USE_BORDER], settings[Settings.MAX_X], settings[Settings.MAX_Z])
            ok = plugin.locationHandler.isSafe(randomLocation)
            attempts++
        }

        if (!ok) {
            return messages[Messages.NO_SAFE_LOCATION_FOUND].msg(sender)
        }

        val centeredLocation = randomLocation.clone().add(0.5, 0.0, 0.5)

        if (settings[Settings.WARMUP] > 0 && !player.hasPermission("randomteleport.warmup.bypass")) {
            plugin.cooldownHandler.warmups.add(player.uniqueId)
            messages[Messages.WARMUP].msg(player)
            val warmupTime = settings[Settings.WARMUP] - (System.currentTimeMillis()- startTime) / 1000
            if (warmupTime > 0) {
                plugin.cooldownHandler.tasks[player.uniqueId] = Bukkit.getScheduler().runTaskLater(
                    plugin,
                    Runnable {
                        teleportAsync(plugin, sender, player, target, centeredLocation)
                    }, 20 * warmupTime
                )
                return
            }
        }

        teleportAsync(plugin, sender, player, target, centeredLocation)
    }
}