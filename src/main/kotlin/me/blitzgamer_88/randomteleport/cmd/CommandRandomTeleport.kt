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
import org.bukkit.event.player.PlayerTeleportEvent


@Command("randomteleport")
@Alias("rtp", "wild")
class CommandRandomTeleport(private val mainClass: RandomTeleport) : CommandBase() {

    @Default
    fun randomTeleport(sender: CommandSender, @Completion("#players") @Optional target: Player?) {

        val enabledWorlds = conf().getProperty(Config.enabledWorlds)

        val useBorder = conf().getProperty(Config.useBorder)
        val useWorldGuard = conf().getProperty(Config.useWorldGuard)

        val coolDown = conf().getProperty(Config.coolDown)
        val useCoolDownWhenTeleportedByOthers = conf().getProperty(Config.useCoolDownWhenTeleportedByOthers)

        val maxX = conf().getProperty(Config.maxX)
        val maxZ = conf().getProperty(Config.maxZ)

        val maxAttempts = conf().getProperty(Config.maxAttempts)

        val rtpPermissionSelf = conf().getProperty(Config.rtpPermissionSelf)
        val rtpPermissionOther = conf().getProperty(Config.rtpPermissionOthers)
        val rtpCoolDownBypassPermission = conf().getProperty(Config.rtpCoolDownBypassPermission)

        val successfullyTeleported = conf().getProperty(Config.successfullyTeleported)
        val successfullyTeleportedOther = conf().getProperty(Config.successfullyTeleportedOther)
        val noLocationFound = conf().getProperty(Config.noLocationFound)
        val noWorldFound = conf().getProperty(Config.noWorldFound)
        val coolDownRemaining = conf().getProperty(Config.coolDownRemaining)
        val coolDownRemainingTarget = conf().getProperty(Config.coolDownRemainingTarget)
        val targetNotSpecified = conf().getProperty(Config.targetNotSpecified)
        val noPermission = conf().getProperty(Config.noPermission)

        if (target == null && sender !is Player) {
            sender.sendMessage(targetNotSpecified)
            return
        }

        val player = target ?: sender as Player

        if (target == null && !player.hasPermission(rtpPermissionSelf)) {
            noPermission.msg(player)
            return
        }

        if (target != null && sender is Player && !sender.hasPermission(rtpPermissionOther)) {
            noPermission.msg(sender)
            return
        }

        if ((coolDown != 0 && target == null && !player.hasPermission(rtpCoolDownBypassPermission)) || (coolDown != 0 && target != null && useCoolDownWhenTeleportedByOthers)) {
            val time = System.currentTimeMillis()
            val newCoolDown = coolDown * 1000.toLong()
            val lastCoolDown = mainClass.getCoolDownsConfig()?.get("${player.uniqueId}").toString().toLongOrNull()
            if (lastCoolDown != null && time - newCoolDown < lastCoolDown) {
                val coolDownLeft = coolDown - ((time - lastCoolDown) / 1000)
                if (target == null) coolDownRemainingTarget.replace("%cooldown%", coolDownLeft.toString()).msg(player)
                else coolDownRemaining.replace("%cooldown%", coolDownLeft.toString()).msg(sender)
                return
            }
        }

        if (!enabledWorlds.contains("all") && enabledWorlds.isNotEmpty()) {
            var teleportWorldName = enabledWorlds.shuffled().take(1)[0]
            var teleportWorld = Bukkit.getWorld(teleportWorldName)

            if (teleportWorld == null) {
                enabledWorlds.removeAt(0)
                "$teleportWorldName has been selected but the world doesn't exist.".debug()
                while (teleportWorld == null && enabledWorlds.isNotEmpty()) {
                    "&cError! $teleportWorldName has not been found".log()
                    teleportWorldName = enabledWorlds.shuffled().take(1)[0]
                    teleportWorld = Bukkit.getWorld(teleportWorldName)
                    if (teleportWorld == null) {
                        "$teleportWorldName has been selected but the world doesn't exist.".debug()
                        enabledWorlds.removeAt(0)
                    } else "$teleportWorldName has been selected and the world exists".debug()
                }
            } else "$teleportWorldName has been selected and the world exists.".debug()

            if (teleportWorld == null) {
                noWorldFound.msg(sender)
                return
            }

            lateinit var randomLocation: Location
            var ok = false
            var attempts = 1

            while (!ok && attempts < maxAttempts) {
                randomLocation = getRandomLocation(teleportWorld, useBorder, maxX, maxZ)
                if (checkLocationSafety(randomLocation, useWorldGuard)) {
                    ok = true
                    "Found a safe location in $attempts attempts.".debug()
                } else "Couldn't find a safe location in attempt $attempts. Coords: ${randomLocation.blockX} ${randomLocation.blockY} ${randomLocation.blockZ}".debug()
                attempts++
            }

            if (!ok) {
                noLocationFound.msg(sender)
                return
            }

            PaperLib.teleportAsync(player, randomLocation, PlayerTeleportEvent.TeleportCause.COMMAND)
            if ((target == null && coolDown != 0) || (coolDown != 0 && target != null && useCoolDownWhenTeleportedByOthers)) mainClass.getCoolDownsConfig()?.set("${player.uniqueId}", System.currentTimeMillis())
            if (target != null) sender.sendMessage(PlaceholderAPI.setPlaceholders(target, successfullyTeleportedOther))
            successfullyTeleported.msg(player)
            return

        }

        val worlds = Bukkit.getWorlds()
        val teleportWorld = worlds.shuffled().take(1)[0]

        lateinit var randomLocation: Location

        var ok = false
        var attempts = 1
        while (!ok && attempts <= maxAttempts) {
            randomLocation = getRandomLocation(teleportWorld, useBorder, maxX, maxZ)
            if (checkLocationSafety(randomLocation, useWorldGuard)) {
                ok = true
                "Found a safe location in $attempts attempts.".debug()
            } else "Couldn't find a safe location in attempt $attempts. Coords: ${randomLocation.blockX} ${randomLocation.blockY} ${randomLocation.blockZ}".debug()
            attempts++
        }

        if (!ok) {
            noLocationFound.msg(sender)
            return
        }

        PaperLib.teleportAsync(player, randomLocation, PlayerTeleportEvent.TeleportCause.COMMAND)
        if ((coolDown != 0 && target == null) || (coolDown != 0 && target != null && useCoolDownWhenTeleportedByOthers)) mainClass.getCoolDownsConfig()?.set("${player.uniqueId}", System.currentTimeMillis())
        if (target != null) sender.sendMessage(PlaceholderAPI.setPlaceholders(target, successfullyTeleportedOther))
        successfullyTeleported.msg(player)
    }



    @SubCommand("world")
    fun randomTeleportWorld(sender: CommandSender, @Completion("#worlds") worldName: String, @Completion("#players") @Optional target: Player?) {

        val useBorder = conf().getProperty(Config.useBorder)
        val useWorldGuard = conf().getProperty(Config.useWorldGuard)

        val coolDown = conf().getProperty(Config.coolDown)
        val useCoolDownWhenTeleportedByOthers = conf().getProperty(Config.useCoolDownWhenTeleportedByOthers)

        val maxX = conf().getProperty(Config.maxX)
        val maxZ = conf().getProperty(Config.maxZ)

        val maxAttempts = conf().getProperty(Config.maxAttempts)

        val rtpWorldPermissionSelf = conf().getProperty(Config.rtpWorldPermissionSelf)
        val rtpWorldPermissionOther = conf().getProperty(Config.rtpWorldPermissionOthers)
        val rtpCoolDownBypassPermission = conf().getProperty(Config.rtpCoolDownBypassPermission)

        val wrongWorldName = conf().getProperty(Config.wrongWorldName)
        val noLocationFound = conf().getProperty(Config.noLocationFound)
        val successfullyTeleported = conf().getProperty(Config.successfullyTeleported)
        val successfullyTeleportedOther = conf().getProperty(Config.successfullyTeleportedOther)
        val targetNotSpecified = conf().getProperty(Config.targetNotSpecified)
        val noPermission = conf().getProperty(Config.noPermission)
        val coolDownRemaining = conf().getProperty(Config.coolDownRemaining)
        val coolDownRemainingTarget = conf().getProperty(Config.coolDownRemainingTarget)


        val teleportWorld = Bukkit.getWorld(worldName)
        val perWorldPermission = "rtp.world.$worldName"

        if (teleportWorld == null) {
            sender.sendMessage(wrongWorldName)
            return
        }

        if (target == null && sender !is Player) {
            targetNotSpecified.msg(sender)
            return
        }

        val player = target ?: sender as Player

        if (target == null && !sender.hasPermission(rtpWorldPermissionSelf) && !sender.hasPermission(perWorldPermission)) {
            noPermission.msg(sender)
            return
        }

        if (target != null && sender is Player && !sender.hasPermission(rtpWorldPermissionOther)) {
            noPermission.msg(sender)
            return
        }

        if ((coolDown != 0 && target == null && !sender.hasPermission(rtpCoolDownBypassPermission)) || (coolDown != 0 && target != null && useCoolDownWhenTeleportedByOthers && !target.hasPermission(rtpCoolDownBypassPermission))) {
            val time = System.currentTimeMillis()
            val newCoolDown = coolDown * 1000.toLong()
            val lastCoolDown = mainClass.getCoolDownsConfig()?.get("${player.uniqueId}").toString().toLongOrNull()
            if (lastCoolDown != null && time - newCoolDown < lastCoolDown) {
                val coolDownLeft = coolDown - ((time - lastCoolDown) / 1000)
                if (target == null) coolDownRemaining.replace("%cooldown%", coolDownLeft.toString()).msg(player)
                else coolDownRemainingTarget.replace("%cooldown%", coolDownLeft.toString()).msg(sender)
                return
            }
        }

        lateinit var randomLocation: Location

        var ok = false
        var attempts = 1
        while (!ok && attempts <= maxAttempts) {
            randomLocation = getRandomLocation(teleportWorld, useBorder, maxX, maxZ)
            if (checkLocationSafety(randomLocation, useWorldGuard)) {
                ok = true
                "Found a safe location in $attempts attempts.".debug()
            } else "Couldn't find a safe location in attempt $attempts. Coords: ${randomLocation.blockX} ${randomLocation.blockY} ${randomLocation.blockZ}".debug()
            attempts++
        }

        if (!ok) {
            noLocationFound.msg(sender)
            return
        }

        PaperLib.teleportAsync(player, randomLocation, PlayerTeleportEvent.TeleportCause.COMMAND)
        if ((coolDown != 0 && target == null) || (coolDown != 0 && target != null && useCoolDownWhenTeleportedByOthers)) mainClass.getCoolDownsConfig()?.set("${player.uniqueId}", System.currentTimeMillis())
        successfullyTeleported.msg(player)
        if (target != null) sender.sendMessage(PlaceholderAPI.setPlaceholders(target, successfullyTeleportedOther))
    }


    @SubCommand("reload")
    fun reload(sender: CommandSender) {

        val noPermission = conf().getProperty(Config.noPermission).color()
        val configReload = conf().getProperty(Config.configReload).color()

        if (sender is Player && !sender.hasPermission("randomteleport.reload")){
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, noPermission))
            return
        }
        conf().reload()
        mainClass.reloadCoolDownsConfig()
        sender.sendMessage(configReload.color())
    }
}