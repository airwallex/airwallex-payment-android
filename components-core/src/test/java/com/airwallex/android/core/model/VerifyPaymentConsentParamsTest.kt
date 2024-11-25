package com.airwallex.android.core.model

import com.airwallex.android.core.model.VerifyPaymentConsentParams.Companion.createParamsByMethodType
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class VerifyPaymentConsentParamsTest {

    private val params = VerifyPaymentConsentParams.Builder(
        clientSecret = "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MjkyODExOTQsImV4cCI6MTYyOTI4NDc5NCwiYWNjb3VudF9pZCI6Ijc3Yjg4NjlhLTgyZGMtNGNmYy05YzQ5LTg1YWM1MjAyN2M4YiIsImRhdGFfY2VudGVyX3JlZ2lvbiI6IkhLIiwiY29uc2VudF9pZCI6ImNzdF9oa2Rtcjd2OXJnMWo1ZzRhenk2In0.VHVT12FqDQjeOiRmmOtKVAl3XLiMJWkWDiMzr7QFQHA",
        paymentConsentId = "cst_hkdmr7v9rg1j5g4azy6",
        paymentMethodType = "card"
    )
        .setAmount(BigDecimal.valueOf(1))
        .setCurrency("HKD")
        .setCvc("123")
        .setReturnUrl("airwallexcheckou://com.airwallex.paymentacceptance")
        .build()

    @Test
    fun testParams() {
        assertEquals(
            "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MjkyODExOTQsImV4cCI6MTYyOTI4NDc5NCwiYWNjb3VudF9pZCI6Ijc3Yjg4NjlhLTgyZGMtNGNmYy05YzQ5LTg1YWM1MjAyN2M4YiIsImRhdGFfY2VudGVyX3JlZ2lvbiI6IkhLIiwiY29uc2VudF9pZCI6ImNzdF9oa2Rtcjd2OXJnMWo1ZzRhenk2In0.VHVT12FqDQjeOiRmmOtKVAl3XLiMJWkWDiMzr7QFQHA",
            params.clientSecret
        )
        assertEquals("cst_hkdmr7v9rg1j5g4azy6", params.paymentConsentId)
        assertEquals("card", params.paymentMethodType)
        assertEquals(BigDecimal.valueOf(1), params.amount)
        assertEquals("HKD", params.currency)
        assertEquals("airwallexcheckou://com.airwallex.paymentacceptance", params.returnUrl)
        assertEquals("123", params.cvc)
    }

    @Test
    fun `test createParamsByMethodType with CARD`() {
        val paymentMethodType = PaymentMethodType.CARD.value
        val clientSecret = "testSecret"
        val paymentConsentId = "testConsentId"
        val amount = BigDecimal(100.00)
        val currency = "USD"
        val cvc = "123"
        val returnUrl = "http://return.url"

        val result = createParamsByMethodType(
            paymentMethodType = paymentMethodType,
            clientSecret = clientSecret,
            paymentConsentId = paymentConsentId,
            amount = amount,
            currency = currency,
            cvc = cvc,
            returnUrl = returnUrl
        )

        assertNotNull(result)
        assertEquals(paymentMethodType, result.paymentMethodType)
        assertEquals(clientSecret, result.clientSecret)
        assertEquals(paymentConsentId, result.paymentConsentId)
        assertEquals(amount, result.amount)
        assertEquals(currency, result.currency)
        assertEquals(cvc, result.cvc)
        assertEquals(returnUrl, result.returnUrl)
    }

    @Test
    fun `test createParamsByMethodType with GOOGLEPAY`() {
        val paymentMethodType = PaymentMethodType.GOOGLEPAY.value
        val clientSecret = "testSecret"
        val paymentConsentId = "testConsentId"
        val amount = BigDecimal(50.00)
        val currency = "EUR"
        val returnUrl = "http://return.url"

        val result = createParamsByMethodType(
            paymentMethodType = paymentMethodType,
            clientSecret = clientSecret,
            paymentConsentId = paymentConsentId,
            amount = amount,
            currency = currency,
            cvc = null,
            returnUrl = returnUrl
        )

        assertNotNull(result)
        assertEquals(paymentMethodType, result.paymentMethodType)
        assertEquals(clientSecret, result.clientSecret)
        assertEquals(paymentConsentId, result.paymentConsentId)
        assertEquals(amount, result.amount)
        assertEquals(currency, result.currency)
        assertNull(result.cvc)
        assertEquals(returnUrl, result.returnUrl)
    }

    @Test
    fun `test createParamsByMethodType with Redirect`() {
        val paymentMethodType = "Alipay"
        val clientSecret = "testSecret"
        val paymentConsentId = "testConsentId"
        val returnUrl = "http://return.url"

        val result = createParamsByMethodType(
            paymentMethodType = paymentMethodType,
            clientSecret = clientSecret,
            paymentConsentId = paymentConsentId,
            amount = null,
            currency = null,
            cvc = null,
            returnUrl = returnUrl
        )

        assertNotNull(result)
        assertEquals(paymentMethodType, result.paymentMethodType)
        assertEquals(clientSecret, result.clientSecret)
        assertEquals(paymentConsentId, result.paymentConsentId)
        assertNull(result.amount)
        assertNull(result.currency)
        assertNull(result.cvc)
        assertEquals(returnUrl, result.returnUrl)
    }
}
