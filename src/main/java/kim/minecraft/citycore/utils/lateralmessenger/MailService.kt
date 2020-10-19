package kim.minecraft.citycore.utils.lateralmessenger

import kim.minecraft.citycore.CityCore
import kim.minecraft.citycore.player.PlayerManager.toHumanRace
import kim.minecraft.citycore.utils.serialzation.UUIDAsStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@Serializable
class MailService(@Serializable(with = UUIDAsStringSerializer::class) val who: UUID, private val message: Array<out String>) {

    @Serializable(with = UUIDAsStringSerializer::class)
    val uniqueID: UUID = UUID.randomUUID()

    private val time = LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    @Transient
    private val action = object : Listener {
        @EventHandler
        fun onJoin() {
            if (!checkIsCurrentHumanRace() && !who.toHumanRace().alive) {
                destroy()
                return
            }
            sendMessage()
        }
    }

    private fun sendMessage() {
        who.toHumanRace().getPlayer().getBukkitPlayer().sendMessage("您于 $time 收到了以下的消息:")
        who.toHumanRace().getPlayer().getBukkitPlayer().sendMessage(message)
    }

    private fun checkIsCurrentHumanRace(): Boolean {
        return who.toHumanRace().getPlayer().currentHumanRace == who
    }

    private fun destroy() {
        removeTask()
        MailServiceManager.services.remove(this)
    }

    private fun check() {
        if (!checkIsCurrentHumanRace() && !who.toHumanRace().alive) {
            destroy()
            return
        }
        if (Bukkit.getPlayer(who.toHumanRace().player) != null) {
            sendMessage()
            destroy()
            return
        }

        addTask()
        MailServiceManager.services.add(this)
    }

    private fun addTask() {
        Bukkit.getPluginManager().registerEvents(action, CityCore.plugin)
    }

    private fun removeTask() {
        HandlerList.unregisterAll(action)
    }

    init {
        check()
    }
}