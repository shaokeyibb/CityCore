package kim.minecraft.citycore.utils.request

import kim.minecraft.citycore.player.HumanRace
import kim.minecraft.citycore.player.PlayerManager.toOfflinePlayer
import kim.minecraft.citycore.utils.lateralmessenger.MailServiceManager.mailTo
import kim.minecraft.citycore.utils.request.tags.RequestReceiver
import kim.minecraft.citycore.utils.request.tags.RequestSender

object RequestManager {
    private var totalID = 0

    val requests: MutableMap<Int, Request> = mutableMapOf()

    fun nextID(): Int {
        totalID++
        return totalID
    }

    fun RequestSender.getAvailableSenderRequest(): Array<Request> {
        return requests.values.filter { !it.destroyed && it.sender == this }.toTypedArray()
    }

    fun RequestReceiver.getAvailableReceiverRequest(): Array<Request> {
        return requests.values.filter { !it.destroyed && this in it.receiver }.toTypedArray()
    }

    fun finalize() {
        requests.values.filter { !it.destroyed }.forEach { (it.sender as HumanRace).player.toOfflinePlayer().mailTo(arrayOf("您关于 ${it.type.friendName} 的请求已过期")) }
    }
}