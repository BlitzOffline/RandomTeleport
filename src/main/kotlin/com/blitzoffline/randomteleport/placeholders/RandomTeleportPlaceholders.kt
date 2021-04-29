package com.blitzoffline.randomteleport.placeholders

import com.blitzoffline.randomteleport.RandomTeleport
import com.blitzoffline.randomteleport.config.holder.Settings
import com.blitzoffline.randomteleport.config.settings
import com.blitzoffline.randomteleport.cooldown.coolDowns
import me.clip.placeholderapi.PlaceholderAPIPlugin
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.World

class RandomTeleportPlaceholders(private val plugin: RandomTeleport) : PlaceholderExpansion() {

    override fun getAuthor() = plugin.description.authors[0] ?: "BlitzOffline"

    override fun getVersion() = plugin.description.version

    override fun getIdentifier() = plugin.description.name.toLowerCase()

    override fun persist(): Boolean {
        return true
    }

    private fun formatBoolean(b: Boolean): String {
        return if (b) PlaceholderAPIPlugin.booleanTrue() else PlaceholderAPIPlugin.booleanFalse()
    }

    override fun onRequest(player: OfflinePlayer, input: String): String? {
        when {
            input.startsWith("cooldown") -> {

                if (!input.contains('_')) return null

                val args = input.split('_')
                if (args.size > 2) return null

                val cooldown = settings[Settings.COOLDOWN]

                if (args[1] == "enabled") return formatBoolean(cooldown > 0)

                if (args[1] == "left") {
                    if (cooldown == 0) return "0"

                    val currentTime = System.currentTimeMillis()
                    val newCoolDown = cooldown *1000.toLong()
                    val savedCoolDown = coolDowns[player.uniqueId] ?: return null

                    if (currentTime-newCoolDown < savedCoolDown) {
                        val coolDownLeft = cooldown - ((currentTime - savedCoolDown) / 1000)
                        return coolDownLeft.toString()
                    }
                    return "0"
                }
            }

            input == "enabled_worlds" -> {
                if (settings[Settings.ENABLED_WORLDS].isEmpty() || settings[Settings.ENABLED_WORLDS].contains("all")){
                    return Bukkit.getWorlds().joinToString(", ", transform = World::getName)
                }
                return settings[Settings.ENABLED_WORLDS].joinToString(", ")
            }
        }
        return null
    }


}