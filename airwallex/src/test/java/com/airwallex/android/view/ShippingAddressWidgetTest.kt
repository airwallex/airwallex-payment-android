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
class ShippingAddressWidgetTest {

    private val context = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.AirwallexDefaultTheme
    )

    private val shippingAddressWidget: ShippingAddressWidget by lazy {
        ShippingAddressWidget(context, null)
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
        shippingAddressWidget.initializeView(shipping)
    }

    @Test
    fun isValidShippingTest() {
        assertTrue(shippingAddressWidget.isValidShipping)
    }
}
