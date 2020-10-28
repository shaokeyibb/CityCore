package kim.minecraft.citycore.features

import io.izzel.taboolib.module.inject.TListener
import kim.minecraft.citycore.utils.storage.SettingsStorage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import kotlin.math.sqrt

@Suppress("unused")
@TListener
object RadiusChat : Listener {

    private val radius = SettingsStorage.settings.getDouble("ChatRadius", 50.0)

    @EventHandler
    fun onChat(e: AsyncPlayerChatEvent) {
        e.recipients.removeIf { it.velocity.distanceSquared(e.player.velocity) > sqrt(radius) }
    }

}