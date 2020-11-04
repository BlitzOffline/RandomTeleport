package me.blitzgamer_88.randomteleport.util

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

// Use the '&X' color format
fun String.color(): String = ChatColor.translateAlternateColorCodes('&', this)

// Parse PAPI placeholders for both Online and Offline Players
fun String.parsePAPI(player: Player): String = PlaceholderAPI.setPlaceholders(player, this.color())
fun String.parsePAPI(player: OfflinePlayer): String = PlaceholderAPI.setPlaceholders(player, this.color())

// Send Log To Console
fun String.log() = Bukkit.getConsoleSender().sendMessage(this.color())

// Send Message To Player
fun String.msg(player: Player) = player.sendMessage(this.color().parsePAPI(player))
fun String.msg(sender: CommandSender) = sender.sendMessage(this.color())

// Send Message To Everyone
fun String.broadcast() = Bukkit.getServer().broadcastMessage(this.color())