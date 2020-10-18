package kim.minecraft.citycore.chunk

import kim.minecraft.citycore.chunk.ChunkManager.toCCChunk
import kim.minecraft.citycore.chunk.event.ChunkBelongingsChangeEvent
import kim.minecraft.citycore.politics.country.Country
import kim.minecraft.citycore.politics.country.CountryManager.toCountry
import kim.minecraft.citycore.utils.serialzation.UUIDAsStringSerializer
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Chunk
import java.util.*

@Serializable
data class Chunk(val chunkSearcher: ChunkSearcher, @Serializable(with = UUIDAsStringSerializer::class) private var belongingsCountry: UUID?, @Serializable(with = UUIDAsStringSerializer::class) private var tempBelongingsCountry: UUID?) {

    init {
        ChunkManager.chunks[chunkSearcher] = this
    }

    fun getBelongingsCountry(): Country? {
        return belongingsCountry?.toCountry()
    }

    fun getTempBelongingsCountry(): Country? {
        return tempBelongingsCountry?.toCountry()
    }

    fun setBelongingsCountry(value: Country?) {
        Bukkit.getPluginManager().callEvent(ChunkBelongingsChangeEvent(this, belongingsCountry?.toCountry(), value))
        belongingsCountry = value?.uniqueID
    }

    fun setTempBelongingsCountry(value: Country?) {
        tempBelongingsCountry = value?.uniqueID
    }

    fun hasOwner(): Boolean {
        return belongingsCountry != null
    }

    fun getBukkitChunk(): Chunk {
        return Bukkit.getWorld(chunkSearcher.world)!!.getChunkAt(chunkSearcher.x, chunkSearcher.z)
    }

    fun hasSuchCountryAround(country: Country): Boolean {
        return chunkSearcher.run {
            Bukkit.getWorld(world)!!.getChunkAt(x + 1, z).toCCChunk().belongingsCountry == country.uniqueID ||
                    Bukkit.getWorld(world)!!.getChunkAt(x, z + 1).toCCChunk().belongingsCountry == country.uniqueID ||
                    Bukkit.getWorld(world)!!.getChunkAt(x - 1, z).toCCChunk().belongingsCountry == country.uniqueID ||
                    Bukkit.getWorld(world)!!.getChunkAt(x, z - 1).toCCChunk().belongingsCountry == country.uniqueID
        }
    }

    override fun hashCode(): Int {
        return chunkSearcher.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return hashCode() == other.hashCode()
    }

    @Serializable
    data class ChunkSearcher(val world: String, val x: Int, val z: Int) {

        fun getChunk(): kim.minecraft.citycore.chunk.Chunk {
            return ChunkManager.chunks[this]!!
        }

        override fun toString(): String {
            return "[$world, $x, $z]"
        }
    }

}