package me.blitzgamer_88.randomteleport.cmd

import io.papermc.lib.PaperLib
import me.blitzgamer_88.randomteleport.RandomTeleport
import me.blitzgamer_88.randomteleport.util.*
import me.clip.placeholderapi.PlaceholderAPI
import me.mattstudios.mf.annotations.*
import me.mattstudios.mf.base.CommandBase
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent


@Command("randomteleport")
@Alias("rtp", "wild")
class CommandRandomTeleport(private val mainClass: RandomTeleport) : CommandBase() {


    @Default
    fun randomTeleport(sender: CommandSender, @Completion("#players") @Optional target: Player?) {

        if (target == null && sender !is Player) {
            sender.sendMessage(targetNotSpecified)
            return
        }

        val player = target ?: sender as Player

        if (target == null && !player.hasPermission("rtp.self")) {
            noPermission.msg(player)
            return
        }

        if (target != null && sender is Player && !sender.hasPermission("rtp.others")) {
            noPermission.msg(sender)
            return
        }

        if (cooldown != 0 && !player.hasPermission("rtp.cooldown.bypass")) {

            val currentTime = System.currentTimeMillis()
            val newCoolDown = cooldown * 1000.toLong()
            val lastCooldown = mainClass.coolDowns[player.uniqueId]

            if (lastCooldown != null && currentTime-newCoolDown < lastCooldown) {
                val coolDownLeft = cooldown - ((currentTime - lastCooldown) / 1000)
                if (target == null) coolDownRemaining.replace("%cooldown%", coolDownLeft.toString()).msg(player)
                else sender.sendMessage(PlaceholderAPI.setPlaceholders(target, coolDownRemainingTarget.replace("%cooldown%", coolDownLeft.toString())))
                return
            }
        }

        val worldNames = if(enabledWorlds.contains("all")) Bukkit.getWorlds().map(World::getName) else enabledWorlds
        val worlds = mutableListOf<World>()

        for (worldName in worldNames) {
            val world = Bukkit.getWorld(worldName) ?: continue
            worlds.add(world)
        }
        if (worlds.isEmpty()) {
            noWorldFound.msg(player)
            return
        }

        val teleportWorld = worlds.shuffled().take(1)[0]
        lateinit var randomLocation: Location
        var ok = false
        var attempts = 1

        while (!ok && attempts < maxAttempts) {
            randomLocation = getRandomLocation(teleportWorld, useBorder, maxX, maxZ)
            if (randomLocation.checkLocationSafety()) ok = true
            attempts++
        }

        if (!ok) {
            noLocationFound.msg(sender)
            return
        }

        PaperLib.teleportAsync(player, randomLocation, PlayerTeleportEvent.TeleportCause.COMMAND)
        if (cooldown != 0) mainClass.coolDowns[player.uniqueId] = System.currentTimeMillis()
        if (target != null) sender.sendMessage(PlaceholderAPI.setPlaceholders(target, successfullyTeleportedOther))
        successfullyTeleported.msg(player)
    }



    @SubCommand("world")
    fun randomTeleportWorld(sender: CommandSender, @Completion("#worlds") worldName: String, @Completion("#players") @Optional target: Player?) {

        val teleportWorld = Bukkit.getWorld(worldName)

        if (target == null && sender !is Player) {
            targetNotSpecified.msg(sender)
            return
        }

        val player = target ?: sender as Player

        if (target == null && !sender.hasPermission("rtp.world") && !sender.hasPermission("rtp.world.$worldName")) {
            noPermission.msg(sender)
            return
        }

        if (target != null && sender is Player && !sender.hasPermission("rtp.world.others")) {
            noPermission.msg(sender)
            return
        }

        if (teleportWorld == null) {
            sender.sendMessage(wrongWorldName)
            return
        }

        if (cooldown > 0 && !player.hasPermission("rtp.cooldown.bypass")) {

            val currentTime = System.currentTimeMillis()
            val newCoolDown = cooldown * 1000.toLong()
            val lastCoolDown = mainClass.coolDowns[player.uniqueId]

            if (lastCoolDown != null && currentTime-newCoolDown < lastCoolDown) {
                val coolDownLeft = cooldown - ((currentTime - lastCoolDown) / 1000)
                if (target == null) coolDownRemaining.replace("%cooldown%", coolDownLeft.toString()).msg(player)
                else sender.sendMessage(PlaceholderAPI.setPlaceholders(target, coolDownRemainingTarget.replace("%cooldown%", coolDownLeft.toString())))
                return
            }
        }

        lateinit var randomLocation: Location

        var ok = false
        var attempts = 1
        while (!ok && attempts <= maxAttempts) {
            randomLocation = getRandomLocation(teleportWorld, useBorder, maxX, maxZ)
            if (randomLocation.checkLocationSafety()) ok = true
            attempts++
        }

        if (!ok) {
            noLocationFound.msg(sender)
            return
        }

        PaperLib.teleportAsync(player, randomLocation, PlayerTeleportEvent.TeleportCause.COMMAND)
        if (cooldown != 0) mainClass.coolDowns[player.uniqueId] = System.currentTimeMillis()
        if (target != null) sender.sendMessage(PlaceholderAPI.setPlaceholders(target, successfullyTeleportedOther))
        successfullyTeleported.msg(player)
    }


    @SubCommand("reload")
    fun reload(sender: CommandSender) {

        if (sender is Player && !sender.hasPermission("rtp.reload")){
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, noPermission))
            return
        }
        mainClass.reload()
        sender.sendMessage(configReload.color())
    }
}