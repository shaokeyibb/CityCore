package kim.minecraft.citycore.chunk.command

import io.izzel.taboolib.module.command.base.BaseCommand
import io.izzel.taboolib.module.command.base.BaseMainCommand
import io.izzel.taboolib.module.command.base.CommandType
import io.izzel.taboolib.module.command.base.SubCommand
import kim.minecraft.citycore.chunk.tasks.CountryClaimChunkTask
import kim.minecraft.citycore.chunk.tasks.TaskManager
import kim.minecraft.citycore.player.PlayerManager.toCCPlayer
import kim.minecraft.citycore.player.PlayerManager.toHumanRace
import kim.minecraft.citycore.politics.country.CountryManager.toCountry
import kim.minecraft.citycore.politics.party.PartyManager.toParty
import kim.minecraft.citycore.utils.storage.SettingsStorage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("unused")
@BaseCommand(name = "chunk", aliases = ["地块"], description = "地块管理相关指令")
class ChunkCommand : BaseMainCommand() {

    @SubCommand(priority = 0.0, description = "殖民脚下的地块", type = CommandType.PLAYER, arguments = [])
    fun claim(sender: CommandSender, args: Array<String>) {
        if ((sender as Player).toCCPlayer()!!.currentHumanRace.toHumanRace().currentCountry == null) {
            sender.sendMessage("§c您尚不属于任何一个国家，无法殖民")
            return
        }

        if (sender.toCCPlayer()!!.currentHumanRace.toHumanRace().currentCountry!!.toCountry().owner != sender.toCCPlayer()!!.currentHumanRace.toHumanRace() &&
                (sender.toCCPlayer()!!.currentHumanRace.toHumanRace().currentCountry!!.toCountry().owner == sender.toCCPlayer()!!.currentHumanRace.toHumanRace().currentParty?.toParty() &&
                        sender.toCCPlayer()!!.currentHumanRace != sender.toCCPlayer()!!.currentHumanRace.toHumanRace().currentParty!!.toParty().ownerHumanRace)) {
            sender.sendMessage("§c您并非一个国家的所有者或执政党党魁，无法殖民")
            return
        }

        if (sender.toCCPlayer()!!.getChunkAtPlace().getBelongingsCountry() != null) {
            sender.sendMessage("§c您脚下的地块已有所有者，无法殖民")
            return
        }

        if (!sender.toCCPlayer()!!.getChunkAtPlace().hasSuchCountryAround(sender.toCCPlayer()!!.currentHumanRace.toHumanRace().currentCountry!!.toCountry())) {
            sender.sendMessage("§c您无法殖民一个与您国家不相邻的地块")
            return
        }

        if (TaskManager.countryClaimChunkTasks[sender.toCCPlayer()!!.getChunkAtPlace().chunkSearcher]?.country == sender.toCCPlayer()!!.currentHumanRace.toHumanRace().currentCountry!!.toCountry()) {
            sender.sendMessage("§c您不能殖民一个您正在殖民的区块")
            return
        }

        if (TaskManager.countryClaimChunkTasks.values.count { it.country == sender.toCCPlayer()!!.currentHumanRace.toHumanRace().currentCountry!!.toCountry() } >= SettingsStorage.settings.getInt("CountryClaimChunkMaxCounts", 1)) {
            sender.sendMessage("§c您最大只能同时殖民 ${SettingsStorage.settings.getInt("CountryClaimChunkMaxCounts", 1)} 块地块")
            return
        }

        if (TaskManager.countryClaimChunkTasks[sender.toCCPlayer()!!.getChunkAtPlace().chunkSearcher] != null) {
            sender.sendMessage("您夺过了 ${TaskManager.countryClaimChunkTasks[sender.toCCPlayer()!!.getChunkAtPlace().chunkSearcher]!!.country.name} 对本地块的殖民进程")
            TaskManager.countryClaimChunkTasks[sender.toCCPlayer()!!.getChunkAtPlace().chunkSearcher]!!.cancelTask()
        }
        CountryClaimChunkTask(sender.toCCPlayer()!!, sender.toCCPlayer()!!.currentHumanRace.toHumanRace().currentCountry!!.toCountry(), sender.toCCPlayer()!!.getChunkAtPlace()).also {
            sender.sendMessage("开始殖民本地块，距离殖民结束大约还需要${it.getRemainTimeSeconds()}秒")
        }
    }

    @SubCommand(priority = 1.0, description = "查看当前区块信息", type = CommandType.PLAYER, arguments = [])
    fun info(sender: CommandSender, args: Array<String>) {
        if (args.isNotEmpty()) {
            sender.sendMessage("§c指令参数错误，请检查参数是否正确")
            return
        }

        val chunk = (sender as Player).toCCPlayer()!!.getChunkAtPlace()
        sender.sendMessage("""
            区块位置: ${chunk.chunkSearcher}
            区块归属:${if (chunk.getBelongingsCountry() != null) chunk.getBelongingsCountry()!!.name else "无"}
        """.trimIndent())
    }
}