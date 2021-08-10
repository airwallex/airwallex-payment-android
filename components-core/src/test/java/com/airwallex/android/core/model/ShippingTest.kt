package com.airwallex.android.core.model

import com.airwallex.android.core.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

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
