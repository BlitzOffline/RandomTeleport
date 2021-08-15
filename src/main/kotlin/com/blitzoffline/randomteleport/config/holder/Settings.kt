package com.blitzoffline.randomteleport.config.holder

import me.mattstudios.config.SettingsHolder
import me.mattstudios.config.annotations.Comment
import me.mattstudios.config.annotations.Path
import me.mattstudios.config.properties.Property

object Settings : SettingsHolder {
    @Path("max-attempts")
    @Comment(
        "This is how many times the plugin checks for a safe location before stopping.",
        "Keep it around 15 or lower for best performance."
    )
    val MAX_ATTEMPTS = Property.create(15)

    @Path("hooks.lands")
    @Comment(
        "\nEnable or disable Lands and WorldGuard hooks. These are used to not teleport players in claimed lands or in worldguard regions.",
        "Enable or disable Vault hook. This is used for teleport prices."
    )
    val HOOK_LANDS = Property.create(false)

    @Path("hooks.vault")
    val HOOK_VAULT = Property.create(false)

    @Path("hooks.worldguard")
    val HOOK_WG = Property.create(true)

    @Path("teleport-price")
    @Comment(
        "\nThis option will only work if Vault hook is enabled.",
        "Here you can specify the price players have to pay to use /rtp"
    )
    val TELEPORT_PRICE = Property.create(100)

    @Path("use-border")
    @Comment(
        "\nEnable this if you want the plugin to use world borders as a max x and z"
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
        "\nSpecify how long the cooldown for the rtp command should be.",
        "Set it to '-1' if you want the cooldown to be disabled."
    )
    val COOLDOWN = Property.create(-1)

    @Path("warmup")
    @Comment(
        "\nSpecify how long the warmup for the rtp command should be.",
        "Set it to '-1' if you want the warmup to be disabled."
    )
    val WARMUP = Property.create(-1)

    @Path("enabled-worlds")
    @Comment(
        "\nThese worlds will be used when using '/rtp <player>'.",
        "You can use the word 'all' and the plugin will use all worlds."
    )
    val ENABLED_WORLDS = Property.create(listOf("world", "world_nether", "all"))
}