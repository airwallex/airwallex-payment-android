package com.airwallex.android.core.util

import androidx.annotation.VisibleForTesting
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

object CurrencyUtils {

    val currencyToCountryMap = mapOf(
        "AED" to "AE",
        "AUD" to "AU",
        "BEL" to "BE",
        "BDT" to "BD", // Bangladeshi Taka
        "BGR" to "BG",
        "CAD" to "CA",
        "CHF" to "CH",
        "CNH" to "CN",
        "CNY" to "CN",
        "CNY_ONSHORE" to "CN",
        "CSK" to "CZ",
        "CYP" to "CY",
        "CZE" to "CZ",
        "DKK" to "DK",
        "EEK" to "EE",
        "EUR" to "EU",
        "GBP" to "GB",
        "GIP" to "GI",
        "HKD" to "HK",
        "HRK" to "HR",
        "HRV" to "HR",
        "HUF" to "HU",
        "IDR" to "ID",
        "INR" to "IN",
        "JPY" to "JP",
        "ISK" to "IS",
        "KRW" to "KR",
        "LKR" to "LK", // Sri Lankan Rupee
        "MYR" to "MY",
        "NOK" to "NO",
        "NPR" to "NP", // Nepalese Rupee
        "NZD" to "NZ",
        "PHP" to "PH",
        "PKR" to "PK", // Pakistani Rupee
        "PLN" to "PL",
        "RON" to "RO",
        "SEK" to "SE",
        "SGD" to "SG",
        "THB" to "TH",
        "TRY" to "TR", // Turkish Lira
        "USD" to "US",
        "VND" to "VN"
    )

    fun formatPrice(
        currency: String,
        amount: BigDecimal
    ): String {
        val currencyFormat = NumberFormat.getCurrencyInstance()
        val decimalFormatSymbols = (currencyFormat as DecimalFormat)
            .decimalFormatSymbols
        var symbol = getCurrencySymbol(currency)
        if (symbol == "HK$") {
            symbol = "$"
        }
        decimalFormatSymbols.currencySymbol = symbol
        currencyFormat.decimalFormatSymbols = decimalFormatSymbols
        return currencyFormat.format(amount)
    }

    private val currencyLocaleMap: TreeMap<Currency, Locale> by lazy {
        TreeMap(Comparator { c1, c2 -> c1.currencyCode.compareTo(c2.currencyCode) })
    }

    @VisibleForTesting
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
