package me.blitzgamer_88.randomteleport.util

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import org.bukkit.Location
import org.bukkit.Material
import me.angeschossen.lands.api.integration.LandsIntegration
import me.blitzgamer_88.randomteleport.RandomTeleport


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

fun registerLandsIntegration(plugin: RandomTeleport) {
    landsIntegration = LandsIntegration(plugin)
}

fun Location.isSafe(): Boolean {
    val head = this.clone().add(0.0, 1.0, 0.0)
    val ground = this.clone().subtract(0.0, 1.0, 0.0)
    if (useWorldGuard) if(ground.inWorldGuardRegion() || this.inWorldGuardRegion() || head.inWorldGuardRegion()) return false
    if (useLands) if(ground.inLands() || this.inLands() || head.inLands()) return false
    return groundIsSafe(ground) && footIsSafe(this) && headIsSafe(head)
}

fun Location.inWorldGuardRegion() : Boolean {
    val weLocation: com.sk89q.worldedit.util.Location = BukkitAdapter.adapt(this)
    val container = WorldGuard.getInstance().platform.regionContainer
    val query = container.createQuery()
    val regionSet = query.getApplicableRegions(weLocation)

    return regionSet.size() > 0
}

fun Location.inLands() : Boolean {
    return landsIntegration.isClaimed(this)
}


fun groundIsSafe(ground: Location) : Boolean {
    val block = ground.block
    val material = block.type
    return !block.isEmpty && !block.isLiquid && !material.isAir && !unsafeBlocks.contains(material)
}

fun footIsSafe(foot: Location) : Boolean {
    val block = foot.block
    val material = block.type
    return block.isEmpty || material.isAir
}

fun headIsSafe(head: Location) : Boolean {
    val block = head.block
    val material = block.type
    return block.isEmpty|| material.isAir
}