package me.blitzgamer_88.randomteleport.util

import me.blitzgamer_88.randomteleport.conf.Config


var enabledWorlds = mutableListOf<String>()

var useBorder = false
var useWorldGuard = false

var maxX = 0
var maxZ = 0

var maxAttempts = 0
var cooldown = 0
var hookWorldGuard = false

var successfullyTeleported = ""
var successfullyTeleportedOther = ""
var noLocationFound = ""
var noWorldFound = ""
var coolDownRemaining = ""
var coolDownRemainingTarget = ""
var targetNotSpecified = ""
var noPermission = ""
var wrongWorldName = ""
var configReload = ""


fun registerValues() {
    hookWorldGuard = conf().getProperty(Config.useWorldGuard)
    cooldown = conf().getProperty(Config.coolDown)

    enabledWorlds = conf().getProperty(Config.enabledWorlds)

    useBorder = conf().getProperty(Config.useBorder)
    useWorldGuard = conf().getProperty(Config.useWorldGuard)

    maxX = conf().getProperty(Config.maxX)
    maxZ = conf().getProperty(Config.maxZ)

    maxAttempts = conf().getProperty(Config.maxAttempts)
}

fun registerMessages() {
    successfullyTeleported = conf().getProperty(Config.successfullyTeleported)
    successfullyTeleportedOther = conf().getProperty(Config.successfullyTeleportedOther)
    noLocationFound = conf().getProperty(Config.noLocationFound)
    noWorldFound = conf().getProperty(Config.noWorldFound)
    coolDownRemaining = conf().getProperty(Config.coolDownRemaining)
    coolDownRemainingTarget = conf().getProperty(Config.coolDownRemainingTarget)
    targetNotSpecified = conf().getProperty(Config.targetNotSpecified)
    noPermission = conf().getProperty(Config.noPermission)
    wrongWorldName = conf().getProperty(Config.wrongWorldName)
    configReload = conf().getProperty(Config.configReload).color()
}