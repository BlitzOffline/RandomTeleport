package me.blitzgamer_88.randomteleport.config

import ch.jalu.configme.Comment
import ch.jalu.configme.SettingsHolder
import ch.jalu.configme.properties.Property
import ch.jalu.configme.properties.PropertyInitializer.newListProperty
import ch.jalu.configme.properties.PropertyInitializer.newProperty


internal object Config : SettingsHolder {

    @JvmField
    @Comment("This is how many times the plugins checks for a safe location before stopping.", "Keep it around 10 or lower for best performance.")
    val maxAttempts: Property<Int> = newProperty("max-attempts", 10)

    @JvmField
    @Comment("If this is enabled, the plugin will check so the teleport location is not in a WorldGuard region")
    val useWorldGuard: Property<Boolean> = newProperty("use-worldguard", true)

    @JvmField
    @Comment("Enable this if you want the plugin to use world borders as a max x and z")
    val useBorder: Property<Boolean> = newProperty("use-border", true)
    @JvmField
    @Comment("These values are used if useBorder is set to false")
    val maxX: Property<Int> = newProperty("max-x", 150)
    @JvmField
    val maxZ: Property<Int> = newProperty("max-z", 150)


    @JvmField
    @Comment("Specify how long the cooldown for the rtp command should be in seconds.", "Set it to '-1' if you want the cooldown to be disabled.")
    val coolDown: Property<Int> = newProperty("cooldown", -1)

    @JvmField
    @Comment("Specify how long the warmup for the rtp command should be.", "Set it to '-1' if you want the warmup to be disabled.")
    val warmup: Property<Int> = newProperty("warmup", -1)

    @JvmField
    @Comment("These worlds will be used when using '/rtp <player>'.", "You can use the word 'all' and the plugin will use all worlds.")
    val enabledWorlds: Property<MutableList<String>> = newListProperty("enabled-worlds", "world", "world_nether", "all")


    @JvmField
    @Comment("Customize the messages that are sent by the plugin:")
    val targetNotSpecified: Property<String> = newProperty("NO-TARGET-SPECIFIED", "&cYou need to specify a target!")
    @JvmField
    val wrongWorldName: Property<String> = newProperty("WRONG-WORLD-NAME", "&cThe world you specified does not exist.")
    @JvmField
    val successfullyTeleported: Property<String> = newProperty("TELEPORTED-SUCCESSFULLY", "&aYou have been randomly teleported.")
    @JvmField
    val successfullyTeleportedOther: Property<String> = newProperty("TARGET-TELEPORTED-SUCCESSFULLY", "&aYou have successfully teleported %player_name%.")
    @JvmField
    val noLocationFound: Property<String> = newProperty("NO-SAFE-LOCATION-FOUND", "&cCould not find a safe location. Try again.")
    @JvmField
    val alreadyTeleporting: Property<String> = newProperty("ALREADY-TELEPORTING", "&cYou are already being teleported!")
    @JvmField
    val alreadyTeleportingTarget: Property<String> = newProperty("ALREADY-TELEPORTING-TARGET", "&cPlayer is already being teleported!")
    @JvmField
    val warmupStarted: Property<String> = newProperty("WARMUP", "&7You will be teleported soon. Do not move!")
    @JvmField
    val noPermission: Property<String> = newProperty("NO-PERMISSION", "&cYou don't have permission to do that.")
    @JvmField
    val noWorldFound: Property<String> = newProperty("CONFIG-WORLD-WRONG", "&cCould not find any worlds.")
    @JvmField
    val configReload: Property<String> = newProperty("CONFIG-RELOAD", "&cConfiguration reloaded.")

    @JvmField
    @Comment("'TELEPORT-CANCELED' is the only message where you can use the internal placeholder: %reason%", "This placeholders will display the reason to why the teleportation was canceled")
    val teleportCanceled: Property<String> = newProperty("TELEPORT-CANCELED", "&cTeleport canceled!")

    @JvmField
    @Comment("These are the messages that will replace the placeholder %reason% in 'TELEPORT-CANCELED'")
    val hurtReason: Property<String> = newProperty("HURT", "hurt something/someone")
    @JvmField
    val gotHurtReason: Property<String> = newProperty("GOT-HURT", "got hurt")
    @JvmField
    val placedABlockReason: Property<String> = newProperty("PLACED-A-BLOCK", "placed a block")
    @JvmField
    val brokeABlockReason: Property<String> = newProperty("BROKE-A-BLOCK:", "broke a block")
    @JvmField
    val movedReason: Property<String> = newProperty("MOVED", "moved")

    @JvmField
    @Comment("'COOLDOWN-REMAINING' and 'COOLDOWN-REMAINING-TARGET' are the only messages where you can use the internal placeholder: '%cooldown%'", "This placeholder shows how many seconds are left until the player's cooldown is going to expire.")
    val coolDownRemaining: Property<String> = newProperty("COOLDOWN-REMAINING", "&cYou still have %cooldown% seconds remaining until you can use this again.")
    @JvmField
    val coolDownRemainingTarget: Property<String> = newProperty("COOLDOWN-REMAINING-TARGET", "%player_name% has %cooldown% seconds of cooldown left.")

}