package com.blitzoffline.randomteleport.tasks

import com.blitzoffline.randomteleport.RandomTeleport
import com.blitzoffline.randomteleport.config.holder.Settings
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

class GenerateTeleportLocations(private val plugin: RandomTeleport) : BukkitRunnable() {
    override fun run() {
        plugin.locations.forEach { entry ->
            if (entry.value.size >= 15) {
                return@forEach
            }

            while (entry.value.size < 15) {
                val world = Bukkit.getWorld(entry.key) ?: return@forEach
                val newLocation = plugin.locationHandler.getRandomLocation(
                    world, plugin.settings[Settings.USE_BORDER],
                    plugin.settings[Settings.MAX_X], plugin.settings[Settings.MAX_X],
                    plugin.settings[Settings.MAX_ATTEMPTS]) ?: return@forEach
                plugin.locations[entry.key]?.add(newLocation)
            }

        }
    }
}