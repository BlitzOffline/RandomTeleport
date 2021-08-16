package com.blitzoffline.randomteleport.config.holder

import me.mattstudios.config.SettingsHolder
import me.mattstudios.config.annotations.Comment
import me.mattstudios.config.annotations.Path
import me.mattstudios.config.configurationdata.CommentsConfiguration
import me.mattstudios.config.properties.Property

object Settings : SettingsHolder {
    override fun registerComments(conf: CommentsConfiguration) {
        conf.setComment(
            "hooks",
            "\n",
            "Here you can enable or disable plugins into which RandomTeleport will hook and use."
        )
    }

    @Path("max-attempts")
    @Comment(
        "This is how many times the plugin checks for a safe location before stopping.",
        "Keep it around 15 or lower for best performance."
    )
    val MAX_ATTEMPTS = Property.create(15)

    @Path("hooks.lands")
    @Comment("If this hook is enabled, the plugin will not teleport players in Lands claimed by someone.")
    val HOOK_LANDS = Property.create(false)

    @Path("hooks.vault")
    @Comment("If this is hook enabled, you can use the teleport-price option and set prices for each teleport.")
    val HOOK_VAULT = Property.create(false)

    @Path("hooks.worldguard")
    @Comment("If hook this is enabled, the plugin will not teleport players in WorldGuard regions.")
    val HOOK_WG = Property.create(true)

    @Path("hooks.griefdefender")
    @Comment("If this hook is enabled, the plugin will not teleport players in GriefDefender claims.")
    val HOOK_GD = Property.create(false)

    @Path("teleport-price")
    @Comment(
        "\n",
        "This option will only work if Vault hook is enabled.",
        "Here you can specify the price players have to pay to use /rtp"
    )
    val TELEPORT_PRICE = Property.create(100)

    @Path("use-border")
    @Comment(
        "\n",
        "Enable this if you want the plugin to use world borders as a max x and z"
    )
    val USE_BORDER = Property.create(true)

    @Path("max-x")
    @Comment(
        "These values are used if useBorder is set to false"
    )
    val MAX_X = Property.create(150)

    @Path("max-z")
    val MAX_Z = Property.create(150)

    @Path("cooldown")
    @Comment(
        "\n",
        "Specify how long the cooldown for the rtp command should be.",
        "Set it to '-1' if you want the cooldown to be disabled."
    )
    val COOLDOWN = Property.create(-1)

    @Path("warmup")
    @Comment(
        "\n",
        "Specify how long the warmup for the rtp command should be.",
        "Set it to '-1' if you want the warmup to be disabled."
    )
    val WARMUP = Property.create(-1)

    @Path("enabled-worlds")
    @Comment(
        "\n",
        "These worlds will be used when using '/rtp <player>'.",
        "You can use the word 'all' and the plugin will use all worlds."
    )
    val ENABLED_WORLDS = Property.create(listOf("world", "world_nether", "all"))
}