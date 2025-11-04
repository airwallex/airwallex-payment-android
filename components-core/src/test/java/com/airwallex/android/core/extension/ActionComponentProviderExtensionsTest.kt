package com.airwallex.android.core.extension

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import com.airwallex.android.core.*
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.Billing
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.parser.PaymentIntentParser
import io.mockk.*
import org.json.JSONObject
import org.junit.Test

class ActionComponentProviderExtensionsTest {
    private val actionComponent = spyk<ActionComponent>()
    private val fragment = mockk<Fragment>()
    private val activity = mockk<Activity>()
    private val paymentManager = mockk<PaymentManager>(relaxed = true)
    private val context = mockk<Context>()
    private val billing = Billing(
        firstName = "John",
        lastName = "Citizen"
    )
    private val googlePay = PaymentMethod.GooglePay.Builder()
        .setBilling(billing)
        .setPaymentDataType("payment_data_type")
        .setEncryptedPaymentToken("encrypted_payment_token")
        .build()

    @Test
    fun `test confirmGooglePayIntent with next action`() {
        val paymentIntent = mockk<PaymentIntent>(relaxed = true)
        val paymentIntentListener = slot<Airwallex.PaymentListener<PaymentIntent>>()
        val listener = mockk<Airwallex.PaymentResultListener>()
        every { paymentManager.startOperation(any(), capture(paymentIntentListener)) } answers {
            paymentIntentListener.captured.onSuccess(paymentIntent)
        }

        actionComponent.confirmGooglePayIntent(
            fragment = fragment,
            activityProvider = { activity },
            paymentManager = paymentManager,
            applicationContext = context,
            paymentIntentId = "paymentIntent id",
            clientSecret = "secret",
            googlePay = googlePay,
            autoCapture = true,
            listener = listener
        )

        verify {
            actionComponent.handlePaymentIntentResponse(
                paymentIntent.id,
                paymentIntent.nextAction,
                fragment,
                activity,
                context,
                any(),
                listener,
                any()
            )
        }
    }

    @Test
    fun `test confirmGooglePayIntent without next action`() {
        val noActionPaymentIntent = PaymentIntentParser().parse(
            JSONObject(
                """
            {
                "id": "int_6hJ72Y7zich939UCz8j6BLkonH",
                "amount": 100.01,
                "currency": "AUD"
            }
                """.trimIndent()
            )
        )
        val paymentIntentListener = slot<Airwallex.PaymentListener<PaymentIntent>>()
        val listener = mockk<Airwallex.PaymentResultListener>(relaxed = true)
        every { paymentManager.startOperation(any(), capture(paymentIntentListener)) } answers {
            paymentIntentListener.captured.onSuccess(noActionPaymentIntent)
        }

        actionComponent.confirmGooglePayIntent(
            fragment = fragment,
            activityProvider = { activity },
            paymentManager = paymentManager,
            applicationContext = context,
            paymentIntentId = "paymentIntent id",
            clientSecret = "secret",
            googlePay = googlePay,
            autoCapture = true,
            listener = listener
        )

        verify { listener.onCompleted(AirwallexPaymentStatus.Success(noActionPaymentIntent.id)) }
    }

    @Test
    fun `test confirmGooglePayIntent when request fails`() {
        val paymentIntentListener = slot<Airwallex.PaymentListener<PaymentIntent>>()
        val listener = mockk<Airwallex.PaymentResultListener>(relaxed = true)
        val exception = mockk<AirwallexException>()
        every { paymentManager.startOperation(any(), capture(paymentIntentListener)) } answers {
            paymentIntentListener.captured.onFailed(exception)
        }

        actionComponent.confirmGooglePayIntent(
            fragment = fragment,
            activityProvider = { activity },
            paymentManager = paymentManager,
            applicationContext = context,
            paymentIntentId = "paymentIntent id",
            clientSecret = "secret",
            googlePay = googlePay,
            autoCapture = true,
            listener = listener
        )

        verify { listener.onCompleted(AirwallexPaymentStatus.Failure(exception)) }
    }
}