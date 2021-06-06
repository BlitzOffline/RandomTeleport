package com.blitzoffline.randomteleport.commands

import com.blitzoffline.randomteleport.RandomTeleport
import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.config.holder.Settings
import com.blitzoffline.randomteleport.util.getRandomLocation
import com.blitzoffline.randomteleport.util.isSafe
import com.blitzoffline.randomteleport.util.msg
import com.blitzoffline.randomteleport.util.parsePAPI
import com.blitzoffline.randomteleport.util.teleportAsync
import me.mattstudios.mf.annotations.Alias
import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Completion
import me.mattstudios.mf.annotations.Optional
import me.mattstudios.mf.annotations.SubCommand
import me.mattstudios.mf.base.CommandBase
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Command("randomteleport")
@Alias("rtp", "wild")
class CommandRTPWorld(private val plugin: RandomTeleport) : CommandBase() {
    private val settings = plugin.settings
    private val messages = plugin.messages

    @SubCommand("world")
    fun randomTeleportWorld(sender: CommandSender, @Completion("#worlds") worldName: String, @Completion("#players") @Optional target: Player?) {
        val startTime = System.currentTimeMillis()

        if (target == null && sender !is Player) {
            return messages[Messages.NO_TARGET_SPECIFIED].msg(sender)
        }

        val player = target ?: sender as Player

        if (target == null && !player.hasPermission("randomteleport.world") && !player.hasPermission("randomteleport.world.${worldName.lowercase()}")) {
            return messages[Messages.NO_PERMISSION].msg(player)
        }

        if (target != null && !sender.hasPermission("randomteleport.world.others")) {
            return messages[Messages.NO_PERMISSION].msg(sender)
        }

        if (sender is Player && settings[Settings.HOOK_VAULT] && settings[Settings.TELEPORT_PRICE] > 0 && plugin.economy.getBalance(sender) < settings[Settings.TELEPORT_PRICE] && !player.hasPermission("randomteleport.cost.bypass")) {
            return messages[Messages.NOT_ENOUGH_MONEY].msg(sender)
        }

        if (plugin.cooldownHandler.warmupsStarted.contains(player.uniqueId) && !player.hasPermission("randomteleport.warmup.bypass")) {
            if (player == sender) messages[Messages.ALREADY_TELEPORTING].msg(player)
            else return messages[Messages.ALREADY_TELEPORTING_TARGET].parsePAPI(player).msg(sender)
        }

        val teleportWorld = Bukkit.getWorld(worldName) ?: return messages[Messages.WRONG_WORLD_NAME].msg(sender)

        plugin.cooldownHandler.cooldownCheck(player, target, sender)

        lateinit var randomLocation: Location

        var ok = false
        var attempts = 0
        while (!ok && attempts <= settings[Settings.MAX_ATTEMPTS]) {
            randomLocation = getRandomLocation(teleportWorld, settings[Settings.USE_BORDER], settings[Settings.MAX_X], settings[Settings.MAX_Z])
            ok = randomLocation.isSafe(settings)
            attempts++
        }

        if (!ok) {
            return messages[Messages.NO_SAFE_LOCATION_FOUND].msg(sender)
        }

        val newLocation = randomLocation.clone().add(0.5, 0.0, 0.5)
        plugin.cooldownHandler.warmupsStarted.add(player.uniqueId)

        if (settings[Settings.WARMUP] > 0 && !player.hasPermission("randomteleport.warmup.bypass")) {
            messages[Messages.WARMUP].msg(player)
            val warmupTime = settings[Settings.WARMUP] - (System.currentTimeMillis()- startTime) / 1000
            if (warmupTime > 0) {
                plugin.cooldownHandler.tasks[player.uniqueId] = Bukkit.getScheduler().runTaskLater(
                    plugin,
                    Runnable {
                        teleportAsync(plugin, sender, player, target, newLocation)
                    }, 20 * warmupTime
                )
            }
        }

        teleportAsync(plugin, sender, player, target, newLocation)
    }
}