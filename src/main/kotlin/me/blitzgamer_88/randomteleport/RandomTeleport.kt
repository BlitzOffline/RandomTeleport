package me.blitzgamer_88.randomteleport

import ch.jalu.configme.SettingsManager
import io.papermc.lib.PaperLib
import me.blitzgamer_88.randomteleport.cmd.CommandRandomTeleport
import me.blitzgamer_88.randomteleport.conf.RandomTeleportConfiguration
import me.bristermitten.pdm.PDMBuilder
import me.mattstudios.mf.base.CommandManager
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin


class RandomTeleport : JavaPlugin() {

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

    override fun onEnable() {

        PDMBuilder(this).build().loadAllDependencies().join()

        loadConfig()


        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {

            logger.warning("Could not find PlaceholderAPI! This plugin is required")
            Bukkit.getPluginManager().disablePlugin(this)

        }

        val cmdManager = CommandManager(this, true)
        cmdManager.completionHandler.register("#worlds") { Bukkit.getWorlds().map(World::getName) }
        cmdManager.register(CommandRandomTeleport(this))

        logger.info("Plugin enabled!")
    }



    override fun onDisable() {

        logger.info("Plugin disabled!")

    }

}