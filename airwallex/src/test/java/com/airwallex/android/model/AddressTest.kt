package com.airwallex.android.model

import com.airwallex.android.ParcelUtils
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AddressTest {

    @Test
    fun builderConstructor() {
        val address = Address.Builder()
            .setCountryCode("CN")
            .setState("Shanghai")
            .setCity("Shanghai")
            .setStreet("Pudong District")
            .setPostcode("100000")
            .build()
        assertEquals(address, AddressFixtures.ADDRESS)
    }

    @Test
    fun testParcelable() {
        assertEquals(AddressFixtures.ADDRESS, ParcelUtils.create(AddressFixtures.ADDRESS))
    }

}