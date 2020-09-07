package me.blitzgamer_88.randomteleport

import ch.jalu.configme.SettingsManager
import io.papermc.lib.PaperLib
import me.blitzgamer_88.randomteleport.cmd.CommandRandomTeleport
import me.blitzgamer_88.randomteleport.conf.RandomTeleportConfiguration
import me.blitzgamer_88.randomteleport.placeholders.RandomTeleportPlaceholders
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

    // CONFIG.YML

    private var conf = null as? SettingsManager?

    private fun loadConfig() {
        val file = this.dataFolder.resolve("config.yml")

        if (!file.exists())
        {
            file.parentFile.mkdirs()
            file.createNewFile()
        }

        this.conf = RandomTeleportConfiguration(file)
    }

    fun conf(): SettingsManager
    {
        return checkNotNull(conf)
    }


    // PLAYER COOL-DOWNS

    private var coolDowns: FileConfiguration? = null
    private var coolDownsFile: File? = null

    fun reloadCoolDownsConfig() {
        if (coolDownsFile == null) {
            coolDownsFile = File(dataFolder, "cooldowns.yml")
        }
        coolDowns = YamlConfiguration.loadConfiguration(coolDownsFile!!)

        // Look for defaults in the jar
        val defConfigStream: Reader? = getResource("cooldowns.yml")?.reader()
        if (defConfigStream != null){
            val defConfig = YamlConfiguration.loadConfiguration(defConfigStream)
            (coolDowns as YamlConfiguration).setDefaults(defConfig)
        }
    }

    fun saveCoolDownsConfig() {
        if (coolDowns == null || coolDownsFile == null) {
            return
        }
        try {
            getCoolDownsConfig()!!.save(coolDownsFile!!)
        } catch (ex: IOException) {
            logger.log(Level.SEVERE, "Could not save cooldowns to $coolDownsFile", ex)
        }
    }

    fun getCoolDownsConfig(): FileConfiguration? {
        if (coolDowns == null) {
            reloadCoolDownsConfig()
        }
        return coolDowns
    }


    // PLUGIN ENABLE

    override fun onEnable() {

        PDMBuilder(this).build().loadAllDependencies().join()

        loadConfig()


        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {

            logger.warning("Could not find PlaceholderAPI! This plugin is required")
            Bukkit.getPluginManager().disablePlugin(this)

        }

        val papi = RandomTeleportPlaceholders(this)
        papi.register()

        val cmdManager = CommandManager(this, true)
        cmdManager.completionHandler.register("#worlds") { Bukkit.getWorlds().map(World::getName) }
        cmdManager.register(CommandRandomTeleport(this))

        logger.info("Plugin enabled!")
    }


    // PLUGIN DISABLE

    override fun onDisable() {

        logger.info("Plugin disabled!")

    }

}