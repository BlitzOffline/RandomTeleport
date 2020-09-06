package me.blitzgamer_88.randomteleport.conf

import ch.jalu.configme.Comment
import ch.jalu.configme.SettingsHolder
import ch.jalu.configme.properties.Property
import ch.jalu.configme.properties.PropertyBuilder
import ch.jalu.configme.properties.PropertyInitializer
import ch.jalu.configme.properties.types.PrimitivePropertyType


internal object Config : SettingsHolder {

    @JvmField
    @Comment("This is how many times the plugins checks for a safe location before stopping.")
    val maxAttempts: Property<Int> = PropertyInitializer.newProperty("maxAttempts", 10)

    @JvmField
    @Comment(" ", "If this is enabled, the plugin will check so the teleport location is not in a world guard region")
    val useWorldGuard: Property<Boolean> = PropertyInitializer.newProperty("useWorldGuard", true)

    @JvmField
    @Comment(" ", "If the plugins uses the world border as a max y and z")
    val useBorder: Property<Boolean> = PropertyInitializer.newProperty("useBorder", true)
    @JvmField
    @Comment("These values are used if useBorder is set to false")
    val maxX: Property<Int> = PropertyInitializer.newProperty("maxX", 150)
    @JvmField
    val maxZ: Property<Int> = PropertyInitializer.newProperty("maxZ", 150)


    @JvmField
    @Comment(" ", "These worlds will be used when using '/rtp <player>'.", "You can use the word 'all' and the plugin will use all worlds.")
    val enabledWorlds: Property<MutableList<String>> = PropertyBuilder.ListPropertyBuilder(PrimitivePropertyType.STRING)
        .path("enabled_worlds")
        .defaultValue(listOf("world", "world_nether", "all"))
        .build()


    @JvmField
    @Comment(" ", "This permission will be used for a player to use the '/rtp' command on themselves.")
    val rtpPermissionSelf: Property<String> = PropertyInitializer.newProperty("rtpPermissionSelf", "rtp.self")
    @JvmField
    @Comment("This permission will be used for a player to use the '/rtp' command on other players.")
    val rtpPermissionOthers: Property<String> = PropertyInitializer.newProperty("rtpPermissionOthers", "rtp.others")
    @JvmField
    @Comment("This permission will be used for a player to use the '/rtp world' command on themselves.")
    val rtpWorldPermissionSelf: Property<String> = PropertyInitializer.newProperty("rtpWorldPermissionSelf", "rtp.world")
    @Comment("This permission will be used for a player to use the '/rtp world' command on other players.")
    @JvmField
    val rtpWorldPermissionOthers: Property<String> = PropertyInitializer.newProperty("rtpWorldPermissionOthers", "rtp.world.others")

//    @JvmField
//    @Comment("", "Enable debug mode")
//    val debug: Property<Boolean> = PropertyInitializer.newProperty("debug", false)

    @JvmField
    @Comment(" ", "Customize the messages that are sent by the plugin:")
    val targetNotSpecified: Property<String> = PropertyInitializer.newProperty("targetNotSpecified", "&cYou need to specify a target!")
    @JvmField
    val noPermission: Property<String> = PropertyInitializer.newProperty("noPermission", "&cYou don't have permission to do that.")
    @JvmField
    val wrongWorldName: Property<String> = PropertyInitializer.newProperty("wrongWorldName", "&cThe world you specified does not exist.")
    @JvmField
    val successfullyTeleported: Property<String> = PropertyInitializer.newProperty("successfullyTeleported", "&aYou have been randomly teleported.")
    @JvmField
    val successfullyTeleportedOther: Property<String> = PropertyInitializer.newProperty("successfullyTeleportedOther", "&aYou have successfully teleported %player_name%.")
    @JvmField
    val noLocationFound: Property<String> = PropertyInitializer.newProperty("noLocationFound", "&cCould not find a safe location. Try again.")
    @JvmField
    val noWorldFound: Property<String> = PropertyInitializer.newProperty("noWorldFound", "&cAll worlds listed in config.yml are wrong.")
    @JvmField
    val configReload: Property<String> = PropertyInitializer.newProperty("configReload", "&cConfiguration reloaded.")
}