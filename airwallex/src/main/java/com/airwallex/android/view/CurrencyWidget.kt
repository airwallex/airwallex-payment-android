package com.airwallex.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import com.airwallex.android.CurrencyUtils.formatPrice
import com.airwallex.android.R
import kotlinx.android.synthetic.main.widget_currency.view.*
import java.math.BigDecimal
import java.util.*

internal class CurrencyWidget(context: Context, attrs: AttributeSet) :
    FrameLayout(context, attrs) {

    init {
        View.inflate(context, R.layout.widget_currency, this)
    }

    fun updateCurrency(currency: String, amount: BigDecimal) {
        tv_currency.text = currency
        tv_price.text = formatPrice(currency, amount)

        try {
            val resourceId: Int = resources.getIdentifier(currency.toLowerCase(Locale.US), "drawable", context.packageName)
            icon.setImageDrawable(ResourcesCompat.getDrawable(resources, resourceId, null))
        } catch (e: Exception) {
            icon.visibility = View.GONE
        }
    }
}
