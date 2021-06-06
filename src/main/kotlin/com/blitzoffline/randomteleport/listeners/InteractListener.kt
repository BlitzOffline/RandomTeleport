package com.blitzoffline.randomteleport.listeners

import com.blitzoffline.randomteleport.RandomTeleport
import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.util.msg
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

class InteractListener(private val plugin: RandomTeleport) : Listener {
    private val messages = plugin.messages

    @EventHandler(ignoreCancelled = true)
    fun BlockBreakEvent.onPlayerBlockBreak() {
        if (plugin.cooldownHandler.tasks[player.uniqueId] == null) return

        plugin.cooldownHandler.tasks[player.uniqueId]?.cancel()
        plugin.cooldownHandler.tasks.remove(player.uniqueId)
        plugin.cooldownHandler.warmups.remove(player.uniqueId)
        messages[Messages.TELEPORT_CANCELED].replace("%reason%", messages[Messages.BROKE_A_BLOCK]).msg(player)
    }

    @EventHandler(ignoreCancelled = true)
    fun BlockPlaceEvent.onPlayerBlockPlace() {
        if (plugin.cooldownHandler.tasks[player.uniqueId] == null) return

        plugin.cooldownHandler.tasks[player.uniqueId]?.cancel()
        plugin.cooldownHandler.tasks.remove(player.uniqueId)
        plugin.cooldownHandler.warmups.remove(player.uniqueId)
        messages[Messages.TELEPORT_CANCELED].replace("%reason%", messages[Messages.PLACED_A_BLOCK]).msg(player)
    }

}