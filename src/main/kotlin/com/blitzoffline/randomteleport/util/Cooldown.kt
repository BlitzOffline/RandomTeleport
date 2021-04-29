package com.blitzoffline.randomteleport.util

import com.blitzoffline.randomteleport.config.holder.Settings
import com.blitzoffline.randomteleport.config.settings
import com.blitzoffline.randomteleport.cooldown.coolDowns
import org.bukkit.entity.Player

fun Player.isInCooldown() : Boolean {
    if (this.hasPermission("rtp.cooldown.bypass")) return false

    val lastCoolDown = coolDowns[this.uniqueId] ?: return false
    val currentTime = System.currentTimeMillis()
    val newCoolDown = settings[Settings.COOLDOWN] * 1000.toLong()

    if (currentTime-newCoolDown >= lastCoolDown) return false
    return true
}