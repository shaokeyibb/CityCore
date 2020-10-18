package kim.minecraft.citycore.economy.currency

import kim.minecraft.citycore.politics.country.Country
import kim.minecraft.citycore.politics.country.CountryManager.toCountry
import kim.minecraft.citycore.utils.serialzation.UUIDAsStringSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Currency(val name: String, @Serializable(with = UUIDAsStringSerializer::class) val belongingsCountry: UUID, val symbol: Char) {

    @Serializable(with = UUIDAsStringSerializer::class) val uniqueID: UUID = UUID.randomUUID()

    init {
        CurrencyManager.currencies[uniqueID] = this
    }

    fun getBelongingsCountry(): Country {
        return belongingsCountry.toCountry()
    }

    override fun hashCode(): Int {
        return uniqueID.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return hashCode() == other.hashCode()
    }
}