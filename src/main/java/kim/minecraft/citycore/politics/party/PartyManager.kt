package kim.minecraft.citycore.politics.party

import kim.minecraft.citycore.CityCore
import kim.minecraft.citycore.politics.country.Country
import kim.minecraft.citycore.politics.country.CountryManager
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*

object PartyManager {

    var parties: MutableMap<UUID, Party> = mutableMapOf()

    fun UUID.toParty(): Party {
        return parties[this]!!
    }

    fun serialize(json: Json, folder: File) {
        parties.also { CityCore.plugin.logger.info("[Party]正保存 ${it.size} 个数据") }.forEach {
            File(folder, it.key.toString() + ".json").run {
                if (!this.exists()) createNewFile()
                this.writeText(json.encodeToString(it.value))
            }
        }
    }

    fun deserialize(json:Json,folder: File) {
        folder.listFiles { _, name -> name.endsWith(".json", true) }!!.also { CityCore.plugin.logger.info("[Party]正加载 ${it.size} 个数据") }.iterator().forEach {
            json.decodeFromString<Party>(it.readText())
        }
    }
}