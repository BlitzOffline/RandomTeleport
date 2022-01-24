package com.blitzoffline.randomteleport.util

import com.blitzoffline.randomteleport.RandomTeleport
import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.config.holder.Settings
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent

fun teleport(plugin: RandomTeleport, sender: CommandSender, player: Player, target: Player?, location: Location) {
    if (plugin.isPaper) {
        return teleportAsync(plugin, sender, player, target, location)
    }
    teleportSync(plugin, sender, player, target, location)
}

fun teleportAsync(plugin: RandomTeleport, sender: CommandSender, player: Player, target: Player?, location: Location) {
    player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept {
        plugin.cooldownHandler.warmups.remove(player.uniqueId)
        if (plugin.hooks["Vault"] == true && plugin.settings[Settings.TELEPORT_PRICE] > 0 && sender is Player && !sender.hasPermission("randomteleport.cost.bypass")) plugin.economy.withdrawPlayer(sender, plugin.settings[Settings.TELEPORT_PRICE].toDouble())
        if (plugin.settings[Settings.COOLDOWN] > 0 && !player.hasPermission("randomteleport.cooldown.bypass")) plugin.cooldownHandler.cooldowns[player.uniqueId] = System.currentTimeMillis()
        if (target != null) plugin.messages[Messages.TARGET_TELEPORTED_SUCCESSFULLY].parsePAPI(target).msg(sender)
        plugin.messages[Messages.TELEPORTED_SUCCESSFULLY].msg(player)
    }
    plugin.cooldownHandler.tasks.remove(player.uniqueId)
}

fun teleportSync(plugin: RandomTeleport, sender: CommandSender, player: Player, target: Player?, location: Location) {
    player.teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND)
    plugin.cooldownHandler.warmups.remove(player.uniqueId)
    if (plugin.hooks["Vault"] == true && plugin.settings[Settings.TELEPORT_PRICE] > 0 && sender is Player && !sender.hasPermission("randomteleport.cost.bypass")) plugin.economy.withdrawPlayer(sender, plugin.settings[Settings.TELEPORT_PRICE].toDouble())
    if (plugin.settings[Settings.COOLDOWN] > 0 && !player.hasPermission("randomteleport.cooldown.bypass")) plugin.cooldownHandler.cooldowns[player.uniqueId] = System.currentTimeMillis()
    if (target != null) plugin.messages[Messages.TARGET_TELEPORTED_SUCCESSFULLY].parsePAPI(target).msg(sender)
    plugin.messages[Messages.TELEPORTED_SUCCESSFULLY].msg(player)
    plugin.cooldownHandler.tasks.remove(player.uniqueId)
}