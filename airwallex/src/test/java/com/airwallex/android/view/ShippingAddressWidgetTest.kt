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
import kotlin.test.assertEquals
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
        .setEmail("john.doe@airwallex.com")
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
        assertTrue(shippingAddressWidget.isValid)
    }

    @Test
    fun shippingValueTest() {
        assertEquals(shipping, shippingAddressWidget.shipping)
    }

    @Test
    fun listenFocusChangedTest() {
        shippingAddressWidget.firstNameTextInputLayout.value = "a"
        shippingAddressWidget.firstNameTextInputLayout.requestInputFocus()
        assertEquals(null, shippingAddressWidget.firstNameTextInputLayout.error)

        shippingAddressWidget.lastNameTextInputLayout.value = "a"
        shippingAddressWidget.lastNameTextInputLayout.requestInputFocus()
        assertEquals(null, shippingAddressWidget.lastNameTextInputLayout.error)

        shippingAddressWidget.stateTextInputLayout.value = ""
        shippingAddressWidget.stateTextInputLayout.requestInputFocus()
        assertEquals(null, shippingAddressWidget.stateTextInputLayout.error)

        shippingAddressWidget.cityTextInputLayout.value = "city"
        shippingAddressWidget.cityTextInputLayout.requestInputFocus()
        assertEquals(null, shippingAddressWidget.cityTextInputLayout.error)

        shippingAddressWidget.addressTextInputLayout.value = "address"
        shippingAddressWidget.addressTextInputLayout.requestInputFocus()
        assertEquals(null, shippingAddressWidget.addressTextInputLayout.error)

        shippingAddressWidget.emailTextInputLayout.value = "jim631@sina.com"
        shippingAddressWidget.emailTextInputLayout.requestInputFocus()
        assertEquals(null, shippingAddressWidget.emailTextInputLayout.error)
    }
}
