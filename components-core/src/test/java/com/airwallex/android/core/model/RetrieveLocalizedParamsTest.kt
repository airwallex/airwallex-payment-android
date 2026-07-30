package com.airwallex.android.core.model

import org.junit.Test
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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
    fun `bank params reject malformed locale`() {
        assertFailsWith<IllegalArgumentException> {
            RetrieveBankParams.Builder(
                clientSecret = "secret",
                paymentMethodType = "online_banking"
            )
                .setLocale(Locale("en-US"))
                .build()
        }
    }

    @Test
    fun `payment method type info params reject malformed locale`() {
        assertFailsWith<IllegalArgumentException> {
            RetrievePaymentMethodTypeInfoParams.Builder(
                clientSecret = "secret",
                paymentMethodType = "card"
            )
                .setLocale(Locale("en-US"))
                .build()
        }
    }
}
