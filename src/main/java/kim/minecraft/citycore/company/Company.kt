package kim.minecraft.citycore.company

import kim.minecraft.citycore.company.tags.CompanyOwner
import kim.minecraft.citycore.economy.wallet.Wallet
import kim.minecraft.citycore.economy.wallet.tags.WalletHolder
import kim.minecraft.citycore.player.tags.MemberHolder
import kim.minecraft.citycore.utils.serialzation.UUIDAsStringSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
class Company(var name: String, var kind: Kind, var owner: CompanyOwner, override val members: MutableList<@Serializable(with = UUIDAsStringSerializer::class) UUID>, override val wallet: Wallet) : MemberHolder, WalletHolder {

    @Serializable(with = UUIDAsStringSerializer::class)
    val uniqueID: UUID = UUID.randomUUID()

    enum class Kind {
        UNLIMITED, LIMITED, LIMITED_SHARE
    }

    override fun hashCode(): Int {
        return uniqueID.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return hashCode() == other.hashCode()
    }
}