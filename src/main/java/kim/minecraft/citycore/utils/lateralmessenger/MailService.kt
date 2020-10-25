package kim.minecraft.citycore.utils.lateralmessenger

import kim.minecraft.citycore.CityCore
import kim.minecraft.citycore.player.PlayerManager.toHumanRace
import kim.minecraft.citycore.utils.serialzation.UUIDAsStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Serializable
class MailService(@Serializable(with = UUIDAsStringSerializer::class) val who: UUID, private val message: Array<out String>) {

    @Serializable(with = UUIDAsStringSerializer::class)
    val uniqueID: UUID = UUID.randomUUID()

    private val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    fun sendMessage() {
        who.toHumanRace().getPlayer().getBukkitPlayer().sendMessage("您于 $time 收到了以下的消息:")
        who.toHumanRace().getPlayer().getBukkitPlayer().sendMessage(message)
    }

    fun checkIsCurrentHumanRace(): Boolean {
        return who.toHumanRace().getPlayer().currentHumanRace == who
    }

    fun destroy() {
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

        MailServiceManager.services.add(this)
    }

    init {
        check()
    }
}