package kim.minecraft.citycore.politics.country

import kim.minecraft.citycore.chunk.Chunk
import kim.minecraft.citycore.company.tags.CompanyOwner
import kim.minecraft.citycore.economy.currency.Currency
import kim.minecraft.citycore.economy.wallet.tags.WalletHolder
import kim.minecraft.citycore.economy.wallet.Wallet
import kim.minecraft.citycore.player.HumanRace
import kim.minecraft.citycore.player.tags.MemberHolder
import kim.minecraft.citycore.politics.country.event.CountryCreateEvent
import kim.minecraft.citycore.politics.country.tags.CountryOwner
import kim.minecraft.citycore.politics.party.Party
import kim.minecraft.citycore.utils.serialzation.UUIDAsStringSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import java.util.*

@Serializable
data class Country(var name: String, var ideology: String, @Polymorphic var owner: CountryOwner, val chunks: MutableList<Chunk.ChunkSearcher>, val parties: MutableList<@Serializable(with = UUIDAsStringSerializer::class) UUID>, var currency: Currency?, override val members: MutableList<@Serializable(with = UUIDAsStringSerializer::class) UUID>, override val wallet: Wallet) : MemberHolder, WalletHolder, CompanyOwner {

    @Serializable(with = UUIDAsStringSerializer::class)
    override val uniqueID: UUID = UUID.randomUUID()

    init {
        CountryManager.countries[uniqueID] = this
        Bukkit.getPluginManager().callEvent(CountryCreateEvent(owner, this))
    }

    override fun hashCode(): Int {
        return uniqueID.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return hashCode() == other.hashCode()
    }

}