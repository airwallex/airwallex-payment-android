package com.airwallex.android.googlepay

import com.airwallex.android.core.BillingAddressParameters
import com.airwallex.android.core.GooglePayOptions
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class PaymentsUtilTest {
    @Test
    fun `test isReadyToPayRequest without supported card schemes`() {
        val request = PaymentsUtil.isReadyToPayRequest(
            GooglePayOptions(merchantId = "merchantId"), null
        )
        assertEquals(
            request.toString(),
            "{\"apiVersionMinor\":0,\"apiVersion\":2,\"allowedPaymentMethods\":" +
                "[{\"type\":\"CARD\",\"parameters\":{\"allowedAuthMethods\":[\"PAN_ONLY\"," +
                "\"CRYPTOGRAM_3DS\"],\"allowedCardNetworks\":[\"MASTERCARD\",\"VISA\"]}}]}"
        )
    }

    @Test
    fun `test isReadyToPayRequest with supported card schemes`() {
        val request = PaymentsUtil.isReadyToPayRequest(
            GooglePayOptions(merchantId = "merchantId"),
            listOf("MASTERCARD")
        )
        assertEquals(
            request.toString(),
            "{\"apiVersionMinor\":0,\"apiVersion\":2,\"allowedPaymentMethods\":" +
                "[{\"type\":\"CARD\",\"parameters\":{\"allowedAuthMethods\":[\"PAN_ONLY\"," +
                "\"CRYPTOGRAM_3DS\"],\"allowedCardNetworks\":[\"MASTERCARD\"]}}]}"
        )
    }

    @Test
    fun `test isReadyToPayRequest with billing address params`() {
        val request = PaymentsUtil.isReadyToPayRequest(
            GooglePayOptions(
                merchantId = "merchantId",
                billingAddressRequired = true,
                billingAddressParameters = BillingAddressParameters(
                    BillingAddressParameters.Format.FULL, true
                )
            ),
            null
        )
        assertEquals(
            request.toString(),
            "{\"apiVersionMinor\":0,\"apiVersion\":2,\"allowedPaymentMethods\":" +
                "[{\"type\":\"CARD\",\"parameters\":{\"allowedAuthMethods\":" +
                "[\"PAN_ONLY\",\"CRYPTOGRAM_3DS\"],\"billingAddressRequired\":true," +
                "\"billingAddressParameters\":{\"format\":\"FULL\",\"phoneNumberRequired\":" +
                "true},\"allowedCardNetworks\":[\"MASTERCARD\",\"VISA\"]}}]}"
        )
    }
}
