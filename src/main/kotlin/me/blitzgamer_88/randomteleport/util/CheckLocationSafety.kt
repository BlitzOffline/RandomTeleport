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

    if (regionSet.size() > 0) return true
    return false
}


fun checkLocationSafety(foot: Location, useWorldGuard: Boolean) : Boolean {

    val head = foot.clone().add(0.0, 1.0, 0.0)
    val ground = foot.clone().subtract(0.0, 1.0, 0.0)

    if (useWorldGuard && Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
        if (ground.inWorldGuardRegion() || foot.inWorldGuardRegion() || head.inWorldGuardRegion()) {
            "Location inside a region.".debug()
            return false
        }
    }
    if (groundIsSafe(ground) && footIsSafe(foot) && headIsSafe(head)){
        return true
    }
    return false
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

    if (block.isEmpty){
        "Ground is empty.".debug()
        return false
    }
    if (block.isLiquid){
        "Ground is liquid.".debug()
        return false
    }
    if (!material.isSolid){
        "Ground is not solid.".debug()
        return false
    }
    if (material.isAir){
        "Ground is air.".debug()
        return false
    }
    if (material.isTransparent){
        "Ground is transparent.".debug()
        return false
    }
    if (FLOWING_TYPES.contains(material)){
        "Ground is a flowing type.".debug()
        return false
    }
    if (DAMAGING_TYPES.contains(material)){
        "Ground is a damaging type.".debug()
        return false
    }
    if (PORTAL.contains(material)){
        "Ground is portal.".debug()
        return false
    }
    return true
}


fun footIsSafe(foot: Location) : Boolean {
    val block = foot.block
    val material = block.type

    if (block.isEmpty){
        "Foot is Empty.".debug()
        return true
    }
    if (!material.isSolid){
        "Foot is not Solid.".debug()
        return true
    }
    if (material.isAir){
        "Foot is Air.".debug()
        return true
    }
    if (material.isTransparent){
        "Foot is Transparent.".debug()
        return true
    }
    return false
}


fun headIsSafe(head: Location) : Boolean {
    val block = head.block
    val material = block.type

    if (block.isEmpty){
        "Head is Empty.".debug()
        return true
    }
    if (!material.isSolid){
        "Head is not Solid.".debug()
        return true
    }
    if (material.isAir){
        "Head is Air.".debug()
        return true
    }
    if (material.isTransparent){
        "Head is Transparent.".debug()
        return true
    }
    return false
}