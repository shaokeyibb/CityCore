package kim.minecraft.citycore.utils.lateralmessenger

import io.izzel.taboolib.module.inject.TListener
import kim.minecraft.citycore.CityCore
import kim.minecraft.citycore.player.HumanRace
import kim.minecraft.citycore.player.PlayerManager.toCCPlayer
import kim.minecraft.citycore.player.PlayerManager.toHumanRace
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.OfflinePlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.io.File

@TListener
object MailServiceManager : Listener {

    val services: MutableList<MailService> = mutableListOf()

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        services.filter { it.checkIsCurrentHumanRace() && it.who.toHumanRace().alive }.forEach {
            it.sendMessage()
            it.destroy()
        }
    }

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
        return getTasks(this)
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