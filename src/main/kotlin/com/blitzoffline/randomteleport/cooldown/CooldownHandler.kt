package com.blitzoffline.randomteleport.cooldown

import com.blitzoffline.randomteleport.RandomTeleport
import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.config.holder.Settings
import com.blitzoffline.randomteleport.util.msg
import com.blitzoffline.randomteleport.util.parsePAPI
import java.util.UUID
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

class CooldownHandler(plugin: RandomTeleport) {
    private val settings = plugin.settings
    private val messages = plugin.messages

    val cooldowns = HashMap<UUID, Long>()
    val warmups = mutableListOf<UUID>()
    val tasks = HashMap<UUID, BukkitTask>()

    fun inWarmup(player: Player): Boolean {
        if (player.hasPermission("randomteleport.warmup.bypass")) return false
        return warmups.contains(player.uniqueId)
    }

    private fun Player.isInCooldown(): Boolean {
        if (this.hasPermission("randomteleport.cooldown.bypass")) return false

        val lastCooldown = cooldowns[this.uniqueId] ?: return false
        val currTime = System.currentTimeMillis()
        val newCooldown = settings[Settings.COOLDOWN] * 1000L

        if (currTime - newCooldown >= lastCooldown) return false
        return true
    }

    fun cooldownCheck(player: Player, target: Player?, sender: CommandSender) {
        if (player.isInCooldown()) {
            if (target == null) messages[Messages.COOLDOWN_REMAINING].replace("%cooldown%", replaceCooldown(player))
                .msg(player)
            else return messages[Messages.COOLDOWN_REMAINING_TARGET].replace("%cooldown%", replaceCooldown(target))
                .parsePAPI(target).msg(sender)
        }
    }

    private fun replaceCooldown(player: Player) =
        "${settings[Settings.COOLDOWN] - ((System.currentTimeMillis() - cooldowns[player.uniqueId]!!) / 1000)}"

}
