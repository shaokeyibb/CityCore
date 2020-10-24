package kim.minecraft.citycore.player.listener

import io.izzel.taboolib.module.inject.TListener
import kim.minecraft.citycore.player.PlayerManager.toCCPlayer
import kim.minecraft.citycore.player.PlayerManager.toHumanRace
import kim.minecraft.citycore.player.tasks.HumanRaceRespawnTask
import kim.minecraft.citycore.politics.country.CountryManager.toCountry
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent

@Suppress("unused")
@TListener
object DeathOfTheHumanRace : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onDeath(e: PlayerDeathEvent) {
        //Death Message Handler
        e.deathMessage = e.deathMessage?.replace(e.entity.name, e.entity.toCCPlayer()!!.currentHumanRace.toHumanRace().name, false)
        e.entity.spigot().respawn()
        if (e.entity.toCCPlayer()!!.currentHumanRace.toHumanRace().currentCountry?.toCountry()?.owner == e.entity.toCCPlayer()!!.currentHumanRace.toHumanRace()) {
            e.deathMessage = "悲报: 国家 ${e.entity.toCCPlayer()!!.currentHumanRace.toHumanRace().currentCountry?.toCountry()?.name} 元首 ${e.entity.toCCPlayer()!!.currentHumanRace.toHumanRace().name} 与世长辞了！"
            return
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onRespawn(e: PlayerRespawnEvent) {
        //HumanRace Initial
        val player = e.player.toCCPlayer()!!
        player.currentHumanRace.toHumanRace().alive = false
        HumanRaceRespawnTask(player)
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onJoin(e: PlayerJoinEvent) {
        if (e.player.toCCPlayer() != null && !e.player.toCCPlayer()!!.currentHumanRace.toHumanRace().alive) {
            e.player.sendMessage("请先完成角色初始化再继续游戏")
            HumanRaceRespawnTask(e.player.toCCPlayer()!!)
        }

    }
}