package kim.minecraft.citycore.economy.currency

import kim.minecraft.citycore.CityCore
import kim.minecraft.citycore.politics.country.Country
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*

object CurrencyManager {

    var currencies: MutableMap<UUID, Currency> = mutableMapOf()

    fun String.getCurrency(): Currency? {
        return currencies.values.firstOrNull { it.name == this }
    }

    fun UUID.toCurrency(): Currency {
        return currencies[this]!!
    }

    fun createCurrency(name: String, belongingsCountry: Country, symbol: Char): Currency {
        return Currency(name, belongingsCountry.uniqueID, symbol)
    }

    fun serialize(json: Json, folder: File) {
        currencies.also { CityCore.plugin.logger.info("[Currency]正保存 ${it.size} 个数据") }.forEach {
            File(folder, it.key.toString() + ".json").run {
                if (!this.exists()) createNewFile()
                this.writeText(json.encodeToString(it.value))
            }
        }
    }

    fun deserialize(json:Json,folder: File) {
        folder.listFiles { _, name -> name.endsWith(".json", true) }!!.also { CityCore.plugin.logger.info("[Currency]正加载 ${it.size} 个数据") }.iterator().forEach {
            json.decodeFromString<Currency>(it.readText())
        }
    }

}