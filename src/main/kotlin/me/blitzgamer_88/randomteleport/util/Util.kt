package me.blitzgamer_88.randomteleport.util

import org.bukkit.ChatColor

fun String.color(): String = ChatColor.translateAlternateColorCodes('&', this)