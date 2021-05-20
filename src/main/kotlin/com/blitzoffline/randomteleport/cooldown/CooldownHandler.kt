package com.blitzoffline.randomteleport.cooldown

import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.config.holder.Settings
import com.blitzoffline.randomteleport.config.settings
import com.blitzoffline.randomteleport.util.parsePAPI
import com.blitzoffline.randomteleport.util.msg
import org.bukkit.command.CommandSender
import java.util.UUID
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

val cooldowns = HashMap<UUID, Long>()
val warmupsStarted = mutableListOf<UUID>()
val tasks = HashMap<UUID, BukkitTask>()

fun Player.isInCooldown() : Boolean {
    if (this.hasPermission("randomteleport.cooldown.bypass")) return false

    val lastCooldown = cooldowns[this.uniqueId] ?: return false
    val currTime = System.currentTimeMillis()
    val newCooldown = settings[Settings.COOLDOWN] * 1000L

    if (currTime - newCooldown >= lastCooldown) return false
    return true
}

fun cooldownCheck(player: Player, target: Player?, sender: CommandSender) {
    if (player.isInCooldown()) {
        if (target == null) settings[Messages.COOLDOWN_REMAINING].replace("%cooldown%", replaceCooldown(player)).msg(player)
        else return settings[Messages.COOLDOWN_REMAINING_TARGET].replace("%cooldown%", replaceCooldown(player)).parsePAPI(target).msg(sender)
    }
}

private fun replaceCooldown(player: Player) = "${settings[Settings.COOLDOWN] - ((System.currentTimeMillis() - cooldowns[player.uniqueId]!!) / 1000)}"
