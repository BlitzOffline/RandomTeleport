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
    if (this.hasPermission("randomteleport.cooldown.bypass")) return false

    val lastCooldown = cooldowns[this.uniqueId] ?: return false
    val currTime = System.currentTimeMillis()
    val newCooldown = settings[Settings.COOLDOWN] * 1000L

    if (currTime - newCooldown >= lastCooldown) return false
    return true
}