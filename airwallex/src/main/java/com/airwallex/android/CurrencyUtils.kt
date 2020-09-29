package com.airwallex.android

import java.util.*

object CurrencyUtils {

    private val currencyLocaleMap: TreeMap<Currency, Locale> by lazy {
        TreeMap(Comparator { c1, c2 -> c1.currencyCode.compareTo(c2.currencyCode) })
    }

    fun getCurrencySymbol(currencyCode: String?): String {
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