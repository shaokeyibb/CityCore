package kim.minecraft.citycore.economy.wallet

import kim.minecraft.citycore.economy.currency.Currency
import kim.minecraft.citycore.utils.serialzation.UUIDAsStringSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Wallet(private val currency: MutableMap<@Serializable(with = UUIDAsStringSerializer::class) UUID, Double>) {

    @Serializable(with = UUIDAsStringSerializer::class)
    val uniqueID: UUID = UUID.randomUUID()

    fun getBalance(cur: Currency): Double {
        return currency[cur.uniqueID] ?: 0.0
    }

    fun changeBalance(cur: Currency, amount: Double, notAllowNegative: Boolean) {
        if (!notAllowNegative)
            currency[cur.uniqueID] = (currency[cur.uniqueID] ?: 0.0) + amount
        else
            currency[cur.uniqueID] = (currency[cur.uniqueID] ?: 0.0) + (if (((currency[cur.uniqueID]
                            ?: 0.0) - amount) < 0.0) 0.0 else (currency[cur.uniqueID] ?: 0.0) - amount)
    }

    fun resetBalance(cur: Currency) {
        currency[cur.uniqueID] = 0.0
    }

    fun setBalance(cur: Currency, amount: Double) {
        currency[cur.uniqueID] = amount
    }

    override fun hashCode(): Int {
        return uniqueID.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return hashCode() == other.hashCode()
    }
}