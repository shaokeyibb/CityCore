package kim.minecraft.citycore.player.listener

import io.izzel.taboolib.module.inject.TListener
import kim.minecraft.citycore.chunk.ChunkManager.hasCCChunk
import kim.minecraft.citycore.chunk.ChunkManager.toCCChunk
import kim.minecraft.citycore.player.PlayerManager.toCCPlayer
import kim.minecraft.citycore.player.PlayerManager.toHumanRace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

@Suppress("unused")
@TListener
object PlayerChunkAccessControl : Listener {

    @EventHandler
    fun onBreak(e: BlockBreakEvent) {
        if (e.player.toCCPlayer() == null) return

        if (!e.player.location.chunk.hasCCChunk() || !e.player.location.chunk.toCCChunk().hasOwner()) {
            e.isCancelled = true
        }

        if (e.isCancelled) {
            e.player.sendMessage("§c您不能在未殖民地区进行破坏！")
        }
    }

    @EventHandler
    fun onPlace(e: BlockPlaceEvent) {
        if (e.player.toCCPlayer() == null) return

        if (!e.player.location.chunk.hasCCChunk() || !e.player.location.chunk.toCCChunk().hasOwner()) {
            e.isCancelled = true
        }

        if (e.isCancelled) {
            e.player.sendMessage("§c您不能在未殖民地区进行破坏！")
        }
    }

}