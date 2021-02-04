package me.blitzgamer_88.randomteleport.listeners

import me.blitzgamer_88.randomteleport.util.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

class InteractListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onPlayerBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        if (tasks[player.uniqueId] == null) return

        tasks[player.uniqueId]?.cancel()
        tasks.remove(player.uniqueId)
        warmupsStarted.remove(player.uniqueId)
        teleportCanceled.replace("%reason%", brokeABlockReason).msg(player)
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        if (tasks[player.uniqueId] == null) return

        tasks[player.uniqueId]?.cancel()
        tasks.remove(player.uniqueId)
        warmupsStarted.remove(player.uniqueId)
        teleportCanceled.replace("%reason%", placedABlockReason).msg(player)
    }

}