package com.blitzoffline.randomteleport.util

import com.blitzoffline.randomteleport.RandomTeleport
import com.blitzoffline.randomteleport.config.holder.Settings
import com.blitzoffline.randomteleport.config.settings
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import me.angeschossen.lands.api.integration.LandsIntegration
import org.bukkit.Location
import org.bukkit.Material


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

private lateinit var landsIntegration: LandsIntegration
fun registerLandsIntegration(plugin: RandomTeleport) { landsIntegration = LandsIntegration(plugin) }

fun Location.isSafe(): Boolean {
    val head = this.clone().add(0.0, 1.0, 0.0)
    val ground = this.clone().subtract(0.0, 1.0, 0.0)
    if (settings[Settings.HOOK_WG]) if(ground.isInWorldGuardRegion() || this.isInWorldGuardRegion() || head.isInWorldGuardRegion()) return false
    if (settings[Settings.HOOK_LANDS]) if(ground.isInLand() || this.isInLand() || head.isInLand()) return false
    return ground.groundIsSafe() && this.bodyIsSafe() && head.bodyIsSafe()
}

fun Location.isInWorldGuardRegion() : Boolean {
    val weLocation = BukkitAdapter.adapt(this)
    val container = WorldGuard.getInstance().platform.regionContainer

    return container.createQuery().getApplicableRegions(weLocation).size() > 0
}

fun Location.isInLand() = landsIntegration.isClaimed(this)

fun Location.groundIsSafe(): Boolean {
    val block = this.block
    val material = block.type
    return !block.isEmpty && !block.isLiquid && !material.isAir && !unsafeBlocks.contains(material)
}

fun Location.bodyIsSafe(): Boolean {
    val block = this.block
    val material = block.type
    return block.isEmpty || material.isAir
}