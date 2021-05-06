package com.airwallex.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import com.airwallex.android.CurrencyUtils.formatPrice
import com.airwallex.android.databinding.WidgetCurrencyBinding
import java.math.BigDecimal
import java.util.*

internal class CurrencyWidget(context: Context, attrs: AttributeSet) :
    FrameLayout(context, attrs) {

    private val viewBinding = WidgetCurrencyBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private val currencyToCountryMap = mapOf(
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

    fun updateCurrency(currency: String, amount: BigDecimal) {
        viewBinding.tvCurrency.text = currency
        viewBinding.tvPrice.text = formatPrice(currency, amount)

        try {
            val country = currencyToCountryMap[currency]
            if (country == null) {
                viewBinding.icon.visibility = View.GONE
            } else {
                val resourceId: Int = resources.getIdentifier(String.format("ic_flag_%s", country.toLowerCase(Locale.US)), "drawable", context.packageName)
                viewBinding.icon.setImageDrawable(ResourcesCompat.getDrawable(resources, resourceId, null))
            }
        } catch (e: Exception) {
            viewBinding.icon.visibility = View.GONE
        }
    }
}
