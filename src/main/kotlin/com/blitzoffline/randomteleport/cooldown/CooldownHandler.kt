package com.blitzoffline.randomteleport.cooldown

import com.blitzoffline.randomteleport.config.holder.Settings
import com.blitzoffline.randomteleport.config.settings
import java.util.UUID
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

val cooldowns = HashMap<UUID, Long>()
val warmupsStarted = mutableListOf<UUID>()
val tasks = HashMap<UUID, BukkitTask>()

fun Player.isInCooldown() : Boolean {
    if (this.hasPermission("rtp.cooldown.bypass")) return false

    val lastCoolDown = cooldowns[this.uniqueId] ?: return false
    val currentTime = System.currentTimeMillis()
    val newCoolDown = settings[Settings.COOLDOWN] * 1000.toLong()

    if (currentTime-newCoolDown >= lastCoolDown) return false
    return true
}