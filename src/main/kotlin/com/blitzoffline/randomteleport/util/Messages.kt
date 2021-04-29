package com.blitzoffline.randomteleport.util

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


fun String.parsePAPI(player: Player): String = PlaceholderAPI.setPlaceholders(player, this)

lateinit var adventure: BukkitAudiences
val legacySerializer = LegacyComponentSerializer.legacyAmpersand()

fun String.log() = adventure.console().sendMessage(legacySerializer.deserialize(this))
fun String.msg(player: Player) = adventure.player(player).sendMessage(legacySerializer.deserialize(this.parsePAPI(player)))
fun String.msg(sender: CommandSender) = adventure.sender(sender).sendMessage(legacySerializer.deserialize(this))
fun String.broadcast() = adventure.all().sendMessage(legacySerializer.deserialize(this))