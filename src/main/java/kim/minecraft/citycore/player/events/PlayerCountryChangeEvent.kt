package kim.minecraft.citycore.player.events

import kim.minecraft.citycore.events.CityCoreEvent
import kim.minecraft.citycore.player.Player
import kim.minecraft.citycore.politics.country.Country

class PlayerCountryChangeEvent(val who: Player, val from: Country, var to: Country): CityCoreEvent()