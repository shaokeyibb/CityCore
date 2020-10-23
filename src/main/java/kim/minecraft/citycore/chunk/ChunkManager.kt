package kim.minecraft.citycore.chunk

import com.flowpowered.math.vector.Vector2d
import de.bluecolored.bluemap.api.marker.Shape
import kim.minecraft.citycore.CityCore
import kim.minecraft.citycore.chunk.ChunkManager.toCCChunk
import kim.minecraft.citycore.hooks.bluemap.BlueMapManager
import kim.minecraft.citycore.politics.country.Country
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object ChunkManager {

    var chunks: MutableMap<Chunk.ChunkSearcher, Chunk> = mutableMapOf()

    fun org.bukkit.Chunk.toCCChunk(): Chunk {
        val temp = Chunk.ChunkSearcher(this.world.name, this.x, this.z)
        return if (!chunks.contains(temp)) createChunk(this.world.name, this.x, this.z, null, null)
        else chunks[temp]!!
    }

    fun org.bukkit.Chunk.hasCCChunk(): Boolean {
        return chunks.contains(Chunk.ChunkSearcher(this.world.name, this.x, this.z))
    }

    fun createChunk(world: String, x: Int, z: Int, belongingsCountry: Country?, tempBelongingsCountry: Country?): Chunk {
        val temp = Chunk.ChunkSearcher(world, x, z)
        if (chunks.containsKey(Chunk.ChunkSearcher(world, x, z))) throw Exception("The chunk you need to create ($temp) is already exists")
        return Chunk(temp, belongingsCountry?.uniqueID, tempBelongingsCountry?.uniqueID)
    }

    fun serialize(json: Json, folder: File) {
        chunks.also { CityCore.plugin.logger.info("[Chunk]正保存 ${it.size} 个数据") }.forEach {
            File(folder, it.key.toString() + ".json").run {
                if (!this.exists()) createNewFile()
                this.writeText(json.encodeToString(it.value))
            }
        }
    }

    fun deserialize(json: Json, folder: File) {
        folder.listFiles { _, name -> name.endsWith(".json", true) }!!.also { CityCore.plugin.logger.info("[Chunk]正加载 ${it.size} 个数据") }.iterator().forEach {
            json.decodeFromString<Chunk>(it.readText())
        }
    }

}