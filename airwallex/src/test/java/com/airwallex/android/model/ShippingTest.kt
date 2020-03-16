package com.airwallex.android.model

import com.airwallex.android.ParcelUtils
import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ShippingTest {

    @Test
    fun builderConstructor() {
        val billing = Shipping.Builder()
            .setFirstName("John")
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
        assertEquals(billing, ShippingFixtures.SHIPPING)
    }

    @Test
    fun testParcelable() {
        assertEquals(ShippingFixtures.SHIPPING, ParcelUtils.create(ShippingFixtures.SHIPPING))
    }
}
