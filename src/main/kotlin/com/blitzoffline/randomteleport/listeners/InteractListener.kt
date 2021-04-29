package com.blitzoffline.randomteleport.listeners

import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.config.messages
import com.blitzoffline.randomteleport.cooldown.tasks
import com.blitzoffline.randomteleport.cooldown.warmupsStarted
import com.blitzoffline.randomteleport.util.msg
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
        messages[Messages.TELEPORT_CANCELED].replace("%reason%", messages[Messages.BROKE_A_BLOCK]).msg(player)
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        if (tasks[player.uniqueId] == null) return

        tasks[player.uniqueId]?.cancel()
        tasks.remove(player.uniqueId)
        warmupsStarted.remove(player.uniqueId)
        messages[Messages.TELEPORT_CANCELED].replace("%reason%", messages[Messages.PLACED_A_BLOCK]).msg(player)
    }

}