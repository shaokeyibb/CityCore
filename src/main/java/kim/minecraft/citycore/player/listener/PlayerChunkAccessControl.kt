package kim.minecraft.citycore.player.listener

import io.izzel.taboolib.module.inject.TListener
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

        if (e.player.world.getChunkAt(e.player.location).toCCChunk().getBelongingsCountry() == null ||
                e.player.world.getChunkAt(e.player.location).toCCChunk().getBelongingsCountry()?.uniqueID != e.player.toCCPlayer()!!.currentHumanRace.toHumanRace().currentCountry) {
            e.isCancelled = true
        }
        if (e.player.toCCPlayer()!!.currentHumanRace.toHumanRace().currentCountry!=null && e.player.world.getChunkAt(e.player.location).toCCChunk().getTempBelongingsCountry()?.uniqueID == e.player.toCCPlayer()!!.currentHumanRace.toHumanRace().currentCountry) {
            e.isCancelled = false
        }

        if (e.isCancelled) {
            e.player.sendMessage("§c您不能在非您国家的领土或野外进行破坏！")
        }
    }

    @EventHandler
    fun onPlace(e: BlockPlaceEvent) {
        if (e.player.world.getChunkAt(e.player.location).toCCChunk().getBelongingsCountry() == null ||
                e.player.world.getChunkAt(e.player.location).toCCChunk().getBelongingsCountry()?.uniqueID != e.player.toCCPlayer()?.currentHumanRace?.toHumanRace()?.currentCountry) {
            e.isCancelled = true
        }
        if (e.player.world.getChunkAt(e.player.location).toCCChunk().getTempBelongingsCountry()?.uniqueID == e.player.toCCPlayer()?.currentHumanRace?.toHumanRace()?.currentCountry) {
            e.isCancelled = false
        }

        if (e.isCancelled) {
            e.player.sendMessage("§c您不能在非您国家的领土或野外进行破坏！")
        }
    }

}