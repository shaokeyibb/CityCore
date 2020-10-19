package kim.minecraft.citycore.features

import io.izzel.taboolib.internal.xseries.messages.ActionBar
import kim.minecraft.citycore.CityCore
import kim.minecraft.citycore.player.Player
import kim.minecraft.citycore.player.PlayerManager.toCCPlayer
import kim.minecraft.citycore.utils.HologramTags
import kim.minecraft.citycore.utils.storage.SettingsStorage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import kotlin.random.Random

object BlinkMe : Listener {

    private val list = mutableMapOf<Player, Action>()

    @EventHandler
    fun onRegister(e: PlayerJoinEvent) {
        if (e.player.toCCPlayer() == null) return
        list[e.player.toCCPlayer()!!] = Action(e.player.toCCPlayer()!!)
    }

    @EventHandler
    fun onUnregister(e: PlayerQuitEvent) {
        if (e.player.toCCPlayer() == null) return
        list.remove(e.player.toCCPlayer()!!)!!.also { it.unregister() }
    }

    class Action(val player: Player) {

        val blindTime = Random.nextLong(SettingsStorage.settings.getLong("HumanRaceEyesBlindMinDelay", 40), SettingsStorage.settings.getLong("HumanRaceEyesBlindMaxDelay", 120))
        val refreshTime = SettingsStorage.settings.getLong("HumanRaceEyesBlindRefreshTimeTicks", 5)

        fun unregister() {
            action.cancel()
        }

        private val action = object : BukkitRunnable() {
            override fun run() {
                val time = Random.nextInt(SettingsStorage.settings.getInt("HumanRaceEyesBlindMinDuration", 24), SettingsStorage.settings.getInt("HumanRaceEyesBlindMaxDuration", 28))
                player.getBukkitPlayer().addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, time, 255, false, false, false))
                player.getBukkitPlayer().addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, time, 255, false, false, false))
                HologramTags.addTags(player, "!眨眼!")
                object : BukkitRunnable() {
                    override fun run() {
                        HologramTags.removeTag(player, "!眨眼!")
                    }
                }.runTaskLater(CityCore.plugin, time.toLong())
                object : BukkitRunnable() {
                    var now = blindTime.toInt()
                    override fun run() {
                        try {
                            var i = 0
                            val string = StringBuilder()
                            while (i <= now) {
                                if ((i % refreshTime.toInt()) == 0) string.append("█")
                                i++
                            }
                            ActionBar.sendActionBar(player.getBukkitPlayer(), string.toString())
                            now--
                            if (now == 0) cancel()
                        } catch (ignored: NullPointerException) {
                            cancel()
                        }
                    }
                }.runTaskTimerAsynchronously(CityCore.plugin, 0, 1)
            }
        }

        init {
            action.runTaskTimer(CityCore.plugin, 20, blindTime)
        }
    }
}