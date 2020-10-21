package kim.minecraft.citycore.utils.request

import kim.minecraft.citycore.utils.request.tags.RequestReceiver
import kim.minecraft.citycore.utils.request.tags.RequestSender

object RequestManager {
    private var totalID = 0

    val requests: MutableMap<Int, Request> = mutableMapOf()

    fun nextID(): Int {
        totalID++
        return totalID
    }

    fun RequestSender.getAvailableRequest(): Array<Request> {
        return requests.values.filter { !it.destroyed && it.sender == this }.toTypedArray()
    }

    fun RequestReceiver.getAvailableRequest(): Array<Request> {
        return requests.values.filter { !it.destroyed && this in it.receiver }.toTypedArray()
    }
}