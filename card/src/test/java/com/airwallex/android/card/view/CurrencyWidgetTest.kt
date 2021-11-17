package com.airwallex.android.card.view

import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.ui.R
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.math.BigDecimal
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class CurrencyWidgetTest {

    private val context = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.AirwallexDefaultTheme
    )

    private val currencyWidget: CurrencyWidget by lazy {
        CurrencyWidget(context, null)
    }

    @Test
    fun currencyTest() {
        currencyWidget.updateCurrency("CNY", BigDecimal.ONE)
        assertEquals("CNY", currencyWidget.currency)
        assertEquals("￥1.00", currencyWidget.price)

        currencyWidget.updateCurrency("CNY", BigDecimal.TEN)
        assertEquals("CNY", currencyWidget.currency)
        assertEquals("￥10.00", currencyWidget.price)

        currencyWidget.updateCurrency("USD", BigDecimal.TEN)
        assertEquals("USD", currencyWidget.currency)
        assertEquals("US$10.00", currencyWidget.price)
    }
}
