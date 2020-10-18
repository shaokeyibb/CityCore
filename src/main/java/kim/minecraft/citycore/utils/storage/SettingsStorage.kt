package kim.minecraft.citycore.utils.storage

import io.izzel.taboolib.module.config.TConfig
import kim.minecraft.citycore.CityCore

object SettingsStorage {

    val settings: TConfig = TConfig.create(CityCore.plugin, "config.yml")

}