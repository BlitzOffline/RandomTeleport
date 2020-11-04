package me.blitzgamer_88.randomteleport.util

import me.blitzgamer_88.randomteleport.conf.Config
import org.bukkit.Bukkit

fun String.debug() {
    if (conf().getProperty(Config.debug)) {
        Bukkit.getServer().consoleSender.sendMessage(this)
    }
}