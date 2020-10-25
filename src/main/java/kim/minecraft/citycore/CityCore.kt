package kim.minecraft.citycore

import io.izzel.taboolib.loader.Plugin
import kim.minecraft.citycore.features.BlinkMe
import kim.minecraft.citycore.features.CustomRecipes
import kim.minecraft.citycore.hooks.bluemap.BlueMapManager
import kim.minecraft.citycore.utils.request.RequestManager
import kim.minecraft.citycore.utils.storage.DataStorage
import kim.minecraft.citycore.utils.storage.SettingsStorage
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
        BlueMapManager.runRefresher()
    }

    override fun onDisable() {
        Bukkit.getOnlinePlayers().forEach { it.kickPlayer("服务器已关闭") }
        RequestManager.finalize()
        DataStorage.serializeAll()
    }

    private fun registerListener() {
        if (SettingsStorage.settings.getBoolean("HumanRaceEyesBlindEnable"))
            Bukkit.getPluginManager().registerEvents(BlinkMe, this.plugin)
    }

    private fun initGameEnvironment() {
        Bukkit.getWorlds().forEach {
            it.setGameRule(GameRule.KEEP_INVENTORY, true)
        }
    }
}