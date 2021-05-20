package com.blitzoffline.randomteleport.listeners

import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.config.settings
import com.blitzoffline.randomteleport.cooldown.tasks
import com.blitzoffline.randomteleport.cooldown.warmupsStarted
import com.blitzoffline.randomteleport.util.msg
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

class InteractListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun BlockBreakEvent.onPlayerBlockBreak() {
        if (tasks[player.uniqueId] == null) return

        tasks[player.uniqueId]?.cancel()
        tasks.remove(player.uniqueId)
        warmupsStarted.remove(player.uniqueId)
        settings[Messages.TELEPORT_CANCELED].replace("%reason%", settings[Messages.BROKE_A_BLOCK]).msg(player)
    }

    @EventHandler(ignoreCancelled = true)
    fun BlockPlaceEvent.onPlayerBlockPlace() {
        if (tasks[player.uniqueId] == null) return

        tasks[player.uniqueId]?.cancel()
        tasks.remove(player.uniqueId)
        warmupsStarted.remove(player.uniqueId)
        settings[Messages.TELEPORT_CANCELED].replace("%reason%", settings[Messages.PLACED_A_BLOCK]).msg(player)
    }

}