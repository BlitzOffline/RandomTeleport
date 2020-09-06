package me.blitzgamer_88.randomteleport.util

import org.bukkit.Location
import org.bukkit.World

fun getRandomLocation(world: World, useBorder: Boolean, maxX: Int?, maxZ: Int?) : Location {


    var maxValue: Int? = null

    if (useBorder) {
        maxValue = world.worldBorder.size.toInt() / 2
    }

    val randomX: Int?
    val randomZ: Int?

    if (maxValue == null) {
        randomX = (-maxX!!..maxX).random()
        randomZ = (-maxZ!!..maxZ).random()
    }else {
        randomX = (-maxValue..maxValue).random()
        randomZ = (-maxValue..maxValue).random()
    }

    val randomY = world.getHighestBlockYAt(randomX, randomZ)+1

    return Location(world, randomX.toDouble(), randomY.toDouble(), randomZ.toDouble())
}