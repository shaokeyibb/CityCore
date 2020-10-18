package kim.minecraft.citycore.chunk.tasks

import kim.minecraft.citycore.CityCore
import kim.minecraft.citycore.chunk.Chunk
import kim.minecraft.citycore.player.Player
import kim.minecraft.citycore.player.PlayerManager.toHumanRace
import kim.minecraft.citycore.politics.country.Country
import kim.minecraft.citycore.utils.storage.SettingsStorage
import me.arasple.mc.trhologram.api.TrHologramAPI
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

class CountryClaimChunkTask(val player: Player, val country: Country, private val chunk: Chunk) {
    private val timeTicks: Long = SettingsStorage.settings.getLong("CountryClaimChunkTicks", 12000)
    private val startTime: Long = System.currentTimeMillis()
    private val hologram = TrHologramAPI.createHologram(
            CityCore.plugin,
            chunk.chunkSearcher.toString(),
            player.getBukkitPlayer().location.clone().add(0.0, 2.0, 0.0),
            "地块 ${chunk.chunkSearcher} 正被占领中"
    )

    private val update = object : BukkitRunnable() {
        override fun run() {
            hologram.updateLines(listOf("地块 ${chunk.chunkSearcher} 正被占领中",
                    "占领者: ${player.currentHumanRace.toHumanRace().name}",
                    "占领国家: ${country.name}",
                    "占领剩余时间: ${getRemainTimeSeconds()} 秒"))
        }
    }

    private val internalTask: BukkitRunnable = object : BukkitRunnable() {
        override fun run() {
            TaskManager.countryClaimChunkTasks.remove(chunk.chunkSearcher)
            chunk.setTempBelongingsCountry(null)
            chunk.getBelongingsCountry()?.chunks?.remove(chunk.chunkSearcher)
            country.chunks.add(chunk.chunkSearcher)
            chunk.setBelongingsCountry(country)
            Bukkit.broadcastMessage("国家 ${country.name} 成功占领了位于 ${chunk.chunkSearcher} 的一块地块")
            update.cancel()
            hologram.delete()
        }

    }

    init {
        TaskManager.countryClaimChunkTasks[chunk.chunkSearcher] = this
        runTask()
    }

    private fun runTask() {
        chunk.setTempBelongingsCountry(country)
        internalTask.runTaskLater(CityCore.plugin, timeTicks)
        update.runTaskTimer(CityCore.plugin, 0, 20)
    }

    fun cancelTask() {
        if (internalTask.isCancelled) return
        internalTask.cancel()
        hologram.delete()
    }

    private fun getEndTimeMillis(): Long {
        return startTime + (timeTicks / 20) * 1000
    }

    fun getRemainTimeSeconds(): Long {
        return (getEndTimeMillis() - System.currentTimeMillis()) / 1000
    }
}