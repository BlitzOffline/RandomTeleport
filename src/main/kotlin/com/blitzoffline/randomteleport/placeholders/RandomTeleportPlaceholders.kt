package com.blitzoffline.randomteleport.placeholders

import com.blitzoffline.randomteleport.RandomTeleport
import com.blitzoffline.randomteleport.config.holder.Settings
import com.blitzoffline.randomteleport.config.settings
import com.blitzoffline.randomteleport.cooldown.cooldowns
import me.clip.placeholderapi.PlaceholderAPIPlugin
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.World

class RandomTeleportPlaceholders(private val plugin: RandomTeleport) : PlaceholderExpansion() {

    override fun getIdentifier() = plugin.description.name.toLowerCase()

    override fun getAuthor() = plugin.description.authors[0] ?: "BlitzOffline"

    override fun getVersion() = plugin.description.version

    override fun canRegister() = true

    override fun persist() = true

    private fun Boolean.formatBoolean() = if (this) PlaceholderAPIPlugin.booleanTrue() else PlaceholderAPIPlugin.booleanFalse()

    override fun onRequest(player: OfflinePlayer?, params: String): String? {

        if (params.split("_").size != 2) return null
        val cooldown = settings[Settings.COOLDOWN] * 1000L

        when {
            params.startsWith("cooldown_") -> {
                when (params.substringAfter("cooldown_")) {
                    "left" -> {
                        if (player == null) return null
                        val p = player.player ?: return null
                        if (p.hasPermission("randomteleport.cooldown.bypass")) return "0"
                        val savedCooldown = cooldowns[player.uniqueId] ?: return "0"
                        if (cooldown <= 0L) return "0"
                        val currTime = System.currentTimeMillis()
                        return if (currTime - cooldown < savedCooldown) "${cooldown - (currTime - savedCooldown)}"
                        else "0"
                    }
                    "enabled" -> {
                        return (cooldown > 0).formatBoolean()
                    }
                }
            }
            params == "enabled_worlds" -> {
                return if (settings[Settings.ENABLED_WORLDS].contains("all")) Bukkit.getWorlds().joinToString(", ", transform = World::getName)
                else settings[Settings.ENABLED_WORLDS].joinToString(", ")
            }
        }
        return null
    }


}