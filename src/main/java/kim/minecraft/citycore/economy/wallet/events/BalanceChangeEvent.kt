package kim.minecraft.citycore.economy.wallet.events

import kim.minecraft.citycore.economy.currency.Currency
import kim.minecraft.citycore.economy.wallet.tags.WalletHolder
import kim.minecraft.citycore.events.CityCoreEvent

class BalanceChangeEvent(val walletHolder: WalletHolder, val action: Action, val  currency: Currency, val from: Double, val to: Double) : CityCoreEvent() {

    enum class Action {
        WALLET_RESET,
        WALLET_SET,
        WALLET_UP,
        WALLET_DOWN
    }
}