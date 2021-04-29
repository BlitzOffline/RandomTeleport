package com.blitzoffline.randomteleport.config.holder

import me.mattstudios.config.SettingsHolder
import me.mattstudios.config.annotations.Path
import me.mattstudios.config.properties.Property

object Settings : SettingsHolder {
    @Path("max-attempts")
    val MAX_ATTEMPTS = Property.create(15)

    @Path("hooks.lands")
    val HOOK_LANDS = Property.create(false)

    @Path("hooks.vault")
    val HOOK_VAULT = Property.create(false)

    @Path("hooks.worldguard")
    val HOOK_WG = Property.create(true)

    @Path("teleport-price")
    val TELEPORT_PRICE = Property.create(100)

    @Path("use-border")
    val USE_BORDER = Property.create(true)

    @Path("max-x")
    val MAX_X = Property.create(150)

    @Path("max-z")
    val MAX_Z = Property.create(150)

    @Path("cooldown")
    val COOLDOWN = Property.create(-1)

    @Path("warmup")
    val WARMUP = Property.create(-1)

    @Path("enabled-worlds")
    val ENABLED_WORLDS = Property.create(listOf("world", "world_nether", "all"))
}