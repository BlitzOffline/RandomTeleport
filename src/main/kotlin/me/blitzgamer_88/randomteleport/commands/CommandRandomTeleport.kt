package me.blitzgamer_88.randomteleport.commands

import me.blitzgamer_88.randomteleport.RandomTeleport
import me.blitzgamer_88.randomteleport.util.*
import me.clip.placeholderapi.PlaceholderAPI
import me.mattstudios.mf.annotations.*
import me.mattstudios.mf.annotations.Optional
import me.mattstudios.mf.base.CommandBase
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent


@Command("randomteleport")
@Alias("rtp", "wild")
class CommandRandomTeleport(private val plugin: RandomTeleport) : CommandBase() {

    @Default
    fun randomTeleport(sender: CommandSender, @Completion("#players") @Optional target: Player?) {

        val startTime = System.currentTimeMillis()/1000

        if (target == null && sender !is Player) {
            targetNotSpecified.msg(sender)
            return
        }

        val player = target ?: sender as Player

        if (target == null && !player.hasPermission("rtp.self")) {
            noPermission.msg(player)
            return
        }

        if (target != null && !sender.hasPermission("rtp.others")) {
            noPermission.msg(sender)
            return
        }

        if (player.isInCooldown()) {
            if (target == null) coolDownRemaining.replace("%cooldown%", "${cooldown - ((System.currentTimeMillis() - coolDowns[player.uniqueId]!!) / 1000)}").msg(player)
            else PlaceholderAPI.setPlaceholders(target, coolDownRemainingTarget.replace("%cooldown%", "${cooldown - ((System.currentTimeMillis() - coolDowns[player.uniqueId]!!) / 1000)}")).msg(sender)
            return
        }

        if (warmupsStarted.contains(player.uniqueId) && !player.hasPermission("rtp.warmup.bypass")) {
            if (player == sender) alreadyTeleporting.msg(player)
            else PlaceholderAPI.setPlaceholders(player, alreadyTeleportingTarget).msg(sender)
            return
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

        val teleportWorld = worlds.shuffled()[0]

        lateinit var randomLocation: Location
        var ok = false
        var attempts = 0
        while (!ok && attempts < maxAttempts) {
            randomLocation = getRandomLocation(teleportWorld, useBorder, maxX, maxZ)
            ok = randomLocation.isSafe()
            attempts++
        }

        if (!ok) {
            noLocationFound.msg(sender)
            return
        }

        val newLocation = randomLocation.clone().add(0.5, 0.0, 0.5)
        warmupsStarted.add(player.uniqueId)

        if (warmup > 0 && !player.hasPermission("rtp.warmup.bypass")) {
            warmupStarted.msg(player)
            val warmupTime = warmup - (System.currentTimeMillis() / 1000 - startTime)
            if (warmupTime > 0) {
                tasks[player.uniqueId] = Bukkit.getScheduler().runTaskLater(
                    plugin,
                    Runnable {
                        newLocation.world.getChunkAtAsync(newLocation).thenAccept {
                            player.teleportAsync(newLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept {
                                if (cooldown > 0) coolDowns[player.uniqueId] = System.currentTimeMillis()
                                if (target != null) PlaceholderAPI.setPlaceholders(target, successfullyTeleportedOther).msg(sender)
                                successfullyTeleported.msg(player)
                            }
                        }
                        tasks.remove(player.uniqueId)
                    }, 20L * warmupTime
                )
                return
            }
        }

        newLocation.world.getChunkAtAsync(newLocation).thenAccept {
            player.teleportAsync(newLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept {
                if (cooldown > 0) coolDowns[player.uniqueId] = System.currentTimeMillis()
                if (target != null) PlaceholderAPI.setPlaceholders(target, successfullyTeleportedOther).msg(sender)
                successfullyTeleported.msg(player)
            }
        }
        tasks.remove(player.uniqueId)
    }

    @SubCommand("world")
    fun randomTeleportWorld(sender: CommandSender, @Completion("#worlds") worldName: String, @Completion("#players") @Optional target: Player?) {

        val startTime = System.currentTimeMillis()/1000

        if (target == null && sender !is Player) {
            targetNotSpecified.msg(sender)
            return
        }

        val player = target ?: sender as Player

        if (target == null && !player.hasPermission("rtp.world") && !player.hasPermission("rtp.world.$worldName")) {
            noPermission.msg(player)
            return
        }

        if (target != null && !sender.hasPermission("rtp.world.others")) {
            noPermission.msg(sender)
            return
        }

        if (warmupsStarted.contains(player.uniqueId) && !player.hasPermission("rtp.warmup.bypass")) {
            if (player == sender) alreadyTeleporting.msg(player)
            else PlaceholderAPI.setPlaceholders(player, alreadyTeleportingTarget).msg(sender)
            return
        }

        val teleportWorld = Bukkit.getWorld(worldName)
        if (teleportWorld == null) {
            wrongWorldName.msg(sender)
            return
        }

        if (player.isInCooldown()) {
            if (target == null) coolDownRemaining.replace("%cooldown%", "${cooldown - ((System.currentTimeMillis() - coolDowns[player.uniqueId]!!) / 1000)}").msg(player)
            else PlaceholderAPI.setPlaceholders(target, coolDownRemainingTarget.replace("%cooldown%", "${cooldown - ((System.currentTimeMillis() - coolDowns[player.uniqueId]!!) / 1000)}")).msg(sender)
            return
        }

        lateinit var randomLocation: Location

        var ok = false
        var attempts = 0
        while (!ok && attempts <= maxAttempts) {
            randomLocation = getRandomLocation(teleportWorld, useBorder, maxX, maxZ)
            ok = randomLocation.isSafe()
            attempts++
        }

        if (!ok) {
            noLocationFound.msg(sender)
            return
        }

        val newLocation = randomLocation.clone().add(0.5, 0.0, 0.5)
        warmupsStarted.add(player.uniqueId)

        if (warmup > 0 && !player.hasPermission("rtp.warmup.bypass")) {
            warmupStarted.msg(player)
            val warmupTime = warmup - (System.currentTimeMillis() / 1000 - startTime)
            if (warmupTime > 0) {
                tasks[player.uniqueId] = Bukkit.getScheduler().runTaskLater(
                    plugin,
                    Runnable {
                        newLocation.world.getChunkAtAsync(newLocation).thenAccept {
                            player.teleportAsync(newLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept {
                                if (cooldown > 0) coolDowns[player.uniqueId] = System.currentTimeMillis()
                                if (target != null) PlaceholderAPI.setPlaceholders(target, successfullyTeleportedOther).msg(sender)
                                successfullyTeleported.msg(player)
                            }
                        }
                        tasks.remove(player.uniqueId)
                    }, 20L * warmupTime
                )
            }
        }

        newLocation.world.getChunkAtAsync(newLocation).thenAccept {
            player.teleportAsync(newLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept {
                if (cooldown > 0) coolDowns[player.uniqueId] = System.currentTimeMillis()
                if (target != null) PlaceholderAPI.setPlaceholders(target, successfullyTeleportedOther).msg(sender)
                successfullyTeleported.msg(player)
            }
        }
        tasks.remove(player.uniqueId)
    }

    @SubCommand("reload")
    @Permission("rtp.reload")
    fun reload(sender: CommandSender) {
        plugin.reload()
        configReload.msg(sender)
    }
}