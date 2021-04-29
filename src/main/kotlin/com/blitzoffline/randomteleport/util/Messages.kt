package com.blitzoffline.randomteleport.util

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun String.color(): String = ChatColor.translateAlternateColorCodes('&', this)

fun String.parsePAPI(player: Player): String = PlaceholderAPI.setPlaceholders(player, this.color())

fun String.log() = Bukkit.getConsoleSender().sendMessage(this.color())

fun String.msg(player: Player) = player.sendMessage(this.color().parsePAPI(player))
fun String.msg(sender: CommandSender) = sender.sendMessage(this.color())