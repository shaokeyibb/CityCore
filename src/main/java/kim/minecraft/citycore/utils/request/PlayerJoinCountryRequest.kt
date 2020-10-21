package kim.minecraft.citycore.utils.request

import kim.minecraft.citycore.player.HumanRace
import kim.minecraft.citycore.player.PlayerManager.toHumanRace
import kim.minecraft.citycore.player.PlayerManager.toOfflinePlayer
import kim.minecraft.citycore.politics.country.Country
import kim.minecraft.citycore.politics.party.Party
import kim.minecraft.citycore.utils.lateralmessenger.MailServiceManager.mailTo
import kim.minecraft.citycore.utils.request.tags.RequestReceiver
import kim.minecraft.citycore.utils.request.tags.RequestSender

class PlayerJoinCountryRequest(sender: RequestSender, handlerObj: Any) : Request(sender, handlerObj) {

    override val timeOut: Long = 2000
    override val type: RequestType = RequestType.PLAYER_JOIN_COUNTRY
    override val receiver: Array<RequestReceiver> = if ((handlerObj as Country).owner is Party) (handlerObj.owner as Party).operatorHumanRaces.map { it.toHumanRace() }.toTypedArray() else arrayOf(handlerObj.owner as HumanRace)
    override val onAllow: (RequestSender, Any) -> Int = { requestSender: RequestSender, country: Any ->
        if ((requestSender as HumanRace).currentCountry != null) 403
        else {
            requestSender.player.toOfflinePlayer().mailTo(arrayOf("您申请加入 ${(country as Country).name} 的请求已被通过"))
            country.members.add(requestSender.uniqueID)
            200
        }
    }
    override val onDeny: (RequestSender, Any) -> Int = { requestSender: RequestSender, country: Any ->
        (requestSender as HumanRace).player.toOfflinePlayer().mailTo(arrayOf("您申请加入 ${(country as Country).name} 的请求拒绝通过"))
        destroy()
        200
    }
}