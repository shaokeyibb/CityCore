package kim.minecraft.citycore.economy.wallet.command

import io.izzel.taboolib.module.command.base.*
import kim.minecraft.citycore.economy.currency.CurrencyManager
import kim.minecraft.citycore.economy.currency.CurrencyManager.getCurrency
import kim.minecraft.citycore.economy.wallet.events.BalanceChangeEvent
import kim.minecraft.citycore.player.PlayerManager
import kim.minecraft.citycore.player.PlayerManager.toCCPlayer
import kim.minecraft.citycore.player.PlayerManager.toHumanRace
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("unused")
@BaseCommand(name = "wallet", aliases = ["money", " balance", "钱包"], description = "钱包管理相关指令")
class BalanceCommand : BaseMainCommand() {

    @SubCommand(priority = 0.0, description = "查看钱包状态", type = CommandType.PLAYER, arguments = [])
    fun check(sender: CommandSender, args: Array<String>) {
        sender.sendMessage("${(sender as Player).toCCPlayer()!!.currentHumanRace.toHumanRace().name} 的钱包状态")
        CurrencyManager.currencies.values.forEach {
            val bal = sender.toCCPlayer()!!.currentHumanRace.toHumanRace().wallet.getBalance(it)
            if (bal != 0.0) {
                sender.sendMessage("${it.name}: $bal${it.symbol}")
            }
        }
    }

    @SubCommand(priority = 1.0, description = "查看其他玩家钱包状态", type = CommandType.ALL, arguments = ["在线玩家"], permission = "citycore.admin")
    val checkOthers = object : BaseSubCommand() {
        override fun onCommand(p0: CommandSender, p1: Command?, p2: String?, p3: Array<out String>) {

            val temp = PlayerManager.humanRaces.values.firstOrNull { p3[0] == it.name }

            if (temp == null) {
                p0.sendMessage("§c指定玩家不存在")
                return
            }

            p0.sendMessage("${temp.name} 的钱包状态")
            CurrencyManager.currencies.values.forEach {
                val bal = temp.wallet.getBalance(it)
                if (bal != 0.0) {
                    p0.sendMessage("${it.name}: $bal${it.symbol}")
                }
            }
        }

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("在线玩家") { Bukkit.getOnlinePlayers().map { it.toCCPlayer()?.currentHumanRace?.toHumanRace()?.name } })
        }
    }

    @SubCommand(priority = 2.0, description = "向其他玩家付款", type = CommandType.PLAYER, arguments = ["在线玩家", "付款货币名", "付款金额"])
    val pay = object : BaseSubCommand() {
        override fun onCommand(p0: CommandSender, p1: Command?, p2: String?, p3: Array<out String>) {

            val human = p3[0].toHumanRace()
            val currency = p3[1].getCurrency()
            val balance = p3[2].toDoubleOrNull()

            if (human == null) {
                p0.sendMessage("§c指定玩家不存在")
                return
            }

            if (human.player == (p0 as Player).uniqueId) {
                p0.sendMessage("§c您不能向您自己转账")
                return
            }

            if (currency == null) {
                p0.sendMessage("§c参数错误，指定的货币名称不存在")
                return
            }

            if (balance == null) {
                p0.sendMessage("§c参数错误，付款金额必须为一个数字")
                return
            }

            if (balance < 0) {
                p0.sendMessage("§c参数错误，付款金额必须为一个正数")
                return
            }

            if (p0.toCCPlayer()!!.currentHumanRace.toHumanRace().wallet.getBalance(currency) - balance < 0) {
                p0.sendMessage("§c你没有足够的钱以向指定玩家转账")
                return
            }

            Bukkit.getPluginManager().callEvent(BalanceChangeEvent(human, BalanceChangeEvent.Action.WALLET_UP, currency, human.wallet.getBalance(currency), human.wallet.getBalance(currency) + balance))
            p0.toCCPlayer()!!.currentHumanRace.toHumanRace().wallet.changeBalance(currency, -balance, false)
            human.wallet.changeBalance(currency, balance, false)
            p0.sendMessage("转账成功，玩家 ${human.name} 将会收到您的 [${currency.name} $balance${currency.symbol}] 账款")

        }

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("在线玩家") { Bukkit.getOnlinePlayers().map { it.toCCPlayer()?.currentHumanRace?.toHumanRace()?.name } },
                    Argument("付款货币名") { CurrencyManager.currencies.values.map { it.name } },
                    Argument("付款金额") { listOf() })
        }

    }

    @SubCommand(priority = 10.0, description = "管理员指令", type = CommandType.ALL, arguments = ["change/set/reset", "在线玩家", "货币名"], permission = "citycore.admin")
    val admin = object : BaseSubCommand() {
        override fun onCommand(p0: CommandSender, p1: Command?, p2: String?, p3: Array<out String>) {

            val human = p3[1].toHumanRace()
            val currency = p3[2].getCurrency()

            if (human == null) {
                p0.sendMessage("§c指定玩家不存在")
                return
            }

            if (currency == null) {
                p0.sendMessage("§c参数错误，指定的货币名称不存在")
                return
            }


            when (p3[0]) {
                "change" -> {
                    if (p3.size != 4) {
                        p0.sendMessage("§c参数不足，请提供货币数量")
                        return
                    }

                    val balance = p3[3].toDoubleOrNull()

                    if (balance == null) {
                        p0.sendMessage("§c参数错误，货币数量必须为一个数字")
                        return
                    }

                    human.wallet.changeBalance(currency, balance, false)
                    p0.sendMessage("已成功设置玩家 ${human.name} 的 ${currency.name} 货币数量为 ${human.wallet.getBalance(currency)}")
                }

                "set" -> {
                    if (p3.size != 4) {
                        p0.sendMessage("§c参数不足，请提供货币数量")
                        return
                    }

                    val balance = p3[3].toDoubleOrNull()

                    if (balance == null) {
                        p0.sendMessage("§c参数错误，货币数量必须为一个数字")
                        return
                    }

                    human.wallet.setBalance(currency, balance)
                    p0.sendMessage("已成功设置玩家 ${human.name} 的 ${currency.name} 货币数量为 $balance")
                }

                "reset" -> {
                    human.wallet.resetBalance(currency)
                    p0.sendMessage("已成功重置玩家 ${human.name} 的 ${currency.name} 货币数量")
                }
            }
        }

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("change/set/reset") { listOf("change", "set", "reset") },
                    Argument("在线玩家") { Bukkit.getOnlinePlayers().map { it.toCCPlayer()?.currentHumanRace?.toHumanRace()?.name } },
                    Argument("货币名") { CurrencyManager.currencies.values.map { it.name } })
        }

    }
}