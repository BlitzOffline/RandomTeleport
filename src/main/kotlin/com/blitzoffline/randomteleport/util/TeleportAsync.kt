package com.blitzoffline.randomteleport.util

import com.blitzoffline.randomteleport.config.econ
import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.config.holder.Settings
import com.blitzoffline.randomteleport.config.settings
import com.blitzoffline.randomteleport.cooldown.cooldowns
import com.blitzoffline.randomteleport.cooldown.tasks
import com.blitzoffline.randomteleport.cooldown.warmupsStarted
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent

fun teleportAsync(sender: CommandSender, player: Player, target: Player?, location: Location) {
    location.world.getChunkAtAsync(location).thenAccept {
        player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept {
            warmupsStarted.remove(player.uniqueId)
            if (settings[Settings.TELEPORT_PRICE] > 0 && sender is Player && !sender.hasPermission("randomteleport.cost.bypass")) econ.withdrawPlayer(sender, settings[Settings.TELEPORT_PRICE].toDouble())
            if (settings[Settings.COOLDOWN] > 0) cooldowns[player.uniqueId] = System.currentTimeMillis()
            if (target != null) settings[Messages.TARGET_TELEPORTED_SUCCESSFULLY].parsePAPI(target).msg(sender)
            settings[Messages.TELEPORTED_SUCCESSFULLY].msg(player)
        }
    }
    tasks.remove(player.uniqueId)
}