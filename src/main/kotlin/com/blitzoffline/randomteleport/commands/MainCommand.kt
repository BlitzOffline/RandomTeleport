package com.blitzoffline.randomteleport.commands

import com.blitzoffline.randomteleport.RandomTeleport
import com.blitzoffline.randomteleport.config.econ
import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.config.holder.Settings
import com.blitzoffline.randomteleport.config.settings
import com.blitzoffline.randomteleport.cooldown.cooldowns
import com.blitzoffline.randomteleport.cooldown.isInCooldown
import com.blitzoffline.randomteleport.cooldown.tasks
import com.blitzoffline.randomteleport.cooldown.warmupsStarted
import com.blitzoffline.randomteleport.util.getRandomLocation
import com.blitzoffline.randomteleport.util.isSafe
import com.blitzoffline.randomteleport.util.msg
import me.clip.placeholderapi.PlaceholderAPI
import me.mattstudios.mf.annotations.Alias
import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Completion
import me.mattstudios.mf.annotations.Default
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
class MainCommand(private val plugin: RandomTeleport) : CommandBase() {
    @Default
    fun randomTeleport(sender: CommandSender, @Completion("#players") @Optional target: Player?) {
        val startTime = System.currentTimeMillis()

        if (target == null && sender !is Player) {
            settings[Messages.NO_TARGET_SPECIFIED].msg(sender)
            return
        }

        val player = target ?: sender as Player

        if (target == null && !player.hasPermission("randomteleport.self")) {
            settings[Messages.NO_PERMISSION].msg(player)
            return
        }

        if (target != null && !sender.hasPermission("randomteleport.others")) {
            settings[Messages.NO_PERMISSION].msg(sender)
            return
        }

        if (sender is Player && settings[Settings.HOOK_VAULT] && settings[Settings.TELEPORT_PRICE] > 0 && econ.getBalance(sender) < settings[Settings.TELEPORT_PRICE] && !player.hasPermission("randomteleport.cost.bypass")) {
            settings[Messages.NOT_ENOUGH_MONEY].msg(sender)
            return
        }

        if (player.isInCooldown()) {
            if (target == null) settings[Messages.COOLDOWN_REMAINING].replace("%cooldown%", "${settings[Settings.COOLDOWN] - ((System.currentTimeMillis() - cooldowns[player.uniqueId]!!) / 1000)}").msg(player)
            else PlaceholderAPI.setPlaceholders(target, settings[Messages.COOLDOWN_REMAINING_TARGET].replace("%cooldown%", "${settings[Settings.COOLDOWN] - ((System.currentTimeMillis() - cooldowns[player.uniqueId]!!) / 1000)}")).msg(sender)
            return
        }

        if (warmupsStarted.contains(player.uniqueId) && !player.hasPermission("randomteleport.warmup.bypass")) {
            if (player == sender) settings[Messages.ALREADY_TELEPORTING].msg(player)
            else PlaceholderAPI.setPlaceholders(player, settings[Messages.ALREADY_TELEPORTING_TARGET]).msg(sender)
            return
        }

        val worldNames = if(settings[Settings.ENABLED_WORLDS].contains("all")) Bukkit.getWorlds().map(World::getName) else settings[Settings.ENABLED_WORLDS]
        val worlds = worldNames.mapNotNull { Bukkit.getWorld(it) }

        if (worlds.isEmpty()) {
            settings[Messages.CONFIG_WORLDS_WRONG].msg(player)
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
            settings[Messages.NO_SAFE_LOCATION_FOUND].msg(sender)
            return
        }

        val newLocation = randomLocation.clone().add(0.5, 0.0, 0.5)
        warmupsStarted.add(player.uniqueId)

        if (settings[Settings.WARMUP] > 0 && !player.hasPermission("randomteleport.warmup.bypass")) {
            settings[Messages.WARMUP].msg(player)
            val warmupTime = settings[Settings.WARMUP] - (System.currentTimeMillis() - startTime) / 1000
            if (warmupTime > 0) {
                tasks[player.uniqueId] = Bukkit.getScheduler().runTaskLater(
                    plugin,
                    Runnable {
                        newLocation.world.getChunkAtAsync(newLocation).thenAccept {
                            player.teleportAsync(newLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept {
                                warmupsStarted.remove(player.uniqueId)
                                if (settings[Settings.TELEPORT_PRICE] > 0 && sender is Player && !sender.hasPermission("randomteleport.cost.bypass")) econ.withdrawPlayer(sender, settings[Settings.TELEPORT_PRICE].toDouble())
                                if (settings[Settings.COOLDOWN] > 0) cooldowns[player.uniqueId] = System.currentTimeMillis()
                                if (target != null) PlaceholderAPI.setPlaceholders(target, settings[Messages.TARGET_TELEPORTED_SUCCESSFULLY]).msg(sender)
                                settings[Messages.TELEPORTED_SUCCESSFULLY].msg(player)
                            }
                        }
                        tasks.remove(player.uniqueId)
                    }, 20 * warmupTime
                )
                return
            }
        }

        newLocation.world.getChunkAtAsync(newLocation).thenAccept {
            player.teleportAsync(newLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept {
                warmupsStarted.remove(player.uniqueId)
                if (settings[Settings.TELEPORT_PRICE] > 0 && sender is Player && !sender.hasPermission("randomteleport.cost.bypass")) econ.withdrawPlayer(sender, settings[Settings.TELEPORT_PRICE].toDouble())
                if (settings[Settings.COOLDOWN] > 0) cooldowns[player.uniqueId] = System.currentTimeMillis()
                if (target != null) PlaceholderAPI.setPlaceholders(target, settings[Messages.TARGET_TELEPORTED_SUCCESSFULLY]).msg(sender)
                settings[Messages.TELEPORTED_SUCCESSFULLY].msg(player)
            }
        }
        tasks.remove(player.uniqueId)
    }
}