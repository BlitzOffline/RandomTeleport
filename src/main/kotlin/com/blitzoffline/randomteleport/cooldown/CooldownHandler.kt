package com.blitzoffline.randomteleport.cooldown

import java.util.UUID
import org.bukkit.scheduler.BukkitTask

val coolDowns = HashMap<UUID, Long>()
val warmupsStarted = mutableListOf<UUID>()
val tasks = HashMap<UUID, BukkitTask>()