package kim.minecraft.citycore.politics.party

import kim.minecraft.citycore.company.tags.CompanyOwner
import kim.minecraft.citycore.economy.wallet.Wallet
import kim.minecraft.citycore.economy.wallet.tags.WalletHolder
import kim.minecraft.citycore.player.HumanRace
import kim.minecraft.citycore.player.tags.MemberHolder
import kim.minecraft.citycore.politics.country.tags.CountryOwner
import kim.minecraft.citycore.utils.serialzation.UUIDAsStringSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Party(var name: String, var ideology: String, @Serializable(with = UUIDAsStringSerializer::class) var belongingsCountry: UUID?, @Serializable(with = UUIDAsStringSerializer::class) var ownerHumanRace: UUID, override val members: MutableList<@Serializable(with = UUIDAsStringSerializer::class) UUID>, override val wallet: Wallet) : MemberHolder, WalletHolder, CountryOwner, CompanyOwner {

    @Serializable(with = UUIDAsStringSerializer::class)
    override val uniqueID: UUID = UUID.randomUUID()

    init {
        PartyManager.parties[uniqueID] = this
    }

    override fun hashCode(): Int {
        return uniqueID.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return hashCode() == other.hashCode()
    }
}