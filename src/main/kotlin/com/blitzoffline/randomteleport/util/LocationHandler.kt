package com.blitzoffline.randomteleport.util

import com.blitzoffline.randomteleport.RandomTeleport
import com.griefdefender.api.Core
import com.griefdefender.api.GriefDefender
import com.palmergames.bukkit.towny.TownyAPI
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import me.angeschossen.lands.api.integration.LandsIntegration
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import com.sk89q.worldedit.util.Location as WGLocation

class LocationHandler(private val plugin: RandomTeleport) {
    fun isSafe(location: Location): Boolean {
        val head = location.clone().add(0.0, 1.0, 0.0)
        val ground = location.clone().subtract(0.0, 1.0, 0.0)
        if (plugin.hooks["Lands"] == true) if (ground.isInLand() || location.isInLand() || head.isInLand()) return false
        if (plugin.hooks["GriefDefender"] == true) if (ground.isInGDClaim() || location.isInGDClaim() || head.isInGDClaim()) return false
        if (plugin.hooks["WorldGuard"] == true) if (ground.isInWorldGuardRegion() || location.isInWorldGuardRegion() || head.isInWorldGuardRegion()) return false
        if (plugin.hooks["Towny"] == true) if (ground.isInTownyTown() || location.isInTownyTown() || head.isInTownyTown()) return false
        return ground.groundIsSafe() && location.bodyIsSafe() && head.bodyIsSafe()
    }

    fun getRandomLocation(world: World, useBorder: Boolean, maxX: Int, maxZ: Int, maxAttempts: Int) : Location? {
        lateinit var randomLocation: Location
        var ok = false
        var attempts = 0
        while (!ok && attempts < maxAttempts) {
            randomLocation = plugin.locationHandler.getRandomLocation(world, useBorder, maxX, maxZ)
            ok = plugin.locationHandler.isSafe(randomLocation)
            attempts++
        }

        return if (ok) randomLocation else null
    }

    fun getRandomLocation(world: World, useBorder: Boolean, maxX: Int, maxZ: Int) : Location {
        val randomX: Int
        val randomZ: Int

        if (useBorder) {
            val maxValue = world.worldBorder.size.toInt() / 2
            randomX = (-maxValue..maxValue).random()
            randomZ = (-maxValue..maxValue).random()

            val randomY = world.getHighestBlockYAt(randomX, randomZ)+1
            return Location(world, randomX.toDouble(), randomY.toDouble(), randomZ.toDouble())
        }

        randomX = (-maxX..maxX).random()
        randomZ = (-maxZ..maxZ).random()
        val randomY = world.getHighestBlockYAt(randomX, randomZ)+1
        return Location(world, randomX.toDouble(), randomY.toDouble(), randomZ.toDouble())
    }

    private fun Location.isInLand() = landsIntegration.isClaimed(this)

    private fun Location.isInGDClaim() = griefDefenderIntegration.getClaimAt(this) != null

    private fun Location.isInWorldGuardRegion() = worldGuardIntegration.platform.regionContainer.createQuery().getApplicableRegions(this.adapt()).size() > 0

    private fun Location.isInTownyTown() = !townyIntegration.isWilderness(this)

    private fun Location.groundIsSafe(): Boolean {
        val block = this.block
        val material = block.type
        return !block.isEmpty && !block.isLiquid && !material.isAir && !unsafeBlocks.contains(material)
    }

    private fun Location.adapt(): WGLocation {
        return BukkitAdapter.adapt(this)
    }

    private fun Location.bodyIsSafe(): Boolean {
        val block = this.block
        val material = block.type
        return block.isEmpty || material.isAir
    }

    fun startLandsIntegration() {
        landsIntegration = LandsIntegration(plugin)
    }

    fun startGriefDefenderIntegration() {
        griefDefenderIntegration = GriefDefender.getCore()
    }

    fun startWorldGuardIntegration() {
        worldGuardIntegration = WorldGuard.getInstance()
    }

    fun startTownyIntegration() {
        townyIntegration = TownyAPI.getInstance()
    }

    private lateinit var landsIntegration: LandsIntegration
    private lateinit var griefDefenderIntegration: Core
    private lateinit var worldGuardIntegration: WorldGuard
    private lateinit var townyIntegration: TownyAPI
    private val unsafeBlocks = listOf(
        Material.WATER,
        Material.LAVA,
        Material.CACTUS,
        Material.CAMPFIRE,
        Material.SOUL_CAMPFIRE,
        Material.FIRE,
        Material.MAGMA_BLOCK,
        Material.SOUL_FIRE,
        Material.SWEET_BERRY_BUSH,
        Material.WITHER_ROSE,
        Material.NETHER_PORTAL,
        Material.END_PORTAL
    )
}