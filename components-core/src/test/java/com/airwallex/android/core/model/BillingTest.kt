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

    @Test
    fun testToParamMap() {
        val paramMap = billing.toParamMap()
        assertEquals(
            mapOf(
                "first_name" to "John",
                "last_name" to "Doe",
                "phone_number" to "13800000000",

                "email" to "john.doe@airwallex.com",
                "address" to mapOf(
                    "country_code" to "CN",
                    "state" to "Shanghai",
                    "city" to "Shanghai",
                    "street" to "Pudong District",
                    "postcode" to "100000"
                )
            ),
            paramMap
        )
    }
}
