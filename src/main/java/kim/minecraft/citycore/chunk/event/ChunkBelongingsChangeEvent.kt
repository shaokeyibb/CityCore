package kim.minecraft.citycore.chunk.event

import kim.minecraft.citycore.events.CityCoreEvent
import kim.minecraft.citycore.chunk.Chunk
import kim.minecraft.citycore.politics.country.Country

class ChunkBelongingsChangeEvent(val chunk: Chunk, val from: Country?, var to: Country?) : CityCoreEvent()