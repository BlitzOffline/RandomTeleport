package com.blitzoffline.randomteleport.commands

import com.blitzoffline.randomteleport.RandomTeleport
import com.blitzoffline.randomteleport.config.econ
import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.config.holder.Settings
import com.blitzoffline.randomteleport.config.settings
import com.blitzoffline.randomteleport.cooldown.*
import com.blitzoffline.randomteleport.util.*
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

@Command("randomteleport")
@Alias("rtp", "wild")
class MainCommand(private val plugin: RandomTeleport) : CommandBase() {
    @Default
    fun randomTeleport(sender: CommandSender, @Completion("#players") @Optional target: Player?) {
        val startTime = System.currentTimeMillis()

        if (target == null && sender !is Player) {
            return settings[Messages.NO_TARGET_SPECIFIED].msg(sender)
        }

        val player = target ?: sender as Player

        if (target == null && !player.hasPermission("randomteleport.self")) {
            return settings[Messages.NO_PERMISSION].msg(player)
        }

        if (target != null && !sender.hasPermission("randomteleport.others")) {
            return settings[Messages.NO_PERMISSION].msg(sender)
        }

        if (sender is Player && settings[Settings.HOOK_VAULT] && settings[Settings.TELEPORT_PRICE] > 0 && econ.getBalance(sender) < settings[Settings.TELEPORT_PRICE] && !player.hasPermission("randomteleport.cost.bypass")) {
            return settings[Messages.NOT_ENOUGH_MONEY].msg(sender)
        }

        cooldownCheck(player, target, sender)

        if (warmupsStarted.contains(player.uniqueId) && !player.hasPermission("randomteleport.warmup.bypass")) {
            if (player == sender) settings[Messages.ALREADY_TELEPORTING].msg(player)
            else return settings[Messages.ALREADY_TELEPORTING_TARGET].parsePAPI(player).msg(sender)
        }

        val worldNames = if(settings[Settings.ENABLED_WORLDS].contains("all")) Bukkit.getWorlds().map(World::getName) else settings[Settings.ENABLED_WORLDS]
        val worlds = worldNames.mapNotNull { Bukkit.getWorld(it) }

        if (worlds.isEmpty()) {
            return settings[Messages.CONFIG_WORLDS_WRONG].msg(player)
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
            return settings[Messages.NO_SAFE_LOCATION_FOUND].msg(sender)
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
                        teleportAsync(sender, player, target, newLocation)
                    }, 20 * warmupTime
                )
                return
            }
        }

        teleportAsync(sender, player, target, newLocation)
    }
}