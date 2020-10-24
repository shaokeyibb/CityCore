package kim.minecraft.citycore.player.tasks

import io.izzel.taboolib.common.event.PlayerAttackEvent
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.inventory.MenuBuilder
import kim.minecraft.citycore.CityCore
import kim.minecraft.citycore.player.PlayerManager
import kim.minecraft.citycore.player.PlayerManager.toCCPlayer
import kim.minecraft.citycore.player.PlayerManager.getHumanRace
import kim.minecraft.citycore.utils.storage.SettingsStorage
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
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
import org.bukkit.scheduler.BukkitRunnable

class PlayerFirstJoinTask(val player: Player) {

    private val action = object : Listener {
        @EventHandler(priority = EventPriority.HIGH)
        fun onMove(e: PlayerMoveEvent) {
            if (e.player == player) {
                e.isCancelled = true
            }
        }

        @EventHandler(priority = EventPriority.HIGH)
        fun onBreak(e: BlockBreakEvent) {
            if (e.player == player) {
                e.isCancelled = true
            }
        }

        @EventHandler(priority = EventPriority.HIGH)
        fun onPlace(e: BlockPlaceEvent) {
            if (e.player == player) {
                e.isCancelled = true
            }
        }

        @EventHandler(priority = EventPriority.HIGH)
        fun onChat(e: AsyncPlayerChatEvent) {
            if (e.player == player) {
                e.isCancelled = true
            }
        }

        @EventHandler(priority = EventPriority.HIGH)
        fun onCommand(e: PlayerCommandPreprocessEvent) {
            if (e.player == player) {
                e.isCancelled = true
            }
        }

        @EventHandler(priority = EventPriority.HIGH)
        fun onHit(e: PlayerAttackEvent) {
            if (e.player == player) {
                e.isCancelled = true
            }
        }

        @EventHandler(priority = EventPriority.HIGH)
        fun onBeHit(e: EntityDamageEvent) {
            if (e.entity == player) {
                e.isCancelled = true
            }
        }
    }

    private val chat: Listener = object : Listener {
        @EventHandler(priority = EventPriority.HIGH)
        fun onChat(e: AsyncPlayerChatEvent) {
            if (e.player == player) {
                e.isCancelled = true
                if (e.message.isEmpty() || e.message.isBlank() || e.message.firstOrNull { it == ' ' } != null) {
                    player.kickPlayer("角色名不合法，请重新输入")
                    unRegister()
                    return
                } else if (e.message.getHumanRace() != null) {
                    player.kickPlayer("一个名为 ${e.message} 的角色已存在，请重新输入")
                    unRegister()
                    return
                }
                PlayerManager.createPlayer(player, e.message)
                player.sendMessage("角色创建成功，欢迎您，${e.message}")
                player.setDisplayName(e.message)
                player.setPlayerListName(e.message)
                unRegister()
            }
        }
    }

    init {
        MenuBuilder.builder(CityCore.plugin)
                .title("§c第一次进服-初始化角色")
                .rows(3)
                .build {
                    it.setItem(9, ItemBuilder(Material.GREEN_DYE).name("欢迎来到本服务器").lore("现在，您需要通过创建新角色以继续游戏").build())
                    it.setItem(11, ItemBuilder(player).name("点击设置姓名").lore("设置您在游戏中的虚拟昵称").build())
                    it.setItem(17, ItemBuilder(Material.BARRIER).name("退出游戏").lore("拒绝填写信息并退出服务器").build())
                    startAction()
                }.event { clickEvent ->
                    clickEvent.isCancelled = true
                    when (clickEvent.rawSlot) {
                        11 -> {
                            player.closeInventory()
                            startChat()
                            player.sendMessage("请在聊天栏内输入您希望设置的虚拟昵称以继续")
                        }

                        17 -> {
                            player.kickPlayer("未完成角色初始化")
                        }
                    }
                }.open(player)
    }

    private fun startChat() {
        chat.also { it1 ->
            Bukkit.getPluginManager().registerEvents(it1, CityCore.plugin)
        }
    }

    private fun startAction() {
        action.also { it1 ->
            Bukkit.getPluginManager().registerEvents(it1, CityCore.plugin)
            object : BukkitRunnable() {
                override fun run() {
                    unRegister()
                    if (player.toCCPlayer() == null) player.kickPlayer("未在规定时间内完成角色初始化")
                }
            }.runTaskLater(CityCore.plugin, SettingsStorage.settings.getLong("RegisterTimeTicks", 400))
        }
    }

    private fun unRegister() {
        HandlerList.unregisterAll(chat)
        HandlerList.unregisterAll(action)
    }


}