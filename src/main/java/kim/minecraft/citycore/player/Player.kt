package kim.minecraft.citycore.player

import kim.minecraft.citycore.chunk.Chunk
import kim.minecraft.citycore.chunk.ChunkManager.toCCChunk
import kim.minecraft.citycore.player.PlayerManager.toHumanRace
import kim.minecraft.citycore.player.events.PlayerNextGenerationEvent
import kim.minecraft.citycore.utils.serialzation.UUIDAsStringSerializer
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

@Serializable
data class Player(@Serializable(with = UUIDAsStringSerializer::class) val playerUUID: UUID, @Serializable(with = UUIDAsStringSerializer::class) var currentHumanRace: UUID) {

    init {
        PlayerManager.players[playerUUID] = this
    }

    fun getBukkitPlayer(): Player {
        return Bukkit.getPlayer(playerUUID)!!
    }

    fun getChunkAtPlace(): Chunk {
        return getBukkitPlayer().world.getChunkAt(getBukkitPlayer().location).toCCChunk()
    }

    fun nextGeneration(new: HumanRace) {
        Bukkit.getPluginManager().callEvent(PlayerNextGenerationEvent(this, currentHumanRace.toHumanRace(), new))
        currentHumanRace.toHumanRace().alive = false
        currentHumanRace = new.uniqueID
        getBukkitPlayer().setDisplayName(new.name)
        getBukkitPlayer().setPlayerListName(new.name)
    }

    override fun hashCode(): Int {
        return playerUUID.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return hashCode() == other.hashCode()
    }
}