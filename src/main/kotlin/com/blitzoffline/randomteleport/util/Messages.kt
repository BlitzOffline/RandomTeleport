package com.blitzoffline.randomteleport.util

import com.blitzoffline.randomteleport.RandomTeleport
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin


fun String.parsePAPI(player: Player): String = PlaceholderAPI.setPlaceholders(player, this)

val adventure = BukkitAudiences.create(JavaPlugin.getPlugin(RandomTeleport::class.java))
val legacySerializer = LegacyComponentSerializer.legacyAmpersand()

fun String.msg(player: Player) = if (this != "") adventure.player(player).sendMessage(legacySerializer.deserialize(this.parsePAPI(player))) else Unit
fun String.msg(sender: CommandSender) = if (this != "") adventure.sender(sender).sendMessage(legacySerializer.deserialize(this)) else Unit