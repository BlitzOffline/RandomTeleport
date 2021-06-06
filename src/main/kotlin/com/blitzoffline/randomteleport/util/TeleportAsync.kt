package com.blitzoffline.randomteleport.util

import com.blitzoffline.randomteleport.RandomTeleport
import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.config.holder.Settings
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent

fun teleportAsync(plugin: RandomTeleport, sender: CommandSender, player: Player, target: Player?, location: Location) {
    location.world.getChunkAtAsync(location).thenAccept {
        player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept {
            plugin.cooldownHandler.warmupsStarted.remove(player.uniqueId)
            if (plugin.settings[Settings.TELEPORT_PRICE] > 0 && sender is Player && !sender.hasPermission("randomteleport.cost.bypass")) plugin.economy.withdrawPlayer(sender, plugin.settings[Settings.TELEPORT_PRICE].toDouble())
            if (plugin.settings[Settings.COOLDOWN] > 0) plugin.cooldownHandler.cooldowns[player.uniqueId] = System.currentTimeMillis()
            if (target != null) plugin.settings[Messages.TARGET_TELEPORTED_SUCCESSFULLY].parsePAPI(target).msg(sender)
            plugin.settings[Messages.TELEPORTED_SUCCESSFULLY].msg(player)
        }
    }
    plugin.cooldownHandler.tasks.remove(player.uniqueId)
}