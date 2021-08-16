package com.airwallex.android.view

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.core.*
import com.airwallex.android.core.model.*
import com.airwallex.android.view.AddPaymentMethodViewModel.PaymentMethodResult.Error
import org.junit.Test
import java.math.BigDecimal
import java.util.*
import kotlin.test.assertEquals
import com.nhaarman.mockitokotlin2.mock
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch
import kotlin.test.BeforeTest

@RunWith(RobolectricTestRunner::class)
class AddPaymentMethodViewModelTest {

    private val application = ApplicationProvider.getApplicationContext<Application>()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val paymentIntent = PaymentIntent(
        id = "int_6hJ72Y7zich939UCz8j6BLkonH",
        requestId = "a750e597-c30e-4d2b-ad41-cac601a15b25",
        amount = BigDecimal.valueOf(100.01),
        currency = "AUD",
        merchantOrderId = "cc9bfc13-ba30-483b-a62c-ee9250c9bfev",
        order = PurchaseOrder(
            type = "physical_goods"
        ),
        customerId = "cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu",
        descriptor = "Airwallex - T-shirt",
        status = PaymentIntentStatus.REQUIRES_PAYMENT_METHOD,
        capturedAmount = BigDecimal.valueOf(0.1),
        availablePaymentMethodTypes = arrayListOf(PaymentMethodType.CARD, PaymentMethodType.WECHAT),
        customerPaymentMethods = arrayListOf(
            PaymentMethod.Builder()
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
            capturedAmount = BigDecimal.valueOf(0.1),
            refundedAmount = BigDecimal.valueOf(0.1),
            createdAt = Date(1585537440000),
            updatedAt = Date(1585537440000),
            amount = BigDecimal.valueOf(0.1),
            authenticationData = PaymentIntent.PaymentAttemptAuthData(null, null, null, null)
        )
    )

    private val addPaymentMethodViewModel by lazy {
        val activity: Activity = mock()
        AddPaymentMethodViewModel(
            application, Airwallex(activity, context),
            AirwallexPaymentSession.Builder(paymentIntent)
                .setReturnUrl("airwallexcheckout://com.airwallex.android")
                .build()
        )
    }

    @BeforeTest
    fun setup() {
        Airwallex.initialize(
            AirwallexConfiguration.Builder()
                .enableLogging(true)
                .build()
        )

        ClientSecretRepository.init(object : ClientSecretProvider {
            override fun createClientSecret(
                customerId: String,
                updateListener: ClientSecretUpdateListener
            ) {
                updateListener.onClientSecretUpdateFailure("111")
            }
        })
    }

    @Test
    fun createPaymentMethodTest() {
        val countDownLatch = CountDownLatch(1)
        var throwable: Throwable? = null
        addPaymentMethodViewModel.createPaymentMethod(
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
                .build(),
            null
        ).observeForever {
            when (it) {
                is Error -> throwable = it.exception
                else -> Unit
            }
            countDownLatch.countDown()
        }

        countDownLatch.await()
        assertEquals("111", throwable?.message)
    }
}
