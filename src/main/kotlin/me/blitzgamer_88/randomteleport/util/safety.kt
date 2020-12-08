package me.blitzgamer_88.randomteleport.util

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material


fun Location.inWorldGuardRegion() : Boolean {

    val weLocation: com.sk89q.worldedit.util.Location = BukkitAdapter.adapt(this)
    val container = WorldGuard.getInstance().platform.regionContainer
    val query = container.createQuery()
    val regionSet = query.getApplicableRegions(weLocation)

    return regionSet.size() > 0
}


fun Location.checkLocationSafety(): Boolean {
    val head = this.clone().add(0.0, 1.0, 0.0)
    val ground = this.clone().subtract(0.0, 1.0, 0.0)

    if (useWorldGuard && Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
        if (ground.inWorldGuardRegion() || this.inWorldGuardRegion() || head.inWorldGuardRegion()) return false
    }

    return groundIsSafe(ground) && footIsSafe(this) && headIsSafe(head)
}


private val FLOWING_TYPES: List<Material> = listOf(Material.WATER, Material.LAVA)
private val DAMAGING_TYPES: List<Material> = listOf(
    Material.CACTUS,
    Material.CAMPFIRE,
    Material.SOUL_CAMPFIRE,
    Material.FIRE,
    Material.MAGMA_BLOCK,
    Material.SOUL_FIRE,
    Material.SWEET_BERRY_BUSH,
    Material.WITHER_ROSE
)
private val PORTAL: List<Material> = listOf(Material.NETHER_PORTAL, Material.END_PORTAL)

fun groundIsSafe(ground: Location) : Boolean {
    val block = ground.block
    val material = block.type

    if (block.isEmpty || block.isLiquid || !material.isSolid || material.isAir || material.isTransparent || FLOWING_TYPES.contains(material) ||
        DAMAGING_TYPES.contains(material) || PORTAL.contains(material)) return false
    return true
}


fun footIsSafe(foot: Location) : Boolean {
    val block = foot.block
    val material = block.type

    if (block.isEmpty || !material.isSolid || material.isAir || material.isTransparent) return true
    return false
}


fun headIsSafe(head: Location) : Boolean {
    val block = head.block
    val material = block.type

    if (block.isEmpty || !material.isSolid || material.isAir || material.isTransparent) return true
    return false
}