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


@Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
@Command("randomteleport")
@Alias("rtp", "wild")
class CommandRandomTeleport(private val mainClass: RandomTeleport) : CommandBase() {

    @Default
    fun randomTeleport(sender: CommandSender, @Completion("#players") @Optional target: Player?){

        val enabledWorlds = mainClass.conf().getProperty(Config.enabledWorlds)

        val useBorder = mainClass.conf().getProperty(Config.useBorder)
        val useWorldGuard = mainClass.conf().getProperty(Config.useWorldGuard)
//    val debug = mainClass.conf().getProperty(Config.debug)

        val coolDown = mainClass.conf().getProperty(Config.coolDown)
        val useCoolDownWhenTeleportedByOthers = mainClass.conf().getProperty(Config.useCoolDownWhenTeleportedByOthers)

        val maxX = mainClass.conf().getProperty(Config.maxX)
        val maxZ = mainClass.conf().getProperty(Config.maxZ)

        val maxAttempts = mainClass.conf().getProperty(Config.maxAttempts)

        val rtpPermissionSelf = mainClass.conf().getProperty(Config.rtpPermissionSelf)
        val rtpPermissionOther = mainClass.conf().getProperty(Config.rtpPermissionOthers)
        val rtpCoolDownBypassPermission = mainClass.conf().getProperty(Config.rtpCoolDownBypassPermission)

        val successfullyTeleported = mainClass.conf().getProperty(Config.successfullyTeleported).color()
        val successfullyTeleportedOther = mainClass.conf().getProperty(Config.successfullyTeleportedOther).color()
        val noLocationFound = mainClass.conf().getProperty(Config.noLocationFound).color()
        val noWorldFound = mainClass.conf().getProperty(Config.noWorldFound).color()
        val coolDownRemaining = mainClass.conf().getProperty(Config.coolDownRemaining).color()
        val coolDownRemainingTarget = mainClass.conf().getProperty(Config.coolDownRemainingTarget).color()
        val targetNotSpecified = mainClass.conf().getProperty(Config.targetNotSpecified).color()
        val noPermission = mainClass.conf().getProperty(Config.noPermission).color()




        if (target == null) {

            if (sender !is Player) {
                sender.sendMessage(targetNotSpecified)
                return
            }

            if (!sender.hasPermission(rtpPermissionSelf)) {
                sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, noPermission))
                return
            }

            if (coolDown != 0 && !sender.hasPermission(rtpCoolDownBypassPermission)) {
                val time = System.currentTimeMillis()
                val newCoolDown = coolDown*1000.toLong()
                val lastCoolDown = mainClass.getCoolDownsConfig()?.get("${sender.uniqueId}").toString().toLongOrNull()
                if (lastCoolDown != null && time-newCoolDown < lastCoolDown) {
                    val coolDownLeft = coolDown - ((time-lastCoolDown)/1000)
                    val newStringCoolDownRemaining = coolDownRemaining.replace("%cooldown%", coolDownLeft.toString())
                    sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, newStringCoolDownRemaining))
                    return
                }
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
                if (coolDown != 0) {
                    mainClass.getCoolDownsConfig()?.set("${sender.uniqueId}", System.currentTimeMillis())
                }
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
            if (coolDown != 0) {
                mainClass.getCoolDownsConfig()?.set("${sender.uniqueId}", System.currentTimeMillis())
            }
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, successfullyTeleported))
            return

        }

        // IF THERE IS A TARGET

        if (sender is Player && !sender.hasPermission(rtpPermissionOther)) {
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, noPermission))
            return
        }

        if (coolDown != 0 && useCoolDownWhenTeleportedByOthers && !target.hasPermission(rtpCoolDownBypassPermission)) {
            val time = System.currentTimeMillis()
            val newCoolDown = coolDown*1000.toLong()
            val lastCoolDown = mainClass.getCoolDownsConfig()?.get("${target.uniqueId}").toString().toLongOrNull()
            if (lastCoolDown != null && time-newCoolDown < lastCoolDown) {
                val coolDownLeft = coolDown - ((time-lastCoolDown)/1000)
                val newStringCoolDownRemainingTarget = coolDownRemainingTarget.replace("%cooldown%", coolDownLeft.toString())
                sender.sendMessage(PlaceholderAPI.setPlaceholders(target, newStringCoolDownRemainingTarget))
                return
            }
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
            if (coolDown != 0 && useCoolDownWhenTeleportedByOthers) {
                mainClass.getCoolDownsConfig()?.set("${target.uniqueId}", System.currentTimeMillis())
            }
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
        if (coolDown != 0 && useCoolDownWhenTeleportedByOthers) {
            mainClass.getCoolDownsConfig()?.set("${target.uniqueId}", System.currentTimeMillis())
        }
        target.sendMessage(PlaceholderAPI.setPlaceholders(target, successfullyTeleported))
        sender.sendMessage(PlaceholderAPI.setPlaceholders(target, successfullyTeleportedOther))
        return
    }




    @SubCommand("world")
    fun randomTeleportWorld(sender: CommandSender, @Completion("#worlds") worldName: String, @Completion("#players") @Optional target: Player?){


        val useBorder = mainClass.conf().getProperty(Config.useBorder)
        val useWorldGuard = mainClass.conf().getProperty(Config.useWorldGuard)
//    val debug = mainClass.conf().getProperty(Config.debug)

        val coolDown = mainClass.conf().getProperty(Config.coolDown)
        val useCoolDownWhenTeleportedByOthers = mainClass.conf().getProperty(Config.useCoolDownWhenTeleportedByOthers)

        val maxX = mainClass.conf().getProperty(Config.maxX)
        val maxZ = mainClass.conf().getProperty(Config.maxZ)

        val maxAttempts = mainClass.conf().getProperty(Config.maxAttempts)

        val rtpWorldPermissionSelf = mainClass.conf().getProperty(Config.rtpWorldPermissionSelf)
        val rtpWorldPermissionOther = mainClass.conf().getProperty(Config.rtpWorldPermissionOthers)
        val rtpCoolDownBypassPermission = mainClass.conf().getProperty(Config.rtpCoolDownBypassPermission)

        val wrongWorldName = mainClass.conf().getProperty(Config.wrongWorldName).color()
        val noLocationFound = mainClass.conf().getProperty(Config.noLocationFound).color()
        val successfullyTeleported = mainClass.conf().getProperty(Config.successfullyTeleported).color()
        val successfullyTeleportedOther = mainClass.conf().getProperty(Config.successfullyTeleportedOther).color()
        val targetNotSpecified = mainClass.conf().getProperty(Config.targetNotSpecified).color()
        val noPermission = mainClass.conf().getProperty(Config.noPermission).color()
        val coolDownRemaining = mainClass.conf().getProperty(Config.coolDownRemaining).color()
        val coolDownRemainingTarget = mainClass.conf().getProperty(Config.coolDownRemainingTarget).color()


        val teleportWorld = Bukkit.getWorld(worldName)
        val perWorldPermission = "rtp.world.$worldName"

        if (teleportWorld == null) {
            sender.sendMessage(wrongWorldName)
            return
        }

        if (target == null) {

            if (sender !is Player) {
                sender.sendMessage(targetNotSpecified)
                return
            }

            if (!sender.hasPermission(rtpWorldPermissionSelf) && !sender.hasPermission(perWorldPermission)) {
                sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, noPermission))
                return
            }

            if (coolDown != 0 && !sender.hasPermission(rtpCoolDownBypassPermission)) {
                val time = System.currentTimeMillis()
                val newCoolDown = coolDown*1000.toLong()
                val lastCoolDown = mainClass.getCoolDownsConfig()?.get("${sender.uniqueId}").toString().toLongOrNull()
                if (lastCoolDown != null && time-newCoolDown < lastCoolDown) {
                    val coolDownLeft = coolDown - ((time-lastCoolDown)/1000)
                    val newStringCoolDownRemaining = coolDownRemaining.replace("%cooldown%", coolDownLeft.toString())
                    sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, newStringCoolDownRemaining))
                    return
                }
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
            if (coolDown != 0) {
                mainClass.getCoolDownsConfig()?.set("${sender.uniqueId}", System.currentTimeMillis())
            }
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, successfullyTeleported))
            return

        }

        // IF THERE IS A TARGET

        if (sender is Player && !sender.hasPermission(rtpWorldPermissionOther)) {
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, noPermission))
            return
        }

        if (coolDown != 0 && useCoolDownWhenTeleportedByOthers && !target.hasPermission(rtpCoolDownBypassPermission)) {
            val time = System.currentTimeMillis()
            val newCoolDown = coolDown*1000.toLong()
            val lastCoolDown = mainClass.getCoolDownsConfig()?.get("${target.uniqueId}").toString().toLongOrNull()
            if (lastCoolDown != null && time-newCoolDown < lastCoolDown) {
                val coolDownLeft = coolDown - ((time-lastCoolDown)/1000)
                val newStringCoolDownRemainingTarget = coolDownRemainingTarget.replace("%cooldown%", coolDownLeft.toString())
                sender.sendMessage(PlaceholderAPI.setPlaceholders(target, newStringCoolDownRemainingTarget))
                return
            }
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
        if (coolDown != 0 && useCoolDownWhenTeleportedByOthers) {
            mainClass.getCoolDownsConfig()?.set("${target.uniqueId}", System.currentTimeMillis())
        }
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
        mainClass.reloadCoolDownsConfig()
        sender.sendMessage(configReload.color())
    }


    @SubCommand("info")
    fun randomTeleportInfo(sender: CommandSender){
        sender.sendMessage("&eRandomTeleport &8by &6BlitzGamer_88".color())
    }

}