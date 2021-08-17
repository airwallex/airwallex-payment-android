package com.airwallex.android.core.model

import com.airwallex.android.core.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class AddressTest {

    private val address by lazy {
        Address.Builder()
            .setCountryCode("CN")
            .setState("Shanghai")
            .setCity("Shanghai")
            .setStreet("Pudong District")
            .setPostcode("100000")
            .build()
    }

    @Test
    fun builderConstructor() {
        assertEquals(address, AddressFixtures.ADDRESS)
    }

    @Test
    fun testParcelable() {
        assertEquals(AddressFixtures.ADDRESS, ParcelUtils.create(AddressFixtures.ADDRESS))
    }

    @Test
    fun testParams() {
        assertEquals("CN", address.countryCode)
        assertEquals("Shanghai", address.state)
        assertEquals("Shanghai", address.city)
        assertEquals("Pudong District", address.street)
        assertEquals("100000", address.postcode)
    }
}
