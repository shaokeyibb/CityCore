package kim.minecraft.citycore.utils.request

import kim.minecraft.citycore.CityCore
import kim.minecraft.citycore.player.PlayerManager.toOfflinePlayer
import kim.minecraft.citycore.utils.lateralmessenger.MailServiceManager.mailTo
import kim.minecraft.citycore.utils.request.tags.RequestReceiver
import kim.minecraft.citycore.utils.request.tags.RequestSender
import org.bukkit.scheduler.BukkitRunnable

abstract class Request(val sender: RequestSender, private val handlerObj: Any) {

    abstract val timeOut: Long
    abstract val type: RequestType

    abstract val receiver: Array<RequestReceiver>
    abstract val onAllow: (RequestSender, Any) -> Int
    abstract val onDeny: (RequestSender, Any) -> Int

    private val id = RequestManager.nextID()

    var destroyed: Boolean = false

    private val task = object : BukkitRunnable() {
        override fun run() {
            destroy()
        }
    }

    fun allow(handler: RequestReceiver): Int {
        if (destroyed) return 408
        if (handler !in receiver) return 404
        return onAllow(sender, handlerObj).also { destroy() }
    }

    fun deny(handler: RequestReceiver): Int {
        if (destroyed) return 408
        if (handler !in receiver) return 404
        return onDeny(sender, handlerObj).also { destroy() }
    }

    fun destroy() {
        sender.uniqueID.toOfflinePlayer().mailTo(arrayOf("您关于 $type 的请求已过期或被拒绝"))
        destroyed = true
        task.cancel()
    }

    init {
        if (timeOut > 0)
            task.runTaskTimerAsynchronously(CityCore.plugin, 0, timeOut)
        RequestManager.requests[id] = this
    }
}