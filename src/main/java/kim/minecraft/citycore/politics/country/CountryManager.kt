package kim.minecraft.citycore.politics.country

import kim.minecraft.citycore.CityCore
import kim.minecraft.citycore.chunk.Chunk
import kim.minecraft.citycore.economy.currency.Currency
import kim.minecraft.citycore.economy.currency.CurrencyManager
import kim.minecraft.citycore.economy.wallet.Wallet
import kim.minecraft.citycore.economy.wallet.WalletManager
import kim.minecraft.citycore.player.HumanRace
import kim.minecraft.citycore.politics.country.tags.CountryOwner
import kim.minecraft.citycore.politics.party.Party
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*

object CountryManager {

    var countries: MutableMap<UUID, Country> = mutableMapOf()

    fun String.getCountry(): Country? {
        return countries.values.firstOrNull { this == it.name }
    }

    fun UUID.toCountry(): Country {
        return countries[this]!!
    }

    private fun createCountry(name: String, ideology: String, owner: CountryOwner, chunks: MutableList<Chunk.ChunkSearcher>, currency: Currency?, parties: MutableList<UUID>, members: MutableList<UUID>, wallet: Wallet): Country {
        return Country(name, ideology, owner, chunks, parties, currency, members, wallet).also { country ->
            val temp = country.owner
            if (temp is HumanRace) {
                temp.currentCountry = country.uniqueID
                members.add(temp.uniqueID)
            } else if (temp is Party) {
                temp.belongingsCountry = country.uniqueID
                members.add(temp.ownerHumanRace)
            }
            chunks.forEach { it.getChunk().setBelongingsCountry(country) }
        }
    }

    fun createCountry(name: String, ideology: String, owner: HumanRace, firstChunk: Chunk, currencyName: String?, currencySymbol: Char?): Country {
        return if (currencyName == null || currencySymbol == null) {
            createCountry(name, ideology, owner, mutableListOf(firstChunk.chunkSearcher), null, mutableListOf(), mutableListOf(), WalletManager.createWallet())
        } else {
            createCountry(name, ideology, owner, mutableListOf(firstChunk.chunkSearcher), null, mutableListOf(), mutableListOf(), WalletManager.createWallet()).apply { this.currency = CurrencyManager.createCurrency(currencyName, this, currencySymbol) }
        }
    }

    fun createCountry(name: String, ideology: String, owner: Party, firstChunk: Chunk, currencyName: String?, currencySymbol: Char?): Country {
        return if (currencyName == null || currencySymbol == null) {
            createCountry(name, ideology, owner, mutableListOf(firstChunk.chunkSearcher), null, mutableListOf(owner.ownerHumanRace), mutableListOf(), WalletManager.createWallet())
        } else {
            createCountry(name, ideology, owner, mutableListOf(firstChunk.chunkSearcher), null, mutableListOf(owner.ownerHumanRace), mutableListOf(), WalletManager.createWallet()).apply { this.currency = CurrencyManager.createCurrency(currencyName, this, currencySymbol) }
        }
    }

    fun serialize(json: Json, folder: File) {
        countries.also { CityCore.plugin.logger.info("[Country]正保存 ${it.size} 个数据") }.forEach {
            File(folder, it.key.toString() + ".json").run {
                if (!this.exists()) createNewFile()
                this.writeText(json.encodeToString(it.value))
            }
        }
    }

    fun deserialize(json:Json,folder: File) {
        folder.listFiles { _, name -> name.endsWith(".json", true) }!!.also { CityCore.plugin.logger.info("[Country]正加载 ${it.size} 个数据") }.iterator().forEach {
            json.decodeFromString<Country>(it.readText())
        }
    }

}