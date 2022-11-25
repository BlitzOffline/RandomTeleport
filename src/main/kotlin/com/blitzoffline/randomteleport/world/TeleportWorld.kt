package com.blitzoffline.randomteleport.world

import com.blitzoffline.randomteleport.exception.InvalidWorldException
import com.blitzoffline.randomteleport.search.SafetySearchType
import java.util.LinkedList
import org.bukkit.Bukkit
import org.bukkit.World

data class TeleportWorld(
    val name: String,
    val firstTeleportPrice: Int,
    val teleportPrice: Int,
    val useBorder: Boolean,
    val minX: Int,
    val minZ: Int,
    val maxX: Int,
    val maxZ: Int,
    val safetySearchType: SafetySearchType,
    val cooldown: Int,
    val warmup: Int,
) {
    private val minimumSafeLocations = 5
    private val safeLocations = LinkedList<TeleportWorldLocation>()

    fun hasLocations() = safeLocations.isNotEmpty()
    fun hasEnoughLocations() = safeLocations.size == minimumSafeLocations
    fun findSafeLocation(): TeleportWorldLocation? {
        // TODO: Find new safe location.
        return null
    }
    fun fillSafeLocations() {
        if (safeLocations.size == minimumSafeLocations) return

        // TODO: 7/17/22 Spread the safe location searching over a longer period of time and/or maybe in another thread.
        while (safeLocations.size < minimumSafeLocations) {
            val safeLocation = findSafeLocation() ?: continue
            safeLocations.add(safeLocation)
        }
    }
    fun getSafeLocation(): TeleportWorldLocation? {
        return if (safeLocations.isNotEmpty())
            safeLocations.pop()
        else {
            fillSafeLocations()
            findSafeLocation()
        }
    }
    fun getBukkitWorld() = Bukkit.getWorld(name) ?: throw InvalidWorldException("Could not find the world: $name")
}
