package com.airwallex.android.view

import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.R
import com.airwallex.android.core.model.Address
import com.airwallex.android.core.model.Shipping
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.BeforeTest
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class BillingWidgetTest {

    private val context = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.AirwallexDefaultTheme
    )

    private val billingWidget: BillingWidget by lazy {
        BillingWidget(context, null)
    }

    private var shipping: Shipping = Shipping.Builder()
        .setFirstName("Verify")
        .setLastName("Doe")
        .setPhone("13800000000")
        .setAddress(
            Address.Builder()
                .setCountryCode("CN")
                .setState("Shanghai")
                .setCity("Shanghai")
                .setStreet("Pudong District")
                .setPostcode("100000")
                .build()
        )
        .build()

    @BeforeTest
    fun setup() {
        billingWidget.shipping = shipping
    }

    @Test
    fun isValidTest() {
        assertTrue(billingWidget.isValid)
    }

    @Test
    fun billingValueTest() {
        assertNotNull(billingWidget.billing)
    }
}
