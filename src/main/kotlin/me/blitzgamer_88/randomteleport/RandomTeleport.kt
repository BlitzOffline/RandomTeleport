package me.blitzgamer_88.randomteleport

import me.blitzgamer_88.randomteleport.cmd.CommandRandomTeleport
import me.blitzgamer_88.randomteleport.conf.Config
import me.blitzgamer_88.randomteleport.placeholders.RandomTeleportPlaceholders
import me.blitzgamer_88.randomteleport.util.conf
import me.blitzgamer_88.randomteleport.util.loadConfig
import me.blitzgamer_88.randomteleport.util.log
import me.bristermitten.pdm.PDMBuilder
import me.mattstudios.mf.base.CommandManager
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.io.Reader
import java.util.logging.Level


class RandomTeleport : JavaPlugin() {

    override fun onEnable() {

        PDMBuilder(this).build().loadAllDependencies().join()

        this.saveDefaultConfig()
        loadConfig(this)

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            "&cCould not find PlaceholderAPI! This plugin is required.".log()
            Bukkit.getPluginManager().disablePlugin(this)
        }
        else { "&aHooked into PlaceholderAPI.".log() }

        val hookWorldGuard = conf().getProperty(Config.useWorldGuard)
        if (hookWorldGuard && Bukkit.getPluginManager().getPlugin("WorldGuard") == null) "&cCould not hook into WorldGuard. Plugin not found.".log()
        else if (hookWorldGuard) "&aHooked into WorldGuard".log()

        val papi = RandomTeleportPlaceholders(this)
        papi.register()

        val cmdManager = CommandManager(this, true)
        cmdManager.completionHandler.register("#worlds") { Bukkit.getWorlds().map(World::getName) }
        cmdManager.register(CommandRandomTeleport(this))

        "&a[RandomTeleport] Plugin enabled!".log()
    }


    override fun onDisable() {
        "&c[RandomTeleport] Plugin disabled!".log()
    }


    // PLAYER COOL-DOWNS

    private var coolDowns: FileConfiguration? = null
    private var coolDownsFile: File? = null

    fun reloadCoolDownsConfig() {
        if (coolDownsFile == null) coolDownsFile = File(dataFolder, "cooldowns.yml")
        coolDowns = YamlConfiguration.loadConfiguration(coolDownsFile!!)
    }

    fun saveCoolDownsConfig() {
        if (coolDowns == null || coolDownsFile == null) return
        try { getCoolDownsConfig()!!.save(coolDownsFile!!) }
        catch (ex: IOException) { logger.log(Level.SEVERE, "Could not save cooldowns to $coolDownsFile", ex) }
    }

    fun getCoolDownsConfig(): FileConfiguration? {
        if (coolDowns == null) reloadCoolDownsConfig()
        return coolDowns
    }

}