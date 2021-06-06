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
        val player = entity
        if (player !is Player) return
        if (plugin.cooldownHandler.tasks[player.uniqueId] == null) return

        plugin.cooldownHandler.tasks[player.uniqueId]?.cancel()
        plugin.cooldownHandler.tasks.remove(player.uniqueId)
        plugin.cooldownHandler.warmupsStarted.remove(player.uniqueId)
        messages[Messages.TELEPORT_CANCELED].replace("%reason%", messages[Messages.GOT_HURT]).msg(player)
    }

    @EventHandler(ignoreCancelled = true)
    fun EntityDamageByEntityEvent.onPlayerHit() {
        val attacker = damager
        if (attacker !is Player) return
        if (plugin.cooldownHandler.tasks[attacker.uniqueId] == null) return

        plugin.cooldownHandler.tasks[attacker.uniqueId]?.cancel()
        plugin.cooldownHandler.tasks.remove(attacker.uniqueId)
        plugin.cooldownHandler.warmupsStarted.remove(attacker.uniqueId)
        messages[Messages.TELEPORT_CANCELED].replace("%reason%", messages[Messages.HURT]).msg(attacker)
    }
}