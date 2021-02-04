package me.blitzgamer_88.randomteleport.listeners

import me.blitzgamer_88.randomteleport.util.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class MoveListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun playerMove(event: PlayerMoveEvent) {
        val player = event.player
        val startingLocation = event.from
        val finalLocation = event.to
        val distanceSquared = startingLocation.distanceSquared(finalLocation)
        if (distanceSquared < 0.05) return
        if (tasks[player.uniqueId] == null) return

        tasks[player.uniqueId]?.cancel()
        tasks.remove(player.uniqueId)
        warmupsStarted.remove(player.uniqueId)
        teleportCanceled.replace("%reason%", movedReason).msg(player)
    }
}