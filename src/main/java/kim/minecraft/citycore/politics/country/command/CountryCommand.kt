package kim.minecraft.citycore.politics.country.command

import io.izzel.taboolib.module.command.base.*
import kim.minecraft.citycore.economy.currency.CurrencyManager
import kim.minecraft.citycore.economy.currency.CurrencyManager.getCurrency
import kim.minecraft.citycore.player.HumanRace
import kim.minecraft.citycore.player.PlayerManager.toCCPlayer
import kim.minecraft.citycore.player.PlayerManager.toHumanRace
import kim.minecraft.citycore.politics.country.CountryManager
import kim.minecraft.citycore.politics.country.CountryManager.toCountry
import kim.minecraft.citycore.politics.party.Party
import kim.minecraft.citycore.politics.party.PartyManager.toParty
import kim.minecraft.citycore.utils.request.PlayerJoinCountryRequest
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("unused")
@BaseCommand(name = "country", aliases = ["国家"], description = "国家管理相关指令")
class CountryCommand : BaseMainCommand() {

    @SubCommand(priority = 0.0, description = "创建一个无国有货币的国家", type = CommandType.PLAYER, arguments = ["国家名称", "国家意识形态"])
    fun createWithNoCurrency(sender: CommandSender, args: Array<String>) {

        if ((sender as Player).toCCPlayer()!!.getChunkAtPlace().hasOwner()) {
            sender.sendMessage("§c您不能在此处创建国家，因为您所在的区块已有所有者")
            return
        }

        if (!args[1].endsWith("主义")) {
            sender.sendMessage("§c指令参数错误，国家意识形态应当以“主义”为结尾")
            return
        }

        if (CountryManager.countries.values.firstOrNull { it.name == args[0] } != null) {
            sender.sendMessage("§c国家创建失败，一个名为 ${args[0]} 的国家已存在")
            return
        }

        if (sender.toCCPlayer()!!.currentHumanRace.toHumanRace().currentCountry != null) {
            sender.sendMessage("§c国家创建失败，您作为另一个国家的公民无法创建国家")
            return
        }

        if (sender.toCCPlayer()!!.currentHumanRace.toHumanRace().currentParty != null) {
            if (sender.toCCPlayer()!!.currentHumanRace.toHumanRace().currentParty!!.toParty().ownerHumanRace != sender.toCCPlayer()!!.currentHumanRace) {
                sender.sendMessage("§c国家创建失败，您作为党员无法创建国家")
                return
            }
            CountryManager.createCountry(args[0], args[1], sender.toCCPlayer()!!.currentHumanRace.toHumanRace().currentParty!!.toParty(), sender.toCCPlayer()!!.getChunkAtPlace(), null, null)
        } else {
            CountryManager.createCountry(args[0], args[1], sender.toCCPlayer()!!.currentHumanRace.toHumanRace(), sender.toCCPlayer()!!.getChunkAtPlace(), null, null)
        }

        sender.sendMessage("国家创建成功")
    }

    @SubCommand(priority = 1.0, description = "创建一个有国有货币的国家", type = CommandType.PLAYER, arguments = ["国家名称", "国家意识形态", "货币名称", "货币单位符号"])
    fun createWithCurrency(sender: CommandSender, args: Array<String>) {

        if ((sender as Player).toCCPlayer()!!.getChunkAtPlace().hasOwner()) {
            sender.sendMessage("§c您不能在此处创建国家，因为您所在的区块已有所有者")
            return
        }

        if (!args[1].endsWith("主义")) {
            sender.sendMessage("§c指令参数错误，国家意识形态应当以“主义”为结尾")
            return
        }

        if (args[3].toCharArray().size != 1) {
            sender.sendMessage("§c指令参数错误，货币单位符号必须为一个半角字符")
            return
        }

        if (CountryManager.countries.values.firstOrNull { it.name == args[0] } != null) {
            sender.sendMessage("§c国家创建失败，一个名为 ${args[0]} 的国家已存在")
            return
        }

        if (CurrencyManager.currencies.values.firstOrNull { it.name == args[2] } != null) {
            sender.sendMessage("§c国家创建失败，一个名为 ${args[0]} 的货币已存在")
            return
        }

        if (sender.toCCPlayer()!!.currentHumanRace.toHumanRace().currentCountry != null) {
            sender.sendMessage("§c国家创建失败，您作为另一个国家的公民无法创建国家")
            return
        }

        if (sender.toCCPlayer()!!.currentHumanRace.toHumanRace().currentParty != null) {
            if (sender.toCCPlayer()!!.currentHumanRace.toHumanRace().currentParty!!.toParty().ownerHumanRace != sender.toCCPlayer()!!.currentHumanRace) {
                sender.sendMessage("§c国家创建失败，您作为党员无法创建国家")
                return
            }
            CountryManager.createCountry(args[0], args[1], sender.toCCPlayer()!!.currentHumanRace.toHumanRace().currentParty?.toParty()!!, sender.toCCPlayer()!!.getChunkAtPlace(), args[2], args[3].toCharArray()[0])
        } else {
            CountryManager.createCountry(args[0], args[1], sender.toCCPlayer()!!.currentHumanRace.toHumanRace(), sender.toCCPlayer()!!.getChunkAtPlace(), args[2], args[3].toCharArray()[0])
        }

        sender.sendMessage("国家创建成功")
    }

    @SubCommand(priority = 2.0, description = "查看您所属国家信息", type = CommandType.PLAYER, arguments = [])
    fun info(sender: CommandSender, args: Array<String>) {

        if (args.isNotEmpty()) {
            sender.sendMessage("§c指令参数错误，请检查参数是否正确")
            return
        }

        if ((sender as Player).toCCPlayer()!!.currentHumanRace.toHumanRace().currentCountry == null) {
            sender.sendMessage("§c您当前未属于任何一个国家")
            return
        }

        val temp = sender.toCCPlayer()!!.currentHumanRace.toHumanRace().currentCountry!!.toCountry()
        sender.sendMessage(
                """
国家名称: ${temp.name}
${
                    if (temp.owner is HumanRace) {
                        "国家元首: ${(temp.owner as HumanRace).name}"
                    } else {
                        """
国家执政党: ${(temp.owner as Party).name}
国家元首: ${(temp.owner as Party).ownerHumanRace.toHumanRace().name}
                        """.trimIndent()
                    }
                }
国家意识形态: ${temp.ideology}
国家货币: ${
                    if (temp.currency == null) {
                        "无"
                    } else {
                        temp.currency!!.name + " (${temp.currency!!.symbol})"
                    }
                }
国民数量: ${temp.members.size}(${temp.members.map { it.toHumanRace().name }})
国家领土数量: ${temp.chunks.size}(${temp.chunks})
国家政党数量:${temp.parties.size}(${temp.parties.map { it.toParty().name }})
                """.trimIndent())
    }

    @SubCommand(priority = 3.0, description = "以国家名义向其他国家转账", type = CommandType.PLAYER, arguments = ["国家名", "付款货币名", "付款金额"], permission = "citycore.admin")
    val payBetweenCountry = object : BaseSubCommand() {
        override fun onCommand(p0: CommandSender, p1: Command?, p2: String?, p3: Array<out String>) {
            if ((p0 as Player).toCCPlayer()!!.currentHumanRace.toHumanRace().currentCountry == null) {
                p0.sendMessage("§c您当前未属于任何一个国家")
                return
            }

            if (p0.toCCPlayer()!!.currentHumanRace.toHumanRace().currentCountry!!.toCountry().owner != p0.toCCPlayer()!!.currentHumanRace.toHumanRace() &&
                    (p0.toCCPlayer()!!.currentHumanRace.toHumanRace().currentCountry!!.toCountry().owner == p0.toCCPlayer()!!.currentHumanRace.toHumanRace().currentParty?.toParty() &&
                            p0.toCCPlayer()!!.currentHumanRace.toHumanRace() != p0.toCCPlayer()!!.currentHumanRace.toHumanRace().currentParty!!.toParty().ownerHumanRace.toHumanRace())) {
                p0.sendMessage("§c您并非一个国家的所有者或执政党党魁，无法向其他国家转账")
                return
            }

            val country = p3[0].toCountry()
            val currency = p3[1].getCurrency()
            val balance = p3[2].toDoubleOrNull()

            if (country == null) {
                p0.sendMessage("§c指定国家不存在")
                return
            }

            if (country.uniqueID == p0.toCCPlayer()!!.currentHumanRace.toHumanRace().currentCountry) {
                p0.sendMessage("§c您不能向本国转账")
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
                p0.sendMessage("§c你没有足够的钱以向指定国家转账")
                return
            }

            p0.toCCPlayer()!!.currentHumanRace.toHumanRace().currentCountry!!.toCountry().wallet.changeBalance(currency, -balance, false)
            country.wallet.changeBalance(currency, balance, false)
            p0.sendMessage("转账成功，${country.name} 将会收到您的 [${currency.name} $balance${currency.symbol}] 账款")

        }

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("国家名") { CountryManager.countries.values.map { it.name } },
                    Argument("付款货币名") { CurrencyManager.currencies.values.map { it.name } },
                    Argument("付款金额") { listOf() })
        }

    }

    @SubCommand(priority = 0.1, description = "加入一个国家", type = CommandType.PLAYER, arguments = ["国家名称"])
    val join = object : BaseSubCommand() {
        override fun onCommand(p0: CommandSender, p1: Command?, p2: String?, p3: Array<out String>) {
            if ((p0 as Player).toCCPlayer()!!.currentHumanRace.toHumanRace().currentCountry != null) {
                p0.sendMessage("§c您已经是一个国家的公民了")
                return
            }

            val country = p3[0].toCountry()

            if (country == null) {
                p0.sendMessage("§c指定国家不存在")
                return
            }

            PlayerJoinCountryRequest(p0.toCCPlayer()!!.currentHumanRace.toHumanRace(), country)
        }

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("国家名") { CountryManager.countries.values.map { it.name } })
        }
    }

    @SubCommand(priority = 0.2, description = "同意国家加入申请", type = CommandType.PLAYER, arguments = ["待处理请求玩家"])
    val accept = object : BaseSubCommand() {
        override fun onCommand(p0: CommandSender, p1: Command?, p2: String?, p3: Array<out String>) {

        }

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("待处理请求玩家") { Bukkit.getOnlinePlayers().map { it.toCCPlayer()?.currentHumanRace?.toHumanRace()?.name } })
        }
    }

    @SubCommand(priority = 0.3, description = "拒绝国家加入申请", type = CommandType.PLAYER, arguments = ["待处理请求玩家"])
    val deny = object : BaseSubCommand() {
        override fun onCommand(p0: CommandSender, p1: Command?, p2: String?, p3: Array<out String>) {

        }

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("待处理请求玩家") { Bukkit.getOnlinePlayers().map { it.toCCPlayer()?.currentHumanRace?.toHumanRace()?.name } })
        }
    }


}