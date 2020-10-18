package kim.minecraft.citycore.politics.country.listener

import io.izzel.taboolib.module.inject.TListener
import kim.minecraft.citycore.player.HumanRace
import kim.minecraft.citycore.politics.country.event.CountryCreateEvent
import kim.minecraft.citycore.politics.party.Party
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

@Suppress("unused")
@TListener
object Announcer : Listener {

    @EventHandler
    fun onCreate(e: CountryCreateEvent) {
        Bukkit.broadcastMessage("号外！号外！一个由 ${e.country.owner.run { if (this is HumanRace) this.name else (this as Party).name }} 创建的名为 ${e.country.name} 的国家在 ${e.country.chunks[0]} 拔地而起！让我们一起祝贺这个新生国家的建立！")
    }
}