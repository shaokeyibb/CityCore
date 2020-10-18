package kim.minecraft.citycore.utils.storage

import kim.minecraft.citycore.CityCore
import kim.minecraft.citycore.chunk.ChunkManager
import kim.minecraft.citycore.company.tags.CompanyOwner
import kim.minecraft.citycore.economy.currency.CurrencyManager
import kim.minecraft.citycore.player.HumanRace
import kim.minecraft.citycore.player.PlayerManager
import kim.minecraft.citycore.politics.country.Country
import kim.minecraft.citycore.politics.country.CountryManager
import kim.minecraft.citycore.politics.country.tags.CountryOwner
import kim.minecraft.citycore.politics.party.Party
import kim.minecraft.citycore.politics.party.PartyManager
import kim.minecraft.citycore.utils.lateralmessenger.MailService
import kim.minecraft.citycore.utils.lateralmessenger.MailServiceManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.bukkit.plugin.Plugin
import java.io.File

object DataStorage {
    private val plugin: Plugin = CityCore.plugin

    private val json = Json {
        allowStructuredMapKeys = true
        serializersModule = SerializersModule {
            polymorphic(CountryOwner::class) {
                subclass(HumanRace::class)
                subclass(Party::class)
            }
            polymorphic(CompanyOwner::class) {
                subclass(HumanRace::class)
                subclass(Party::class)
                subclass(Country::class)
            }
        }
    }

    fun serializeAll() {
        ChunkManager.serialize(json, File(plugin.dataFolder, "data${File.separator}chunks"))
        CurrencyManager.serialize(json, File(plugin.dataFolder, "data${File.separator}currencies"))
        PlayerManager.serializeHumanRace(json, File(plugin.dataFolder, "data${File.separator}humanraces"))
        PlayerManager.serializePlayer(json, File(plugin.dataFolder, "data${File.separator}players"))
        CountryManager.serialize(json, File(plugin.dataFolder, "data${File.separator}countries"))
        PartyManager.serialize(json, File(plugin.dataFolder, "data${File.separator}parties"))
        MailServiceManager.serialize(json, File(plugin.dataFolder, "data${File.separator}mails"))
    }

    fun deserializeAll() {
        ChunkManager.deserialize(json, File(plugin.dataFolder, "data${File.separator}chunks").also { if (!it.exists()) it.mkdirs() })
        CurrencyManager.deserialize(json, File(plugin.dataFolder, "data${File.separator}currencies").also { if (!it.exists()) it.mkdirs() })
        PlayerManager.deserializeHumanRace(json, File(plugin.dataFolder, "data${File.separator}humanraces").also { if (!it.exists()) it.mkdirs() })
        PlayerManager.deserializePlayer(json, File(plugin.dataFolder, "data${File.separator}players").also { if (!it.exists()) it.mkdirs() })
        CountryManager.deserialize(json, File(plugin.dataFolder, "data${File.separator}countries").also { if (!it.exists()) it.mkdirs() })
        PartyManager.deserialize(json, File(plugin.dataFolder, "data${File.separator}parties").also { if (!it.exists()) it.mkdirs() })
        MailServiceManager.deserialize(json, File(plugin.dataFolder, "data${File.separator}mails").also { if (!it.exists()) it.mkdirs() })
    }
}