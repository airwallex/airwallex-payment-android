package com.airwallex.android.model

import com.airwallex.android.ParcelUtils
import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.math.BigDecimal

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
            capturedAmount = 0f,
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
            clientSecret = "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ=="
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
