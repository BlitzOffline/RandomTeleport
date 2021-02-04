package me.blitzgamer_88.randomteleport.util

import org.bukkit.Location
import org.bukkit.World

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