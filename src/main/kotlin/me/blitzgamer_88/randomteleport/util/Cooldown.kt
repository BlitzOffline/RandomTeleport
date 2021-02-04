package me.blitzgamer_88.randomteleport.util

import org.bukkit.entity.Player

fun Player.isInCooldown() : Boolean {
    if (this.hasPermission("rtp.cooldown.bypass")) return false

    val lastCoolDown = coolDowns[this.uniqueId] ?: return false
    val currentTime = System.currentTimeMillis()
    val newCoolDown = cooldown * 1000.toLong()

    if (currentTime-newCoolDown >= lastCoolDown) return false
    return true
}