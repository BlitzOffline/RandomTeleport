package me.blitzgamer_88.randomteleport.placeholders

import me.blitzgamer_88.randomteleport.RandomTeleport
import me.blitzgamer_88.randomteleport.conf.Config
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
        return "0.0.3"
    }

    override fun getIdentifier(): String {
        return "randomtp"
    }

    override fun persist(): Boolean
    {
        return true
    }

    private fun formatBoolean(b: Boolean): String? {
        return if (b) PlaceholderAPIPlugin.booleanTrue() else PlaceholderAPIPlugin.booleanFalse()
    }

    override fun onRequest(player: OfflinePlayer, input: String): String? {
        when {
            input.startsWith("cooldown") -> {

                if (input.contains('_')) {
                    val args = input.split('_')
                    if (args.size > 2) {
                        return null
                    }
                    if (args[1] == "enabled") {
                        val coolDown = mainClass.conf().getProperty(Config.coolDown)
                        if (coolDown == 0) {
                            return formatBoolean(false)
                        }
                        return formatBoolean(true)
                    }
                    if (args[1] == "left") {
                        val coolDown = mainClass.conf().getProperty(Config.coolDown)
                        if (coolDown == 0) {
                            return "0"
                        }
                        val time = System.currentTimeMillis()
                        val newCoolDown = coolDown*1000.toLong()
                        val savedCoolDown = mainClass.getCoolDownsConfig()?.get("${player.uniqueId}").toString().toLongOrNull()
                        if (savedCoolDown != null && time-newCoolDown < savedCoolDown) {
                            val coolDownLeft = coolDown - ((time - savedCoolDown) / 1000)
                            return coolDownLeft.toString()
                        }
                        return "0"
                    }
                }
            }

            input == "enabled_worlds" -> {
                val enabledWorlds = mainClass.conf().getProperty(Config.enabledWorlds)
                if (enabledWorlds == null || enabledWorlds.contains("all")){
                    return Bukkit.getWorlds().map(World::getName).toString().replace("[", "").replace("]", "")
                }
                return enabledWorlds.toString().replace("[", "").replace("]", "")
            }
        }
        return null
    }


}