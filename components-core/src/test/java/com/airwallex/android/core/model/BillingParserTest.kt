package com.airwallex.android.core.model

import com.airwallex.android.core.model.parser.BillingParser
import org.json.JSONObject
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BillingParserTest {

    @Test
    fun testParseWithAllFields() {
        val billing = BillingParser().parse(
            JSONObject(
                """
                {
                    "first_name": "John",
                    "last_name": "Doe",
                    "phone_number": "+1234567890",
                    "email": "john.doe@example.com",
                    "date_of_birth": "1990-01-15",
                    "address": {
                        "country_code": "US",
                        "state": "CA",
                        "city": "San Francisco",
                        "street": "123 Main St",
                        "postcode": "94102"
                    }
                }
                """.trimIndent()
            )
        )

        assertEquals("John", billing.firstName)
        assertEquals("Doe", billing.lastName)
        assertEquals("+1234567890", billing.phone)
        assertEquals("john.doe@example.com", billing.email)
        assertEquals("1990-01-15", billing.dateOfBirth)
        assertNotNull(billing.address)
        assertEquals("US", billing.address?.countryCode)
    }

    @Test
    fun testParseWithAddressPresent() {
        // Line 10: Test ?.let true branch when address is present
        val billing = BillingParser().parse(
            JSONObject(
                """
                {
                    "first_name": "Jane",
                    "last_name": "Smith",
                    "address": {
                        "country_code": "AU",
                        "city": "Sydney",
                        "postcode": "2000"
                    }
                }
                """.trimIndent()
            )
        )

        assertEquals("Jane", billing.firstName)
        assertEquals("Smith", billing.lastName)
        assertNotNull(billing.address)
        assertEquals("AU", billing.address?.countryCode)
    }

    @Test
    fun testParseWithAddressNull() {
        // Line 10: Test ?.let false branch when address is missing
        val billing = BillingParser().parse(
            JSONObject(
                """
                {
                    "first_name": "Bob",
                    "last_name": "Johnson",
                    "email": "bob@example.com"
                }
                """.trimIndent()
            )
        )

        assertEquals("Bob", billing.firstName)
        assertEquals("Johnson", billing.lastName)
        assertEquals("bob@example.com", billing.email)
        assertNull(billing.address)
    }

    @Test
    fun testParseWithMinimalFields() {
        val billing = BillingParser().parse(
            JSONObject(
                """
                {
                    "email": "minimal@example.com"
                }
                """.trimIndent()
            )
        )

        assertNull(billing.firstName)
        assertNull(billing.lastName)
        assertNull(billing.phone)
        assertEquals("minimal@example.com", billing.email)
        assertNull(billing.dateOfBirth)
        assertNull(billing.address)
    }

    @Test
    fun testParseWithEmptyJson() {
        val billing = BillingParser().parse(JSONObject("{}"))

        assertNull(billing.firstName)
        assertNull(billing.lastName)
        assertNull(billing.phone)
        assertNull(billing.email)
        assertNull(billing.dateOfBirth)
        assertNull(billing.address)
    }
}
