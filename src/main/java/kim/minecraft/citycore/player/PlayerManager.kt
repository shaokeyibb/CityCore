package kim.minecraft.citycore.player

import kim.minecraft.citycore.CityCore
import kim.minecraft.citycore.economy.wallet.Wallet
import kim.minecraft.citycore.economy.wallet.WalletManager
import kim.minecraft.citycore.politics.country.Country
import kim.minecraft.citycore.politics.party.Party
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.io.File
import java.util.*

object PlayerManager {

    var players: MutableMap<UUID, Player> = mutableMapOf()
    var humanRaces: MutableMap<UUID, HumanRace> = mutableMapOf()

    fun String.getHumanRace(): HumanRace? {
        return humanRaces.values.firstOrNull { this == it.name }
    }

    fun org.bukkit.entity.Player.toCCPlayer(): Player? {
        return players[uniqueId]
    }

    fun UUID.toOfflinePlayer(): OfflinePlayer {
        return Bukkit.getOfflinePlayer(this)
    }

    fun UUID.toCCPlayer(): Player {
        return players[this]!!
    }

    fun UUID.toHumanRace(): HumanRace {
        return humanRaces[this]!!
    }

    fun createHumanRace(player: org.bukkit.entity.Player, name: String, currentCountry: Country?, currentParty: Party?, wallet: Wallet, permissions: MutableList<String>): HumanRace {
        return HumanRace(player.uniqueId, name, currentCountry?.uniqueID, currentParty?.uniqueID, true, wallet, permissions)
    }

    fun createHumanRace(player: org.bukkit.entity.Player, name: String): HumanRace {
        return createHumanRace(player, name, null, null, WalletManager.createWallet(), mutableListOf())
    }

    fun createPlayer(player: org.bukkit.entity.Player, humanRace: HumanRace): Player {
        return Player(player.uniqueId, humanRace.uniqueID)
    }

    fun createPlayer(player: org.bukkit.entity.Player, name: String): Player {
        return createPlayer(player, createHumanRace(player, name))
    }

    fun serializeHumanRace(json: Json, folder: File) {
        humanRaces.also { CityCore.plugin.logger.info("[HumanRace]正保存 ${it.size} 个数据") }.forEach {
            File(folder, it.key.toString() + ".json").run {
                if (!this.exists()) createNewFile()
                this.writeText(json.encodeToString(it.value))
            }
        }
    }

    fun deserializeHumanRace(json: Json, folder: File) {
        folder.listFiles { _, name -> name.endsWith(".json", true) }!!.also { CityCore.plugin.logger.info("[HumanRace]正加载 ${it.size} 个数据") }.iterator().forEach {
            json.decodeFromString<HumanRace>(it.readText())
        }
    }

    fun serializePlayer(json: Json, folder: File) {
        players.also { CityCore.plugin.logger.info("[Player]正保存 ${it.size} 个数据") }.forEach {
            File(folder, it.key.toString() + ".json").run {
                if (!this.exists()) createNewFile()
                this.writeText(json.encodeToString(it.value))
            }
        }
    }

    fun deserializePlayer(json: Json, folder: File) {
        folder.listFiles { _, name -> name.endsWith(".json", true) }!!.also { CityCore.plugin.logger.info("[Player]正加载 ${it.size} 个数据") }.iterator().forEach {
            json.decodeFromString<Player>(it.readText())
        }
    }
}