package com.airwallex.android.core.model

import org.junit.Test
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RetrieveLocalizedParamsTest {

    @Test
    fun `bank params store optional locale`() {
        val params = RetrieveBankParams.Builder(
            clientSecret = "secret",
            paymentMethodType = "online_banking"
        )
            .setLocale(Locale.FRANCE)
            .build()

        assertEquals(Locale.FRANCE, params.locale)
    }

    @Test
    fun `payment method type info params default locale to null`() {
        val params = RetrievePaymentMethodTypeInfoParams.Builder(
            clientSecret = "secret",
            paymentMethodType = "card"
        ).build()

        assertNull(params.locale)
    }

    @Test
    fun `bank params fall back to null for malformed locale`() {
        val params = RetrieveBankParams.Builder(
            clientSecret = "secret",
            paymentMethodType = "online_banking"
        )
            .setLocale(Locale("en-US"))
            .build()

        assertNull(params.locale)
    }

    @Test
    fun `payment method type info params fall back to null for malformed locale`() {
        val params = RetrievePaymentMethodTypeInfoParams.Builder(
            clientSecret = "secret",
            paymentMethodType = "card"
        )
            .setLocale(Locale("en-US"))
            .build()

        assertNull(params.locale)
    }
}
