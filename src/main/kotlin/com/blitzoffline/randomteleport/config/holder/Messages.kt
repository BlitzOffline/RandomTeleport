package com.blitzoffline.randomteleport.config.holder

import me.mattstudios.config.SettingsHolder
import me.mattstudios.config.annotations.Comment
import me.mattstudios.config.annotations.Path
import me.mattstudios.config.properties.Property

object Messages : SettingsHolder {
    @Path("NOT-ENOUGH-MONEY")
    @Comment(
        "Customize the messages that are sent by the plugin:"
    )
    val NOT_ENOUGH_MONEY = Property.create("&cYou don not have enough money!")

    @Path("NO-TARGET-SPECIFIED")
    val NO_TARGET_SPECIFIED = Property.create("&cYou need to specify a target!")

    @Path("TELEPORTED-SUCCESSFULLY")
    val TELEPORTED_SUCCESSFULLY = Property.create("&aYou have been randomly teleported.")

    @Path("TARGET-TELEPORTED-SUCCESSFULLY")
    val TARGET_TELEPORTED_SUCCESSFULLY = Property.create("&aYou have successfully teleported %player_name%.")

    @Path("NO-SAFE-LOCATION-FOUND")
    val NO_SAFE_LOCATION_FOUND = Property.create("&cCould not find a safe location. Try again.")

    @Path("ALREADY-TELEPORTING")
    val ALREADY_TELEPORTING = Property.create("&cYou are already being teleported!")

    @Path("ALREADY-TELEPORTING-TARGET")
    val ALREADY_TELEPORTING_TARGET = Property.create("&cPlayer is already being teleported!")

    @Path("WARMUP")
    val WARMUP = Property.create("&7You will be teleported soon. Do not move!")

    @Path("NO-PERMISSION")
    val NO_PERMISSION = Property.create("&cYou don not have permission to do that.")

    @Path("CONFIG-WORLDS-WRONG")
    val CONFIG_WORLDS_WRONG = Property.create("&cAll worlds listed in config.yml are wrong.")

    @Path("CONFIG-RELOAD")
    val CONFIG_RELOAD = Property.create("&cConfiguration reloaded.")

    @Path("TELEPORT-CANCELED")
    @Comment(
        "\n'TELEPORT-CANCELED' is the only message where you can use the internal placeholder: %reason%",
        "This placeholders will display the reason to why the teleportation was canceled"
    )
    val TELEPORT_CANCELED = Property.create("&cTeleport canceled because you %reason%.")

    @Path("HURT")
    @Comment(
        "\nThese are the messages that will replace the placeholder %reason% in 'TELEPORT-CANCELED'"
    )
    val HURT = Property.create("hurt something/someone")

    @Path("GOT-HURT")
    val GOT_HURT = Property.create("were hurt")

    @Path("PLACED-A-BLOCK")
    val PLACED_A_BLOCK = Property.create("placed a block")

    @Path("BROKE-A-BLOCK")
    val BROKE_A_BLOCK = Property.create("broke a block")

    @Path("MOVED")
    val MOVED = Property.create("moved")

    @Path("COOLDOWN-REMAINING")
    @Comment(
        "\n'COOLDOWN-REMAINING' and 'COOLDOWN-REMAINING-TARGET' are the only messages where you can use the internal placeholder: '%cooldown%'",
        "This placeholder shows how many seconds are left until the player's cooldown is going to expire."
    )
    val COOLDOWN_REMAINING = Property.create("&cYou still have %cooldown% seconds remaining until you can use this again.")

    @Path("COOLDOWN-REMAINING-TARGET")
    val COOLDOWN_REMAINING_TARGET = Property.create("%player_name% has %cooldown% seconds of cooldown left.")
}