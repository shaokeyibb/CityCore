package kim.minecraft.citycore.player.events

import kim.minecraft.citycore.events.CityCoreEvent
import kim.minecraft.citycore.player.HumanRace
import kim.minecraft.citycore.player.Player

class PlayerNextGenerationEvent(val who: Player, val origin: HumanRace, val next: HumanRace) : CityCoreEvent()