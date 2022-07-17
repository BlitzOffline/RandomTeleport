package com.blitzoffline.randomteleport.commands

import com.blitzoffline.randomteleport.RandomTeleport
import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.util.msg
import dev.triumphteam.cmd.bukkit.annotation.Permission
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.SubCommand
import org.bukkit.command.CommandSender

@Command(value = "randomteleport", alias = ["rtp", "wild"])
class CommandReload(private val plugin: RandomTeleport) : BaseCommand() {

    @SubCommand("reload")
    @Permission("randomteleport.admin")
    fun reload(sender: CommandSender) {
        plugin.settings.reload()
        plugin.messages.reload()
        plugin.messages[Messages.CONFIG_RELOAD].msg(sender)
    }
}