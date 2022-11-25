package com.blitzoffline.randomteleport.listeners

import com.blitzoffline.randomteleport.RandomTeleport
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerLoadEvent

class ServerLoadListener(private val plugin: RandomTeleport) : Listener {
    @EventHandler
    fun ServerLoadEvent.onServerLoad() {
        plugin.enableHooks()
    }
}