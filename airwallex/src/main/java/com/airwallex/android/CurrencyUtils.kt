package com.airwallex.android

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

object CurrencyUtils {

    fun formatPrice(
        currency: String,
        amount: BigDecimal
    ): String {
        val currencyFormat = NumberFormat.getCurrencyInstance()
        val decimalFormatSymbols = (currencyFormat as DecimalFormat)
            .decimalFormatSymbols
        val symbol = getCurrencySymbol(currency)
        decimalFormatSymbols.currencySymbol = symbol
        currencyFormat.decimalFormatSymbols = decimalFormatSymbols
        return currencyFormat.format(amount)
    }

    private val currencyLocaleMap: TreeMap<Currency, Locale> by lazy {
        TreeMap(Comparator { c1, c2 -> c1.currencyCode.compareTo(c2.currencyCode) })
    }

    private fun getCurrencySymbol(currencyCode: String?): String {
        val currency = Currency.getInstance(currencyCode)
        if (currencyLocaleMap.containsKey(currency)) {
            return currency.getSymbol(currencyLocaleMap[currency])
        }
        return currency.symbol
    }

    init {
        for (locale in Locale.getAvailableLocales()) {
            try {
                val currency = Currency.getInstance(locale)
                currencyLocaleMap[currency] = locale
            } catch (ignored: Exception) {
            }
        }
    }
}
