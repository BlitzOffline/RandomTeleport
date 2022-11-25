package com.blitzoffline.randomteleport.world

import com.blitzoffline.randomteleport.exception.InvalidWorldException
import org.bukkit.Bukkit
import org.bukkit.Location

data class TeleportWorldLocation(val x: Double, val y: Double, val z: Double, val name: String) {
    fun toBukkitLocation() =
        Location(Bukkit.getWorld(name) ?: throw InvalidWorldException("Could not find the world: $name"), x, y, z)
}