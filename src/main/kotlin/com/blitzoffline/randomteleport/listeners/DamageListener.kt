package com.blitzoffline.randomteleport.listeners

import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.config.messages
import com.blitzoffline.randomteleport.cooldown.tasks
import com.blitzoffline.randomteleport.cooldown.warmupsStarted
import com.blitzoffline.randomteleport.util.msg
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

class DamageListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun EntityDamageEvent.playerDamaged() {
        val player = entity
        if (player !is Player) return
        if (tasks[player.uniqueId] == null) return

        tasks[player.uniqueId]?.cancel()
        tasks.remove(player.uniqueId)
        warmupsStarted.remove(player.uniqueId)
        messages[Messages.TELEPORT_CANCELED].replace("%reason%", messages[Messages.GOT_HURT]).msg(player)
    }

    @EventHandler(ignoreCancelled = true)
    fun EntityDamageByEntityEvent.onPlayerHit() {
        val attacker = damager
        if (attacker !is Player) return
        if (tasks[attacker.uniqueId] == null) return

        tasks[attacker.uniqueId]?.cancel()
        tasks.remove(attacker.uniqueId)
        warmupsStarted.remove(attacker.uniqueId)
        messages[Messages.TELEPORT_CANCELED].replace("%reason%", messages[Messages.HURT]).msg(attacker)
    }
}