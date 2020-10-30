package kim.minecraft.citycore.politics.country.listener

import io.izzel.taboolib.module.inject.TListener
import kim.minecraft.citycore.player.PlayerManager.toCCPlayer
import kim.minecraft.citycore.player.PlayerManager.toHumanRace
import kim.minecraft.citycore.politics.country.CountryManager
import kim.minecraft.citycore.politics.country.CountryManager.toCountry
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent


@Suppress("unused")
@TListener
object AutoDisband:Listener {

    @EventHandler
    fun onDeath(e:PlayerDeathEvent){
        val human = e.entity.toCCPlayer()!!.currentHumanRace.toHumanRace()
        val country = human.currentCountry?.toCountry()!!
        if (country.owner == human&&country.members.size==1){
            human.currentCountry = null
            CountryManager.countries.remove(country.uniqueID)
        }
    }
}