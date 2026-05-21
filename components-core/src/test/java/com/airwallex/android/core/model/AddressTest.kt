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
            .setCountryCode("US")
            .setState("CA")
            .setCity("San Francisco")
            .setStreet("1460 Mission St.#02W101")
            .setPostcode("94103")
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
        assertEquals("US", address.countryCode)
        assertEquals("CA", address.state)
        assertEquals("San Francisco", address.city)
        assertEquals("1460 Mission St.#02W101", address.street)
        assertEquals("94103", address.postcode)
    }

    @Test
    fun testToParamMap() {
        val addressParams = Address.Builder()
            .setCountryCode("US")
            .build()
            .toParamMap()
        assertEquals(mapOf("country_code" to "US"), addressParams)
    }
}
