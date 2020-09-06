package me.blitzgamer_88.randomteleport.cmd

import io.papermc.lib.PaperLib
import me.blitzgamer_88.randomteleport.RandomTeleport
import me.blitzgamer_88.randomteleport.conf.Config
import me.blitzgamer_88.randomteleport.util.*
import me.clip.placeholderapi.PlaceholderAPI
import me.mattstudios.mf.annotations.*
import me.mattstudios.mf.base.CommandBase
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


@Command("randomteleport")
@Alias("rtp", "wild")
class CommandRandomTeleport(private val mainClass: RandomTeleport) : CommandBase() {

    @Default
    fun randomTeleport(sender: CommandSender, @Completion("#players") @Optional target: Player?){


        val targetNotSpecified = mainClass.conf().getProperty(Config.targetNotSpecified).color()
        val noPermission = mainClass.conf().getProperty(Config.noPermission).color()

        val enabledWorlds = mainClass.conf().getProperty(Config.enabledWorlds)

        val useBorder = mainClass.conf().getProperty(Config.useBorder)
        val useWorldGuard = mainClass.conf().getProperty(Config.useWorldGuard)
//    val debug = mainClass.conf().getProperty(Config.debug)

        val maxX = mainClass.conf().getProperty(Config.maxX)
        val maxZ = mainClass.conf().getProperty(Config.maxZ)

        val maxAttempts = mainClass.conf().getProperty(Config.maxAttempts)

        val rtpPermissionSelf = mainClass.conf().getProperty(Config.rtpPermissionSelf).color()
        val rtpPermissionOther = mainClass.conf().getProperty(Config.rtpPermissionOthers).color()
        val noLocationFound = mainClass.conf().getProperty(Config.noLocationFound).color()
        val noWorldFound = mainClass.conf().getProperty(Config.noWorldFound).color()

        val successfullyTeleported = mainClass.conf().getProperty(Config.successfullyTeleported).color()
        val successfullyTeleportedOther = mainClass.conf().getProperty(Config.successfullyTeleportedOther).color()




        if (target == null) {

            if (sender !is Player) {
                sender.sendMessage(targetNotSpecified)
                return
            }

            if (!sender.hasPermission(rtpPermissionSelf)) {
                sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, noPermission))
                return
            }

            if (!enabledWorlds.contains("all") && enabledWorlds != null) {

                var teleportWorldName = enabledWorlds.shuffled().take(1)[0]
                var teleportWorld = Bukkit.getWorld(teleportWorldName)

                if (teleportWorld == null) {
                    enabledWorlds.removeAt(0)
                    debug("$teleportWorldName has been selected but the world doesn't exist.")
                    while (teleportWorld == null && enabledWorlds.size > 0) {
                        mainClass.logger.info("&cError! $teleportWorldName has not been found!".color())
                        teleportWorldName = enabledWorlds.shuffled().take(1)[0]
                        teleportWorld = Bukkit.getWorld(teleportWorldName)
                        if (teleportWorld == null) {
                            debug("$teleportWorldName has been selected but the world doesn't exist.")
                            enabledWorlds.remove(teleportWorldName)
                        }else {
                            debug("$teleportWorldName has been selected and the world exists.")
                        }
                    }
                }else {
                    debug("$teleportWorldName has been selected and the world exists.")
                }

                if (teleportWorld == null) {
                    sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, noWorldFound))
                    return
                }

                lateinit var randomLocation: Location

                var ok = false
                var attempts = 1
                while (!ok && attempts <= maxAttempts) {
                    randomLocation = getRandomLocation(teleportWorld, useBorder, maxX, maxZ)
                    if (checkLocationSafety(randomLocation, useWorldGuard)){
                        ok = true
                        debug("Found a safe location in $attempts attempts.")
                    }else {
                        debug("Couldn't find a safe location in attempt $attempts. Coords: ${randomLocation.blockX} ${randomLocation.blockY} ${randomLocation.blockZ}")
                    }
                    attempts++
                }

                if (!ok){
                    sender.sendMessage(PlaceholderAPI.setPlaceholders(sender,noLocationFound))
                    return
                }

                PaperLib.teleportAsync(sender, randomLocation)
                sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, successfullyTeleported))
                return

            }

            val worlds = Bukkit.getWorlds()

            val teleportWorld = worlds.shuffled().take(1)[0]

            lateinit var randomLocation: Location

            var ok = false
            var attempts = 1
            while (!ok && attempts <= maxAttempts) {
                randomLocation = getRandomLocation(teleportWorld, useBorder, maxX, maxZ)
                if (checkLocationSafety(randomLocation, useWorldGuard)){
                    ok = true
                    debug("Found a safe location in $attempts attempts.")
                }else {
                    debug("Couldn't find a safe location in attempt $attempts. Coords: ${randomLocation.blockX} ${randomLocation.blockY} ${randomLocation.blockZ}")
                }
                attempts++
            }

            if (!ok){
                sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, noLocationFound))
                return
            }


            PaperLib.teleportAsync(sender, randomLocation)
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, successfullyTeleported))
            return

        }

        // IF THERE IS A TARGET

        if (sender is Player && !sender.hasPermission(rtpPermissionOther)) {
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, noPermission))
            return
        }

        if (!enabledWorlds.contains("all") && enabledWorlds != null) {

            var teleportWorldName = enabledWorlds.shuffled().take(1)[0]
            var teleportWorld = Bukkit.getWorld(teleportWorldName)

            if (teleportWorld == null) {
                enabledWorlds.removeAt(0)
                debug("$teleportWorldName has been selected but the world doesn't exist.")
                while (teleportWorld == null && enabledWorlds.size > 0) {
                    mainClass.logger.info("&cError! $teleportWorldName has not been found!".color())
                    teleportWorldName = enabledWorlds.shuffled().take(1)[0]
                    teleportWorld = Bukkit.getWorld(teleportWorldName)
                    if (teleportWorld == null) {
                        debug("$teleportWorldName has been selected but the world doesn't exist.")
                        enabledWorlds.remove(teleportWorldName)
                    }else {
                        debug("$teleportWorldName has been selected and the world exists.")
                    }
                }
            }else {
                debug("$teleportWorldName has been selected and the world exists.")
            }

            if (teleportWorld == null) {
                sender.sendMessage(noWorldFound)
                return
            }

            lateinit var randomLocation: Location

            var ok = false
            var attempts = 1
            while (!ok && attempts <= maxAttempts) {
                randomLocation = getRandomLocation(teleportWorld, useBorder, maxX, maxZ)
                if (checkLocationSafety(randomLocation, useWorldGuard)){
                    ok = true
                    debug("Found a safe location in $attempts attempts.")
                }else {
                    debug("Couldn't find a safe location in attempt $attempts. Coords: ${randomLocation.blockX} ${randomLocation.blockY} ${randomLocation.blockZ}")
                }
                attempts++
            }

            if (!ok){
                sender.sendMessage(noLocationFound)
                return
            }

            PaperLib.teleportAsync(target, randomLocation)
            target.sendMessage(PlaceholderAPI.setPlaceholders(target, successfullyTeleported))
            sender.sendMessage(PlaceholderAPI.setPlaceholders(target, successfullyTeleportedOther))
            return

        }

        val worlds = Bukkit.getWorlds()

        val teleportWorld = worlds.shuffled().take(1)[0]

        lateinit var randomLocation: Location

        var ok = false
        var attempts = 1
        while (!ok && attempts <= maxAttempts) {
            randomLocation = getRandomLocation(teleportWorld, useBorder, maxX, maxZ)
            if (checkLocationSafety(randomLocation, useWorldGuard)){
                ok = true
                debug("Found a safe location in $attempts attempts.")
            }else {
                debug("Couldn't find a safe location in attempt $attempts. Coords: ${randomLocation.blockX} ${randomLocation.blockY} ${randomLocation.blockZ}")
            }
            attempts++
        }

        if (!ok){
            sender.sendMessage(PlaceholderAPI.setPlaceholders(target, noLocationFound))
            return
        }

        PaperLib.teleportAsync(target, randomLocation)
        target.sendMessage(PlaceholderAPI.setPlaceholders(target, successfullyTeleported))
        sender.sendMessage(PlaceholderAPI.setPlaceholders(target, successfullyTeleportedOther))
        return
    }




    @SubCommand("world")
    fun randomTeleportWorld(sender: CommandSender, @Completion("#worlds") worldName: String, @Completion("#players") @Optional target: Player?){


        val targetNotSpecified = mainClass.conf().getProperty(Config.targetNotSpecified).color()
        val noPermission = mainClass.conf().getProperty(Config.noPermission).color()

        val useBorder = mainClass.conf().getProperty(Config.useBorder)
        val useWorldGuard = mainClass.conf().getProperty(Config.useWorldGuard)
//    val debug = mainClass.conf().getProperty(Config.debug)

        val maxX = mainClass.conf().getProperty(Config.maxX)
        val maxZ = mainClass.conf().getProperty(Config.maxZ)

        val maxAttempts = mainClass.conf().getProperty(Config.maxAttempts)

        val rtpWorldPermissionSelf = mainClass.conf().getProperty(Config.rtpWorldPermissionSelf).color()
        val rtpWorldPermissionOther = mainClass.conf().getProperty(Config.rtpWorldPermissionOthers).color()

        val wrongWorldName = mainClass.conf().getProperty(Config.wrongWorldName).color()
        val noLocationFound = mainClass.conf().getProperty(Config.noLocationFound).color()

        val successfullyTeleported = mainClass.conf().getProperty(Config.successfullyTeleported).color()
        val successfullyTeleportedOther = mainClass.conf().getProperty(Config.successfullyTeleportedOther).color()


        val teleportWorld = Bukkit.getWorld(worldName)

        if (teleportWorld == null) {
            sender.sendMessage(wrongWorldName)
            return
        }

        if (target == null) {

            if (sender !is Player) {
                sender.sendMessage(targetNotSpecified)
                return
            }

            if (!sender.hasPermission(rtpWorldPermissionSelf)) {
                sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, noPermission))
                return
            }

            lateinit var randomLocation: Location

            var ok = false
            var attempts = 1
            while (!ok && attempts <= maxAttempts) {
                randomLocation = getRandomLocation(teleportWorld, useBorder, maxX, maxZ)
                if (checkLocationSafety(randomLocation, useWorldGuard)){
                    ok = true
                    debug("Found a safe location in $attempts attempts.")
                }else {
                    debug("Couldn't find a safe location in attempt $attempts. Coords: ${randomLocation.blockX} ${randomLocation.blockY} ${randomLocation.blockZ}")
                }
                attempts++
            }

            if (!ok){
                sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, noLocationFound))
                return
            }

            PaperLib.teleportAsync(sender, randomLocation)
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, successfullyTeleported))
            return

        }

        // IF THERE IS A TARGET

        if (sender is Player && !sender.hasPermission(rtpWorldPermissionOther)) {
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, noPermission))
            return
        }

        lateinit var randomLocation: Location

        var ok = false
        var attempts = 1
        while (!ok && attempts <= maxAttempts) {
            randomLocation = getRandomLocation(teleportWorld, useBorder, maxX, maxZ)
            if (checkLocationSafety(randomLocation, useWorldGuard)){
                ok = true
                debug("Found a safe location in $attempts attempts.")
            }else {
                debug("Couldn't find a safe location in attempt $attempts. Coords: ${randomLocation.blockX} ${randomLocation.blockY} ${randomLocation.blockZ}")
            }
            attempts++
        }

        if (!ok){
            sender.sendMessage(PlaceholderAPI.setPlaceholders(target, noLocationFound))
            return
        }

        PaperLib.teleportAsync(target, randomLocation)
        target.sendMessage(PlaceholderAPI.setPlaceholders(target, successfullyTeleported))
        sender.sendMessage(PlaceholderAPI.setPlaceholders(target, successfullyTeleportedOther))
        return
    }


    @SubCommand("reload")
    fun reload(sender: CommandSender) {

        val noPermission = mainClass.conf().getProperty(Config.noPermission).color()
        val configReload = mainClass.conf().getProperty(Config.configReload).color()

        if (sender is Player && !sender.hasPermission("randomteleport.reload")){
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, noPermission))
            return
        }
        mainClass.conf().reload()
        sender.sendMessage(configReload.color())
    }


    @SubCommand("info")
    fun randomTeleportInfo(sender: CommandSender){
        sender.sendMessage("&eRandomTeleport &8by &6BlitzGamer_88".color())
    }

}