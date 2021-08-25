package com.airwallex.android.view

import android.util.AttributeSet
import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.R
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.math.BigDecimal
import kotlin.test.*

@RunWith(RobolectricTestRunner::class)
class CurrencyWidgetTest {

    private var attributes: AttributeSet? = null
    private lateinit var currencyWidget: CurrencyWidget

    private val context = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.AirwallexDefaultTheme
    )

    @BeforeTest
    fun setup() {
        currencyWidget = CurrencyWidget(context, attributes)
    }

    @Test
    fun isNotNull() {
        assertNotNull(context)
    }

    @Test
    fun isValidTest() {
        assertEquals(true, currencyWidget.isValid("USD", BigDecimal.valueOf(100.00)))
    }
}
