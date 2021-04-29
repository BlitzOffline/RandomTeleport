package com.blitzoffline.randomteleport.commands

import com.blitzoffline.randomteleport.RandomTeleport
import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.config.holder.Settings
import com.blitzoffline.randomteleport.config.messages
import com.blitzoffline.randomteleport.config.settings
import com.blitzoffline.randomteleport.cooldown.cooldowns
import com.blitzoffline.randomteleport.cooldown.tasks
import com.blitzoffline.randomteleport.cooldown.warmupsStarted
import com.blitzoffline.randomteleport.util.getRandomLocation
import com.blitzoffline.randomteleport.util.isInCooldown
import com.blitzoffline.randomteleport.util.isSafe
import com.blitzoffline.randomteleport.util.msg
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
            messages[Messages.NO_TARGET_SPECIFIED].msg(sender)
            return
        }

        val player = target ?: sender as Player

        if (target == null && !player.hasPermission("rtp.self")) {
            messages[Messages.NO_PERMISSION].msg(player)
            return
        }

        if (target != null && !sender.hasPermission("rtp.others")) {
            messages[Messages.NO_PERMISSION].msg(sender)
            return
        }

        if (player.isInCooldown()) {
            if (target == null) messages[Messages.COOLDOWN_REMAINING].replace("%cooldown%", "${settings[Settings.COOLDOWN] - ((System.currentTimeMillis() - cooldowns[player.uniqueId]!!) / 1000)}").msg(player)
            else PlaceholderAPI.setPlaceholders(target, messages[Messages.COOLDOWN_REMAINING_TARGET].replace("%cooldown%", "${settings[Settings.COOLDOWN] - ((System.currentTimeMillis() - cooldowns[player.uniqueId]!!) / 1000)}")).msg(sender)
            return
        }

        if (warmupsStarted.contains(player.uniqueId) && !player.hasPermission("rtp.warmup.bypass")) {
            if (player == sender) messages[Messages.ALREADY_TELEPORTING].msg(player)
            else PlaceholderAPI.setPlaceholders(player, messages[Messages.ALREADY_TELEPORTING_TARGET]).msg(sender)
            return
        }

        val worldNames = if(settings[Settings.ENABLED_WORLDS].contains("all")) Bukkit.getWorlds().map(World::getName) else settings[Settings.ENABLED_WORLDS]
        val worlds = mutableListOf<World>()

        for (worldName in worldNames) {
            val world = Bukkit.getWorld(worldName) ?: continue
            worlds.add(world)
        }

        if (worlds.isEmpty()) {
            messages[Messages.CONFIG_WORLDS_WRONG].msg(player)
            return
        }

        val teleportWorld = worlds.shuffled()[0]

        lateinit var randomLocation: Location
        var ok = false
        var attempts = 0
        while (!ok && attempts < settings[Settings.MAX_ATTEMPTS]) {
            randomLocation = getRandomLocation(teleportWorld, settings[Settings.USE_BORDER], settings[Settings.MAX_X], settings[Settings.MAX_Z])
            ok = randomLocation.isSafe()
            attempts++
        }

        if (!ok) {
            messages[Messages.NO_SAFE_LOCATION_FOUND].msg(sender)
            return
        }

        val newLocation = randomLocation.clone().add(0.5, 0.0, 0.5)
        warmupsStarted.add(player.uniqueId)

        if (settings[Settings.WARMUP] > 0 && !player.hasPermission("rtp.warmup.bypass")) {
            messages[Messages.WARMUP].msg(player)
            val warmupTime = settings[Settings.WARMUP] - (System.currentTimeMillis() / 1000 - startTime)
            if (warmupTime > 0) {
                tasks[player.uniqueId] = Bukkit.getScheduler().runTaskLater(
                    plugin,
                    Runnable {
                        newLocation.world.getChunkAtAsync(newLocation).thenAccept {
                            player.teleportAsync(newLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept {
                                if (settings[Settings.COOLDOWN] > 0) cooldowns[player.uniqueId] = System.currentTimeMillis()
                                if (target != null) PlaceholderAPI.setPlaceholders(target, messages[Messages.TARGET_TELEPORTED_SUCCESSFULLY]).msg(sender)
                                messages[Messages.TELEPORTED_SUCCESSFULLY].msg(player)
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
                if (settings[Settings.COOLDOWN] > 0) cooldowns[player.uniqueId] = System.currentTimeMillis()
                if (target != null) PlaceholderAPI.setPlaceholders(target, messages[Messages.TARGET_TELEPORTED_SUCCESSFULLY]).msg(sender)
                messages[Messages.TELEPORTED_SUCCESSFULLY].msg(player)
            }
        }
        tasks.remove(player.uniqueId)
    }

    @SubCommand("world")
    fun randomTeleportWorld(sender: CommandSender, @Completion("#worlds") worldName: String, @Completion("#players") @Optional target: Player?) {
        val startTime = System.currentTimeMillis() / 1000

        if (target == null && sender !is Player) {
            messages[Messages.NO_TARGET_SPECIFIED].msg(sender)
            return
        }

        val player = target ?: sender as Player

        if (target == null && !player.hasPermission("rtp.world") && !player.hasPermission("rtp.world.$worldName")) {
            messages[Messages.NO_PERMISSION].msg(player)
            return
        }

        if (target != null && !sender.hasPermission("rtp.world.others")) {
            messages[Messages.NO_PERMISSION].msg(sender)
            return
        }

        if (warmupsStarted.contains(player.uniqueId) && !player.hasPermission("rtp.warmup.bypass")) {
            if (player == sender) messages[Messages.ALREADY_TELEPORTING].msg(player)
            else PlaceholderAPI.setPlaceholders(player, messages[Messages.ALREADY_TELEPORTING_TARGET]).msg(sender)
            return
        }

        val teleportWorld = Bukkit.getWorld(worldName)
        if (teleportWorld == null) {
            messages[Messages.WRONG_WORLD_NAME].msg(sender)
            return
        }

        if (player.isInCooldown()) {
            if (target == null) messages[Messages.COOLDOWN_REMAINING].replace("%cooldown%", "${settings[Settings.COOLDOWN] - ((System.currentTimeMillis() - cooldowns[player.uniqueId]!!) / 1000)}").msg(player)
            else PlaceholderAPI.setPlaceholders(target, messages[Messages.COOLDOWN_REMAINING_TARGET].replace("%cooldown%", "${settings[Settings.COOLDOWN] - ((System.currentTimeMillis() - cooldowns[player.uniqueId]!!) / 1000)}")).msg(sender)
            return
        }

        lateinit var randomLocation: Location

        var ok = false
        var attempts = 0
        while (!ok && attempts <= settings[Settings.MAX_ATTEMPTS]) {
            randomLocation = getRandomLocation(teleportWorld, settings[Settings.USE_BORDER], settings[Settings.MAX_X], settings[Settings.MAX_Z])
            ok = randomLocation.isSafe()
            attempts++
        }

        if (!ok) {
            messages[Messages.NO_SAFE_LOCATION_FOUND].msg(sender)
            return
        }

        val newLocation = randomLocation.clone().add(0.5, 0.0, 0.5)
        warmupsStarted.add(player.uniqueId)

        if (settings[Settings.WARMUP] > 0 && !player.hasPermission("rtp.warmup.bypass")) {
            messages[Messages.WARMUP].msg(player)
            val warmupTime = settings[Settings.WARMUP] - (System.currentTimeMillis() / 1000 - startTime)
            if (warmupTime > 0) {
                tasks[player.uniqueId] = Bukkit.getScheduler().runTaskLater(
                    plugin,
                    Runnable {
                        newLocation.world.getChunkAtAsync(newLocation).thenAccept {
                            player.teleportAsync(newLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept {
                                if (settings[Settings.COOLDOWN] > 0) cooldowns[player.uniqueId] = System.currentTimeMillis()
                                if (target != null) PlaceholderAPI.setPlaceholders(target, messages[Messages.TARGET_TELEPORTED_SUCCESSFULLY]).msg(sender)
                                messages[Messages.TELEPORTED_SUCCESSFULLY].msg(player)
                            }
                        }
                        tasks.remove(player.uniqueId)
                    }, 20L * warmupTime
                )
            }
        }

        newLocation.world.getChunkAtAsync(newLocation).thenAccept {
            player.teleportAsync(newLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept {
                if (settings[Settings.COOLDOWN] > 0) cooldowns[player.uniqueId] = System.currentTimeMillis()
                if (target != null) PlaceholderAPI.setPlaceholders(target, messages[Messages.TARGET_TELEPORTED_SUCCESSFULLY]).msg(sender)
                messages[Messages.TELEPORTED_SUCCESSFULLY].msg(player)
            }
        }
        tasks.remove(player.uniqueId)
    }

    @SubCommand("reload")
    @Permission("rtp.reload")
    fun reload(sender: CommandSender) {
        plugin.reload()
        messages[Messages.CONFIG_RELOAD].msg(sender)
    }
}