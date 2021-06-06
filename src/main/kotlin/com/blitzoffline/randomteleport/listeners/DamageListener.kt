package com.blitzoffline.randomteleport.listeners

import com.blitzoffline.randomteleport.RandomTeleport
import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.util.msg
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

class DamageListener(private val plugin: RandomTeleport) : Listener {
    private val messages = plugin.messages

    @EventHandler(ignoreCancelled = true)
    fun EntityDamageEvent.playerDamaged() {
        if (entity !is Player) return
        if (plugin.cooldownHandler.tasks[entity.uniqueId] == null) return

        plugin.cooldownHandler.tasks[entity.uniqueId]?.cancel()
        plugin.cooldownHandler.tasks.remove(entity.uniqueId)
        plugin.cooldownHandler.warmups.remove(entity.uniqueId)
        messages[Messages.TELEPORT_CANCELED].replace("%reason%", messages[Messages.GOT_HURT]).msg(entity)
    }

    @EventHandler(ignoreCancelled = true)
    fun EntityDamageByEntityEvent.onPlayerHit() {
        if (damager !is Player) return
        if (plugin.cooldownHandler.tasks[damager.uniqueId] == null) return

        plugin.cooldownHandler.tasks[damager.uniqueId]?.cancel()
        plugin.cooldownHandler.tasks.remove(damager.uniqueId)
        plugin.cooldownHandler.warmups.remove(damager.uniqueId)
        messages[Messages.TELEPORT_CANCELED].replace("%reason%", messages[Messages.HURT]).msg(damager)
    }
}