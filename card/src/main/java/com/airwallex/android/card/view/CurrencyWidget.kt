package com.airwallex.android.card.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import com.airwallex.android.card.databinding.WidgetCurrencyBinding
import com.airwallex.android.core.util.CurrencyUtils
import com.airwallex.android.core.util.CurrencyUtils.formatPrice
import java.math.BigDecimal
import java.util.*

class CurrencyWidget(context: Context, attrs: AttributeSet?) :
    FrameLayout(context, attrs) {

    val currency: String
        get() {
            return viewBinding.tvCurrency.text.toString()
        }

    val price: String
        get() {
            return viewBinding.tvPrice.text.toString()
        }

    private val viewBinding = WidgetCurrencyBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    fun updateCurrency(currency: String, amount: BigDecimal) {
        viewBinding.tvCurrency.text = currency
        viewBinding.tvPrice.text = formatPrice(currency, amount)

        try {
            val country = CurrencyUtils.currencyToCountryMap[currency]
            if (country == null) {
                viewBinding.icon.visibility = View.GONE
            } else {
                val resourceId: Int = resources.getIdentifier(
                    String.format(
                        "ic_flag_%s",
                        country.lowercase(Locale.US)
                    ),
                    "drawable", context.packageName
                )
                viewBinding.icon.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        resourceId,
                        null
                    )
                )
            }
        } catch (e: Exception) {
            viewBinding.icon.visibility = View.GONE
        }
    }
}
