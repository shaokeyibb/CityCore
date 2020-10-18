package kim.minecraft.citycore.economy.wallet

import java.util.*

object WalletManager {

    fun createWallet(currency: MutableMap<UUID, Double>): Wallet {
        return Wallet(currency)
    }

    fun createWallet(): Wallet {
        return createWallet(mutableMapOf())
    }

}