package com.blitzoffline.randomteleport.commands

import com.blitzoffline.randomteleport.RandomTeleport
import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.config.messages
import com.blitzoffline.randomteleport.util.msg
import me.mattstudios.mf.annotations.Alias
import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.annotations.SubCommand
import me.mattstudios.mf.base.CommandBase
import org.bukkit.command.CommandSender

@Command("randomteleport")
@Alias("rtp", "wild")
class ReloadCommand(private val plugin: RandomTeleport) : CommandBase() {
    @SubCommand("reload")
    @Permission("randomteleport.admin")
    fun reload(sender: CommandSender) {
        plugin.reload()
        messages[Messages.CONFIG_RELOAD].msg(sender)
    }
}