package kim.minecraft.citycore.economy.currency.command

import io.izzel.taboolib.module.command.base.BaseCommand
import io.izzel.taboolib.module.command.base.CommandType
import io.izzel.taboolib.module.command.base.SubCommand
import org.bukkit.command.CommandSender

@Suppress("unused")
@BaseCommand(name = "currency", aliases = ["货币"], description = "国家货币管理相关指令")
class CurrencyCommand {

    @SubCommand(priority = 0.0, description = "检索国库", type = CommandType.PLAYER, arguments = [])
    fun check(sender: CommandSender, args: Array<String>) {
        TODO("检索国库指令")
    }

    @SubCommand(priority = 1.0, description = "发行货币", type = CommandType.PLAYER, arguments = ["货币数额"])
    fun issue(sender: CommandSender, args: Array<String>) {
        TODO("发行货币指令")
    }

    @SubCommand(priority = 2.0, description = "分配货币", type = CommandType.PLAYER, arguments = ["目标玩家", "货币数额"])
    fun distribute(sender: CommandSender, args: Array<String>) {
        TODO("分配货币指令")
    }

}