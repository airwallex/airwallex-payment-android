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
                countryCode = "US",
                state = "CA",
                city = "San Francisco",
                street = "1460 Mission St.#02W101",
                postcode = "94103"
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
                    "country_code" to "US",
                    "state" to "CA",
                    "city" to "San Francisco",
                    "street" to "1460 Mission St.#02W101",
                    "postcode" to "94103"
                )
            ),
            paramMap
        )
    }
}
