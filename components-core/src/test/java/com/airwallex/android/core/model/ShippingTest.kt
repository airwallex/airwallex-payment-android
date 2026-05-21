package com.airwallex.android.core.model

import com.airwallex.android.core.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class ShippingTest {

    private val shipping by lazy {
        Shipping.Builder()
            .setFirstName("John")
            .setLastName("Doe")
            .setPhone("13800000000")
            .setEmail("john.doe@airwallex.com")
            .setShippingMethod("shipping")
            .setAddress(
                Address.Builder()
                    .setCountryCode("US")
                    .setState("CA")
                    .setCity("San Francisco")
                    .setStreet("1460 Mission St.#02W101")
                    .setPostcode("94103")
                    .build()
            )
            .build()
    }

    @Test
    fun builderConstructor() {
        assertEquals(shipping, ShippingFixtures.SHIPPING)
    }

    @Test
    fun testParcelable() {
        assertEquals(ShippingFixtures.SHIPPING, ParcelUtils.create(ShippingFixtures.SHIPPING))
    }

    @Test
    fun testParams() {
        assertEquals("John", shipping.firstName)
        assertEquals("Doe", shipping.lastName)
        assertEquals("13800000000", shipping.phoneNumber)
        assertEquals("john.doe@airwallex.com", shipping.email)
        assertEquals("shipping", shipping.shippingMethod)
        assertEquals(
            Address(
                countryCode = "US",
                state = "CA",
                city = "San Francisco",
                street = "1460 Mission St.#02W101",
                postcode = "94103"
            ),
            shipping.address
        )
    }

    @Test
    fun testToParamMap() {
        val shippingParams = Shipping.Builder()
            .setFirstName("John")
            .setLastName("Doe")
            .setPhone("13800000000")
            .build()
            .toParamMap()
        assertEquals(
            mapOf(
                "first_name" to "John",
                "last_name" to "Doe",
                "phone_number" to "13800000000"
            ),
            shippingParams
        )
    }
}
