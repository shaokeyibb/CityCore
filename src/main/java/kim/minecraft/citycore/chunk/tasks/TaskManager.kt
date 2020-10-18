package kim.minecraft.citycore.chunk.tasks

import kim.minecraft.citycore.chunk.Chunk

object TaskManager {

    val countryClaimChunkTasks: MutableMap<Chunk.ChunkSearcher, CountryClaimChunkTask> = mutableMapOf()

}