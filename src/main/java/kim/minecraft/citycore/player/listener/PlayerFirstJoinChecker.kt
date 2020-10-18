package kim.minecraft.citycore.player.listener

import io.izzel.taboolib.module.inject.TListener
import kim.minecraft.citycore.player.PlayerManager.toCCPlayer
import kim.minecraft.citycore.player.PlayerManager.toHumanRace
import kim.minecraft.citycore.player.tasks.PlayerFirstJoinTask
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent

@Suppress("unused")
@TListener
object PlayerFirstJoinChecker : Listener {

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        if (e.player.toCCPlayer() == null) {
            e.player.setDisplayName("一位尚未初始化角色的新玩家")
            e.player.setPlayerListName("一位尚未初始化角色的新玩家")
            e.joinMessage = "§e§l一位尚未初始化角色的新玩家 加入了服务器"
            PlayerFirstJoinTask(e.player)
        } else {
            e.player.setDisplayName(e.player.toCCPlayer()!!.currentHumanRace.toHumanRace().name)
            e.player.setPlayerListName(e.player.toCCPlayer()!!.currentHumanRace.toHumanRace().name)
            e.joinMessage = "§e§l${e.player.toCCPlayer()!!.currentHumanRace.toHumanRace().name} 加入了服务器"
        }

        @EventHandler(priority = EventPriority.HIGH)
        fun onLeave(e: PlayerQuitEvent) {
            if (e.player.toCCPlayer() == null) {
                e.player.setDisplayName("一位尚未初始化角色的新玩家")
                e.player.setPlayerListName("一位尚未初始化角色的新玩家")
                e.quitMessage = "§e§l一位尚未初始化角色的新玩家 退出了服务器"
            } else {
                e.player.setDisplayName(e.player.toCCPlayer()!!.currentHumanRace.toHumanRace().name)
                e.player.setPlayerListName(e.player.toCCPlayer()!!.currentHumanRace.toHumanRace().name)
                e.quitMessage = "§e§l${e.player.toCCPlayer()!!.currentHumanRace.toHumanRace().name} 退出了服务器"
            }
        }

        @EventHandler
        fun onKick(e: PlayerKickEvent) {
            if (e.player.toCCPlayer() == null) {
                e.leaveMessage = "§e§l一位尚未初始化角色的新玩家 因为 ${e.reason} 被踢出了服务器"
            }
        }
    }
}