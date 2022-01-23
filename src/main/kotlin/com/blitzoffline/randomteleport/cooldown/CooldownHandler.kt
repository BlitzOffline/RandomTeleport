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

    fun inWarmup(player: Player, target: Player?, sender: CommandSender): Boolean {
        if (!player.isInWarmup()) return false
        if (target == null) messages[Messages.ALREADY_TELEPORTING].msg(player)
        else messages[Messages.ALREADY_TELEPORTING_TARGET].parsePAPI(player).msg(sender)
        return true
    }

    fun inCooldown(player: Player, target: Player?, sender: CommandSender): Boolean {
        if (!player.isInCooldown()) return false
        if (target == null) messages[Messages.COOLDOWN_REMAINING].replace("%cooldown%", replaceCooldown(player)).msg(player)
        else  messages[Messages.COOLDOWN_REMAINING_TARGET].replace("%cooldown%", replaceCooldown(target)).parsePAPI(target).msg(sender)
        return true
    }

    private fun Player.isInWarmup(): Boolean {
        if (this.hasPermission("randomteleport.warmup.bypass")) return false
        return warmups.contains(this.uniqueId)
    }

    private fun Player.isInCooldown(): Boolean {
        if (this.hasPermission("randomteleport.cooldown.bypass")) return false
        val lastCooldown = cooldowns[this.uniqueId] ?: return false

        val currTime = System.currentTimeMillis()
        val cooldown = settings[Settings.COOLDOWN] * 1000L

        if (currTime - lastCooldown >= cooldown) return false
        return true
    }

    private fun replaceCooldown(player: Player) = "${settings[Settings.COOLDOWN] - ((System.currentTimeMillis() - cooldowns[player.uniqueId]!!) / 1000)}"
}
