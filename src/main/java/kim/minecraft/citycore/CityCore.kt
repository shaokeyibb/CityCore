package kim.minecraft.citycore

import io.izzel.taboolib.loader.Plugin
import kim.minecraft.citycore.features.CustomRecipes
import kim.minecraft.citycore.utils.storage.DataStorage
import org.bukkit.Bukkit
import org.bukkit.GameRule

@Suppress("unused")
object CityCore : Plugin() {

    override fun onEnable() {
        if (plugin.getResource("config.yml") == null)
            plugin.saveResource("config.yml", false)
        DataStorage.deserializeAll()
        initGameEnvironment()
        registerListener()
        CustomRecipes
    }

    override fun onDisable() {
        DataStorage.serializeAll()
    }

    private fun registerListener() {

    }

    private fun initGameEnvironment(){
        Bukkit.getWorlds().forEach {
            it.setGameRule(GameRule.KEEP_INVENTORY, true)
        }
    }
}