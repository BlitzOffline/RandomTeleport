package me.blitzgamer_88.randomteleport.util

import me.blitzgamer_88.randomteleport.config.Config
import org.bukkit.scheduler.BukkitTask
import java.util.*
import kotlin.collections.HashMap


val coolDowns = HashMap<UUID, Long>()
val warmupsStarted = mutableListOf<UUID>()
val tasks = HashMap<UUID, BukkitTask>()

var enabledWorlds = mutableListOf<String>()
var useBorder = false
var useLands = false
var useWorldGuard = false
var maxX = 0
var maxZ = 0
var maxAttempts = 0
var cooldown = 0
var warmup = 0

lateinit var successfullyTeleported: String
lateinit var successfullyTeleportedOther: String
lateinit var noLocationFound: String
lateinit var noWorldFound: String
lateinit var coolDownRemaining: String
lateinit var coolDownRemainingTarget: String
lateinit var targetNotSpecified: String
lateinit var noPermission: String
lateinit var wrongWorldName: String
lateinit var configReload: String
lateinit var alreadyTeleporting: String
lateinit var alreadyTeleportingTarget: String
lateinit var warmupStarted: String

lateinit var teleportCanceled: String

lateinit var hurtReason: String
lateinit var gotHurtReason: String
lateinit var placedABlockReason: String
lateinit var brokeABlockReason: String
lateinit var movedReason: String


fun loadSettings() {
    cooldown = conf.getProperty(Config.coolDown)
    warmup = conf.getProperty(Config.warmup)
    enabledWorlds = conf.getProperty(Config.enabledWorlds)
    useBorder = conf.getProperty(Config.useBorder)
    useLands = conf.getProperty(Config.useLands)
    useWorldGuard = conf.getProperty(Config.useWorldGuard)
    maxX = conf.getProperty(Config.maxX)
    maxZ = conf.getProperty(Config.maxZ)
    maxAttempts = conf.getProperty(Config.maxAttempts)
}

fun loadMessages() {
    successfullyTeleported = conf.getProperty(Config.successfullyTeleported)
    successfullyTeleportedOther = conf.getProperty(Config.successfullyTeleportedOther)
    noLocationFound = conf.getProperty(Config.noLocationFound)
    noWorldFound = conf.getProperty(Config.noWorldFound)
    coolDownRemaining = conf.getProperty(Config.coolDownRemaining)
    coolDownRemainingTarget = conf.getProperty(Config.coolDownRemainingTarget)
    targetNotSpecified = conf.getProperty(Config.targetNotSpecified)
    noPermission = conf.getProperty(Config.noPermission)
    wrongWorldName = conf.getProperty(Config.wrongWorldName)
    configReload = conf.getProperty(Config.configReload)
    alreadyTeleporting = conf.getProperty(Config.alreadyTeleporting)
    alreadyTeleportingTarget = conf.getProperty(Config.alreadyTeleportingTarget)
    warmupStarted = conf.getProperty(Config.warmupStarted)
    teleportCanceled = conf.getProperty(Config.teleportCanceled)

    hurtReason = conf.getProperty(Config.hurtReason)
    gotHurtReason = conf.getProperty(Config.gotHurtReason)
    placedABlockReason = conf.getProperty(Config.placedABlockReason)
    brokeABlockReason = conf.getProperty(Config.brokeABlockReason)
    movedReason = conf.getProperty(Config.movedReason)
}