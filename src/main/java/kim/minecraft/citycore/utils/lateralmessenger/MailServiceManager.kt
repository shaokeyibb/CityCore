package kim.minecraft.citycore.utils.lateralmessenger

import kim.minecraft.citycore.CityCore
import kim.minecraft.citycore.player.HumanRace
import kim.minecraft.citycore.player.Player
import kim.minecraft.citycore.player.PlayerManager.toCCPlayer
import kim.minecraft.citycore.player.PlayerManager.toHumanRace
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.OfflinePlayer
import java.io.File

object MailServiceManager {

    val services: MutableList<MailService> = mutableListOf()

    private fun createTask(player: HumanRace, message: Array<out String>) {
        MailService(player.uniqueID, message)
    }

    private fun getTasks(player: HumanRace): List<MailService> {
        return services.filter { it.who == player.uniqueID }
    }

    fun OfflinePlayer.mailTo(message: Array<out String>) {
        createTask(uniqueId.toCCPlayer().currentHumanRace.toHumanRace(), message)
    }

    fun HumanRace.getAvailableTasks(): List<MailService> {
        return MailServiceManager.getTasks(this)
    }

    fun serialize(json: Json, folder: File) {
        folder.deleteRecursively()
        folder.mkdir()
        services.also { CityCore.plugin.logger.info("[MailService]正保存 ${it.size} 个数据") }.forEach {
            File(folder, it.uniqueID.toString() + ".json").run {
                if (!this.exists()) createNewFile()
                this.writeText(json.encodeToString(it))
            }
        }
    }

    fun deserialize(json: Json, folder: File) {
        folder.listFiles { _, name -> name.endsWith(".json", true) }!!.also { CityCore.plugin.logger.info("[MailService]正加载 ${it.size} 个数据") }.iterator().forEach {
            json.decodeFromString<MailService>(it.readText())
        }
    }

}