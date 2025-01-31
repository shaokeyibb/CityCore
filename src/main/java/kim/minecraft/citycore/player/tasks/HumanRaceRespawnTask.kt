package kim.minecraft.citycore.player.tasks

import io.izzel.taboolib.common.event.PlayerAttackEvent
import io.izzel.taboolib.util.Features
import kim.minecraft.citycore.CityCore
import kim.minecraft.citycore.player.Player
import kim.minecraft.citycore.player.PlayerManager
import kim.minecraft.citycore.player.PlayerManager.getHumanRace
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

class HumanRaceRespawnTask(val player: Player) {

    private val input: Features.ChatInput = object : Features.ChatInput {
        override fun onChat(p0: String): Boolean {
            if (p0.isEmpty() || p0.isBlank() || p0.firstOrNull { it == ' ' } != null) {
                player.getBukkitPlayer().sendMessage("§c您的输入不合法，请重新输入")
                return true
            } else if (p0.getHumanRace() != null) {
                player.getBukkitPlayer().sendMessage("一个名为 $p0 的角色已存在，请重新输入")
                return true
            } else if (p0.length > 12) {
                player.getBukkitPlayer().sendMessage("角色名过长，请重新输入")
                return true
            }
            player.nextGeneration(PlayerManager.createHumanRace(player.getBukkitPlayer(), p0))
            player.getBukkitPlayer().sendMessage("新角色创建成功，开始新的生活吧!")
            HandlerList.unregisterAll(action)
            return false
        }

        override fun quit(): String = ""

        override fun cancel() {
            player.getBukkitPlayer().sendMessage("请在聊天框内输入您的新角色名以继续游戏")
            Features.inputChat(player.getBukkitPlayer(), this)
        }
    }

    private val action = object : Listener {
        @EventHandler(priority = EventPriority.HIGH)
        fun onMove(e: PlayerMoveEvent) {
            if (e.player == player.getBukkitPlayer()) {
                e.isCancelled = true
            }
        }

        @EventHandler(priority = EventPriority.HIGH)
        fun onBreak(e: BlockBreakEvent) {
            if (e.player == player.getBukkitPlayer()) {
                e.isCancelled = true
            }
        }

        @EventHandler(priority = EventPriority.HIGH)
        fun onPlace(e: BlockPlaceEvent) {
            if (e.player == player.getBukkitPlayer()) {
                e.isCancelled = true
            }
        }

        @EventHandler(priority = EventPriority.HIGH)
        fun onCommand(e: PlayerCommandPreprocessEvent) {
            if (e.player == player.getBukkitPlayer()) {
                e.isCancelled = true
            }
        }

        @EventHandler(priority = EventPriority.HIGH)
        fun onHit(e: PlayerAttackEvent) {
            if (e.player == player.getBukkitPlayer()) {
                e.isCancelled = true
            }
        }

        @EventHandler(priority = EventPriority.HIGH)
        fun onBeHit(e: EntityDamageEvent) {
            if (e.entity == player.getBukkitPlayer()) {
                e.isCancelled = true
            }
        }

        @EventHandler(priority = EventPriority.HIGH)
        fun onQuit(e: PlayerQuitEvent) {
            if (e.player == player.getBukkitPlayer()) {
                HandlerList.unregisterAll(this)
            }
        }
    }

    init {
        player.getBukkitPlayer().inventory.clear()
        Bukkit.getPluginManager().registerEvents(action, CityCore.plugin)
        player.getBukkitPlayer().sendMessage("请在聊天框内输入您的新角色名以继续游戏")
        Features.inputChat(player.getBukkitPlayer(), input)
    }

}