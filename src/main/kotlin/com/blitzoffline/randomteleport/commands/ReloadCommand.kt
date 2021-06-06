package com.blitzoffline.randomteleport.commands

import com.blitzoffline.randomteleport.config.holder.Messages
import com.blitzoffline.randomteleport.config.holder.Settings
import com.blitzoffline.randomteleport.config.messages
import com.blitzoffline.randomteleport.config.settings
import com.blitzoffline.randomteleport.config.setupEconomy
import com.blitzoffline.randomteleport.util.log
import com.blitzoffline.randomteleport.util.msg
import me.mattstudios.mf.annotations.Alias
import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.annotations.SubCommand
import me.mattstudios.mf.base.CommandBase
import org.bukkit.command.CommandSender

@Command("randomteleport")
@Alias("rtp", "wild")
class ReloadCommand : CommandBase() {
    @SubCommand("reload")
    @Permission("randomteleport.admin")
    fun reload(sender: CommandSender) {
        settings.reload()
        if (settings[Settings.HOOK_VAULT]) if (setupEconomy()) "[RandomTeleport] Something went wrong while setting up the Vault hook.".log()
        messages[Messages.CONFIG_RELOAD].msg(sender)
    }
}