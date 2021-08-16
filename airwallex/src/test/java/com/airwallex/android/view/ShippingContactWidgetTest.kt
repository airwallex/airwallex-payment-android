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
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class ShippingContactWidgetTest {

    private val context = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.AirwallexDefaultTheme
    )

    private val shippingContactWidget: ShippingContactWidget by lazy {
        ShippingContactWidget(context, null)
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
        shippingContactWidget.initializeView(shipping)
    }

    @Test
    fun isValidShippingTest() {
        assertTrue(shippingContactWidget.isValidContact)
    }
}
