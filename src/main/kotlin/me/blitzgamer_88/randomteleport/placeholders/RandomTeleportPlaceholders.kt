package me.blitzgamer_88.randomteleport.placeholders

import me.blitzgamer_88.randomteleport.RandomTeleport
import me.blitzgamer_88.randomteleport.util.coolDowns
import me.blitzgamer_88.randomteleport.util.cooldown
import me.blitzgamer_88.randomteleport.util.enabledWorlds
import me.clip.placeholderapi.PlaceholderAPIPlugin
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.World

class RandomTeleportPlaceholders(private val mainClass: RandomTeleport) : PlaceholderExpansion() {

    override fun getAuthor(): String {
        return "BlitzGamer_88"
    }

    override fun getVersion(): String {
        return "1.5"
    }

    override fun getIdentifier(): String {
        return "randomtp"
    }

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

                if (args[1] == "enabled") return formatBoolean(cooldown > 0)

                if (args[1] == "left") {
                    if (cooldown == 0) return "0"

                    val currentTime = System.currentTimeMillis()
                    val newCoolDown = cooldown*1000.toLong()
                    val savedCoolDown = coolDowns[player.uniqueId] ?: return null

                    if (currentTime-newCoolDown < savedCoolDown) {
                        val coolDownLeft = cooldown - ((currentTime - savedCoolDown) / 1000)
                        return coolDownLeft.toString()
                    }
                    return "0"
                }
            }

            input == "enabled_worlds" -> {
                if (enabledWorlds.isEmpty() || enabledWorlds.contains("all")){
                    return Bukkit.getWorlds().map(World::getName).toString().replace("[", "").replace("]", "")
                }
                return enabledWorlds.toString().replace("[", "").replace("]", "")
            }
        }
        return null
    }


}