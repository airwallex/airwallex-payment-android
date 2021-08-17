package com.airwallex.android.core.model

import com.airwallex.android.core.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class BillingTest {

    private val billing by lazy {
        Billing.Builder()
            .setFirstName("John")
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
    }

    @Test
    fun builderConstructor() {
        assertEquals(billing, BillingFixtures.BILLING)
    }

    @Test
    fun testParcelable() {
        assertEquals(BillingFixtures.BILLING, ParcelUtils.create(BillingFixtures.BILLING))
    }

    @Test
    fun testParams() {
        assertEquals("John", billing.firstName)
        assertEquals("Doe", billing.lastName)
        assertEquals("13800000000", billing.phone)
        assertEquals(
            Address(
                countryCode = "CN",
                state = "Shanghai",
                city = "Shanghai",
                street = "Pudong District",
                postcode = "100000"
            ),
            billing.address
        )
    }
}
