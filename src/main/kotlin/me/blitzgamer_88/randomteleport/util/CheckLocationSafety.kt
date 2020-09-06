package me.blitzgamer_88.randomteleport.util

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import me.blitzgamer_88.randomteleport.RandomTeleport
import org.bukkit.Location
import org.bukkit.Material



fun locationInWGRegion(loc: Location) : Boolean {

    val weLocation: com.sk89q.worldedit.util.Location = BukkitAdapter.adapt(loc)
    val container = WorldGuard.getInstance().platform.regionContainer
    val query = container.createQuery()
    val regionSet = query.getApplicableRegions(weLocation)

    if (regionSet.size() > 0){
        return true
    }
    return false

}


fun checkLocationSafety(foot: Location, useWorldGuard: Boolean) : Boolean {

    val head = foot.clone().add(0.0, 1.0, 0.0)
    val ground = foot.clone().subtract(0.0, 1.0, 0.0)


    if (useWorldGuard) {
        if (locationInWGRegion(ground) || locationInWGRegion(foot) || locationInWGRegion(head)) {
            debug("Location inside a region.")
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
        debug("Ground is empty.")
        return false
    }
    if (block.isLiquid){
        debug("Ground is liquid.")
        return false
    }
    if (!material.isSolid){
        debug("Ground is not solid.")
        return false
    }
    if (material.isAir){
        debug("Ground is air.")
        return false
    }
    if (material.isTransparent){
        debug("Ground is transparent.")
        return false
    }
    if (FLOWING_TYPES.contains(material)){
        debug("Ground is a flowing type.")
        return false
    }
    if (DAMAGING_TYPES.contains(material)){
        debug("Ground is a damaging type.")
        return false
    }
    if (PORTAL.contains(material)){
        debug("Ground is portal.")
        return false
    }

    return true
}


fun footIsSafe(foot: Location) : Boolean {
    val block = foot.block
    val material = block.type

    if (block.isEmpty){
        debug("Foot is Empty.")
        return true
    }
    if (!material.isSolid){
        debug("Foot is not Solid.")
        return true
    }
    if (material.isAir){
        debug("Foot is Air.")
        return true
    }
    if (material.isTransparent){
        debug("Foot is Transparent.")
        return true
    }
    return false
}


fun headIsSafe(head: Location) : Boolean {
    val block = head.block
    val material = block.type

    if (block.isEmpty){
        debug("Head is Empty.")
        return true
    }
    if (!material.isSolid){
        debug("Head is not Solid.")
        return true
    }
    if (material.isAir){
        debug("Head is Air.")
        return true
    }
    if (material.isTransparent){
        debug("Head is Transparent.")
        return true
    }
    return false
}