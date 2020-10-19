package kim.minecraft.citycore.utils

import io.izzel.taboolib.module.inject.TListener
import kim.minecraft.citycore.CityCore
import kim.minecraft.citycore.player.Player
import kim.minecraft.citycore.utils.storage.SettingsStorage
import me.arasple.mc.trhologram.api.TrHologramAPI
import me.arasple.mc.trhologram.hologram.Hologram
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.*
import kotlin.collections.ArrayList

@Suppress("unused")
@TListener
object HologramTags : Listener {

    private val tags = mutableMapOf<UUID, MutableList<String>>()

    private val holograms = mutableMapOf<UUID, Hologram>()

    private val refresher = mutableMapOf<UUID, BukkitTask>()

    private fun getTags(uuid: UUID): List<String> {
        return tags[uuid] ?: listOf()
    }

    private fun getTagsFixed(player: org.bukkit.entity.Player): String {
        return StringBuilder().run {
            ArrayList(getTags(player.uniqueId)).forEach {
                append("$it ")
            }
            toString()
        }
    }

    fun addTags(player: Player, tag: String) {
        if (tags[player.playerUUID] == null) resetTag(player)
        tags[player.playerUUID]!!.add(tag)
    }

    fun removeTag(player: Player, tag: String) {
        tags[player.playerUUID]?.remove(tag)
    }

    private fun resetTag(player: Player) {
        tags[player.playerUUID] = mutableListOf()
    }

    private fun createHologram(player: org.bukkit.entity.Player): Hologram {
        return TrHologramAPI.createHologram(CityCore.plugin,
                "tags_${player.uniqueId}",
                player.location.clone().add(0.0, 0.7, 0.0),
                getTagsFixed(player)).also { holograms[player.uniqueId] = it }
    }

    private fun createRefresher(player: org.bukkit.entity.Player): BukkitTask {
        return object : BukkitRunnable() {
            override fun run() {
                if (getTags(player.uniqueId).isNotEmpty() && holograms[player.uniqueId] == null) {
                    holograms[player.uniqueId] = createHologram(player)
                } else if (getTags(player.uniqueId).isEmpty() && holograms[player.uniqueId] != null) {
                    holograms.remove(player.uniqueId)!!.delete()
                }
                holograms[player.uniqueId]?.updateLines(listOf(getTagsFixed(player)))
                holograms[player.uniqueId]?.updateLocation(player.location.clone().add(0.0, 0.7, 0.0))
            }
        }.runTaskTimerAsynchronously(CityCore.plugin, 0, SettingsStorage.settings.getLong("PlayerTagsRefreshTicks", 1)).also {
            refresher[player.uniqueId] = it
        }
    }

    private fun removeRefresher(player: org.bukkit.entity.Player) {
        refresher.remove(player.uniqueId)!!.cancel()
        holograms.remove(player.uniqueId)?.delete()
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        createRefresher(e.player)
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        removeRefresher(e.player)
    }
}