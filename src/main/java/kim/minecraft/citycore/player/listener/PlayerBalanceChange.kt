package kim.minecraft.citycore.player.listener

import io.izzel.taboolib.module.inject.TListener
import kim.minecraft.citycore.economy.wallet.events.BalanceChangeEvent
import kim.minecraft.citycore.player.PlayerManager
import kim.minecraft.citycore.player.PlayerManager.toHumanRace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

@Suppress("unused")
@TListener
object PlayerBalanceChange : Listener {

    @EventHandler
    fun onBalanceChange(e: BalanceChangeEvent) {
        PlayerManager.players.values.firstOrNull { it.currentHumanRace.toHumanRace() === e.walletHolder }.run {
            if (this == null) return

            if (e.action == BalanceChangeEvent.Action.WALLET_RESET)
                getBukkitPlayer().sendMessage("您的钱包中的 ${e.currency.name} 已被清空")
            else if (e.action == BalanceChangeEvent.Action.WALLET_SET)
                getBukkitPlayer().sendMessage("您的钱包中的 ${e.currency.name} 已被设置为 ${e.to}")
            else if (e.action == BalanceChangeEvent.Action.WALLET_UP)
                getBukkitPlayer().sendMessage("您收到了 ${e.currency.name} ${e.to - e.from}${e.currency.symbol}")
            else if (e.action == BalanceChangeEvent.Action.WALLET_DOWN)
                getBukkitPlayer().sendMessage("您被扣除了 ${e.currency.name} ${e.from - e.to}${e.currency.symbol}")
        }
    }
}