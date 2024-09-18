package com.airwallex.android.view

import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.view.util.findWithType
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AvailablePaymentMethodExtensionsTest {

    @Test
    fun `test find with type`() {
        val cardType = mockk<AvailablePaymentMethodType>()
        every { cardType.name } returns PaymentMethodType.CARD.value
        val list = listOf(cardType)

        assertEquals(list.findWithType(PaymentMethodType.CARD), cardType)
        assertNull(list.findWithType(PaymentMethodType.REDIRECT))
    }

    @Test
    fun `test hasSinglePaymentMethod returns true`() {
        val availablePaymentMethodJson = """
        {
            "name": "Test Method",
            "displayName": "Test Method Display"
        }
        """.trimIndent()

        val availablePaymentMethod = parseAvailablePaymentMethodType(availablePaymentMethodJson)

        val paymentMethods = listOfNotNull(availablePaymentMethod)
        val consents = emptyList<PaymentConsent>()

        val result = paymentMethods.hasSinglePaymentMethod(availablePaymentMethod, consents)
        assertTrue(result)
    }

    @Test
    fun `test hasSinglePaymentMethod returns false when desiredPaymentMethodType is null`() {
        val availablePaymentMethodJson = """
        {
            "name": "Test Method",
            "displayName": "Test Method Display"
        }
        """.trimIndent()

        val availablePaymentMethod = parseAvailablePaymentMethodType(availablePaymentMethodJson)

        val paymentConsentJson = """
        {
            "id": "test_consent_id"
        }
        """.trimIndent()

        val paymentConsent = parsePaymentConsent(paymentConsentJson)

        val paymentMethods = listOfNotNull(availablePaymentMethod)
        val consents = listOfNotNull(paymentConsent)

        val result = paymentMethods.hasSinglePaymentMethod(null, consents)
        assertFalse(result)
    }

    @Test
    fun `test hasSinglePaymentMethod returns false when consents is not empty`() {
        val availablePaymentMethodJson = """
        {
            "name": "Test Method",
            "displayName": "Test Method Display"
        }
        """.trimIndent()

        val availablePaymentMethod = parseAvailablePaymentMethodType(availablePaymentMethodJson)

        val paymentConsentJson = """
        {
            "id": "test_consent_id"
        }
        """.trimIndent()

        val paymentConsent = parsePaymentConsent(paymentConsentJson)

        val paymentMethods = listOfNotNull(availablePaymentMethod)
        val consents = listOfNotNull(paymentConsent)

        val result = paymentMethods.hasSinglePaymentMethod(availablePaymentMethod, consents)
        assertFalse(result)
    }

    @Test
    fun `test hasSinglePaymentMethod returns false when no payment methods available`() {
        val availablePaymentMethodJson = """
        {
            "name": "Test Method",
            "displayName": "Test Method Display"
        }
        """.trimIndent()

        val availablePaymentMethod = parseAvailablePaymentMethodType(availablePaymentMethodJson)

        val paymentMethods = emptyList<AvailablePaymentMethodType>()
        val consents = emptyList<PaymentConsent>()

        val result = paymentMethods.hasSinglePaymentMethod(availablePaymentMethod, consents)
        assertFalse(result)
    }

    private fun List<AvailablePaymentMethodType>.hasSinglePaymentMethod(
        desiredPaymentMethodType: AvailablePaymentMethodType?,
        consents: List<PaymentConsent>
    ): Boolean {
        if (desiredPaymentMethodType == null) return false

        val hasPaymentConsents = consents.isNotEmpty()
        val availablePaymentMethodsSize = this.size

        return !hasPaymentConsents && availablePaymentMethodsSize == 1
    }

    private fun parseAvailablePaymentMethodType(json: String): AvailablePaymentMethodType? {
        val gson: Gson = GsonBuilder().create()
        return gson.fromJson(json, AvailablePaymentMethodType::class.java)
    }

    private fun parsePaymentConsent(json: String): PaymentConsent? {
        val gson: Gson = GsonBuilder().create()
        return gson.fromJson(json, PaymentConsent::class.java)
    }
}