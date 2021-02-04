package me.blitzgamer_88.randomteleport.listeners

import me.blitzgamer_88.randomteleport.util.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

class DamageListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun playerDamaged(event: EntityDamageEvent) {
        val player = event.entity
        if (player !is Player) return
        if (tasks[player.uniqueId] == null) return

        tasks[player.uniqueId]?.cancel()
        tasks.remove(player.uniqueId)
        warmupsStarted.remove(player.uniqueId)
        teleportCanceled.replace("%reason%", gotHurtReason).msg(player)
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerHit(event: EntityDamageByEntityEvent) {
        val attacker = event.damager
        if (attacker !is Player) return
        if (tasks[attacker.uniqueId] == null) return

        tasks[attacker.uniqueId]?.cancel()
        tasks.remove(attacker.uniqueId)
        warmupsStarted.remove(attacker.uniqueId)
        teleportCanceled.replace("%reason%", hurtReason).msg(attacker)
    }
}