package kim.minecraft.citycore.player

import kim.minecraft.citycore.company.tags.CompanyOwner
import kim.minecraft.citycore.economy.wallet.tags.WalletHolder
import kim.minecraft.citycore.economy.wallet.Wallet
import kim.minecraft.citycore.permission.PermissionHolder
import kim.minecraft.citycore.player.PlayerManager.toCCPlayer
import kim.minecraft.citycore.politics.country.tags.CountryOwner
import kim.minecraft.citycore.utils.request.tags.RequestReceiver
import kim.minecraft.citycore.utils.request.tags.RequestSender
import kim.minecraft.citycore.utils.serialzation.UUIDAsStringSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class HumanRace(@Serializable(with = UUIDAsStringSerializer::class) val player: UUID, var name: String, @Serializable(with = UUIDAsStringSerializer::class) var currentCountry: UUID?, @Serializable(with = UUIDAsStringSerializer::class) var currentParty: UUID?, var alive: Boolean, override val wallet: Wallet, override val permissions: MutableList<String>) : WalletHolder, CountryOwner, CompanyOwner, PermissionHolder, RequestReceiver, RequestSender {

    @Serializable(with = UUIDAsStringSerializer::class)
    override val uniqueID: UUID = UUID.randomUUID()

    init {
        PlayerManager.humanRaces[uniqueID] = this
    }

    fun getPlayer(): Player {
        return player.toCCPlayer()
    }

    override fun hashCode(): Int {
        return uniqueID.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return hashCode() == other.hashCode()
    }
}