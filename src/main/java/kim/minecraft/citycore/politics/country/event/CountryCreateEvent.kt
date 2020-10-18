package kim.minecraft.citycore.politics.country.event

import kim.minecraft.citycore.events.CityCoreEvent
import kim.minecraft.citycore.politics.country.Country
import kim.minecraft.citycore.politics.country.tags.CountryOwner

class CountryCreateEvent(val who: CountryOwner, val country: Country) : CityCoreEvent()