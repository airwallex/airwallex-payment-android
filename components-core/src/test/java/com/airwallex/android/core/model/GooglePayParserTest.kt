package com.airwallex.android.core.model

import com.airwallex.android.core.model.parser.PaymentMethodParser
import org.json.JSONObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GooglePayParserTest {
    @Test
    fun `parse should return GooglePay object with correct fields`() {
        val jsonString = """
            {
                "payment_data_type": "some_payment_type",
                "encrypted_payment_token": "some_encrypted_token",
                "billing": {
                    "first_name": "John",
                    "last_name": "Doe",
                    "phone_number": "1234567890",
                    "email": "john.doe@example.com",
                    "date_of_birth": "1980-01-01",
                    "address": {
                        "country_code": "US",
                        "state": "CA",
                        "city": "San Francisco",
                        "street": "123 Market St",
                        "postcode": "94105"
                    }
                }
            }
        """

        val json = JSONObject(jsonString)

        val parser = PaymentMethodParser.GooglePayParser()

        val result = parser.parse(json)

        assertNotNull(result)
        assertEquals("some_payment_type", result.paymentDataType)
        assertEquals("some_encrypted_token", result.encryptedPaymentToken)
        assertNotNull(result.billing)

        val billing = result.billing
        assertEquals("John", billing?.firstName)
        assertEquals("Doe", billing?.lastName)
        assertEquals("1234567890", billing?.phone)
        assertEquals("john.doe@example.com", billing?.email)
        assertEquals("1980-01-01", billing?.dateOfBirth)

        val address = billing?.address
        assertNotNull(address)
        assertEquals("US", address.countryCode)
        assertEquals("CA", address.state)
        assertEquals("San Francisco", address.city)
        assertEquals("123 Market St", address.street)
        assertEquals("94105", address.postcode)
    }
}