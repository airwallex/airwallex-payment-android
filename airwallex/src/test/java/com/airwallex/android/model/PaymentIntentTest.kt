package com.airwallex.android.model

import com.airwallex.android.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.math.BigDecimal
import java.util.*
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class PaymentIntentTest {

    @Test
    fun builderConstructor() {
        val paymentIntent = PaymentIntent(
            id = "int_6hJ72Y7zich939UCz8j6BLkonH",
            requestId = "a750e597-c30e-4d2b-ad41-cac601a15b25",
            amount = BigDecimal.valueOf(100.01),
            currency = "USD",
            merchantOrderId = "cc9bfc13-ba30-483b-a62c-ee9250c9bfev",
            order = PurchaseOrder(
                type = "physical_goods"
            ),
            customerId = "cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu",
            descriptor = "Airwallex - T-shirt",
            status = PaymentIntentStatus.REQUIRES_PAYMENT_METHOD,
            capturedAmount = BigDecimal.valueOf(0),
            availablePaymentMethodTypes = arrayListOf("card", "wechatpay"),
            customerPaymentMethods = arrayListOf(
                PaymentMethod.Builder()
                    .setId("")
                    .setRequestId("")
                    .setType(PaymentMethodType.CARD)
                    .setCard(
                        PaymentMethod.Card.Builder()
                            .setExpiryMonth("12")
                            .setExpiryYear("2030")
                            .setName("John Doe")
                            .setBin("411111")
                            .setLast4("1111")
                            .setBrand("visa")
                            .setIssuerCountryCode("US")
                            .setCardType("credit")
                            .setFingerprint("7e9cceb282d05675fed72f67e0a4a5ae4e82ff5a96a1b0e55bc45cf63609a055")
                            .build()
                    )
                    .setBilling(
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
                    )
                    .build()

            ),
            clientSecret = "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            createdAt = Date(1585537417000),
            updatedAt = Date(1585537442000),
            latestPaymentAttempt = PaymentIntent.PaymentAttempt(
                id = "att_7P9rxcJzs06b3Bt7zLWArVk3xi",
                currency = null,
                paymentMethod = PaymentMethod.Builder()
                    .setId("mtd_4iyImkz7wglVXRad6hZWreqRJY0")
                    .setRequestId(null)
                    .setStatus(PaymentMethod.PaymentMethodStatus.VERIFIED)
                    .setType(PaymentMethodType.CARD)
                    .setCard(
                        PaymentMethod.Card.Builder()
                            .setExpiryMonth("01")
                            .setExpiryYear("2023")
                            .setName("Adam")
                            .setBin("520000")
                            .setLast4("1005")
                            .setBrand("mastercard")
                            .setIssuerCountryCode("MY")
                            .setCardType("credit")
                            .setFingerprint("290a1f394301fa8bd83be3f081a5d308d7f9fd89dd72c7c4108029dec75f72ae")
                            .setCvcCheck("unknown")
                            .setAvsCheck("unknown")
                            .build()
                    )
                    .setBilling(
                        Billing.Builder()
                            .setFirstName("Jim")
                            .setLastName("passlist")
                            .setDateOfBirth("2011-10-12")
                            .setEmail("jim631@sina.com")
                            .setPhone("1367875788")
                            .setAddress(
                                Address.Builder()
                                    .setCountryCode("CN")
                                    .setState("Beijing")
                                    .setCity("Shanghai")
                                    .setStreet("Pudong District")
                                    .setPostcode("33333")
                                    .build()
                            )
                            .build()
                    )
                    .setCreatedAt(Date(1585537440000))
                    .setUpdatedAt(Date(1585537440000))
                    .build(),
                status = PaymentIntent.PaymentAttemptStatus.SUCCEEDED,
                capturedAmount = BigDecimal.valueOf(0),
                refundedAmount = BigDecimal.valueOf(0),
                createdAt = Date(1585537440000),
                updatedAt = Date(1585537440000)
            )
        )
        assertEquals(paymentIntent, PaymentIntentFixtures.PAYMENT_INTENT)
    }

    @Test
    fun testParcelable() {
        assertEquals(
            PaymentMethodFixtures.PAYMENT_METHOD,
            ParcelUtils.create(PaymentMethodFixtures.PAYMENT_METHOD)
        )
    }
}
