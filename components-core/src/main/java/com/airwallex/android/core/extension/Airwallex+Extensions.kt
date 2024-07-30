package com.airwallex.android.core.extension

import androidx.annotation.UiThread
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.AirwallexRecurringWithIntentSession
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.Billing
import com.airwallex.android.core.model.CreatePaymentMethodParams
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodType

@UiThread
fun Airwallex.createCardPaymentMethod(
    session: AirwallexSession,
    card: PaymentMethod.Card,
    billing: Billing?,
    saveCard: Boolean,
    listener: Airwallex.PaymentListener<PaymentMethod>
) {
    val resolvedBilling = if (session.isBillingInformationRequired) billing else null

    fun createPaymentMethod(clientSecret: String) {
        createPaymentMethod(
            CreatePaymentMethodParams(
                clientSecret = clientSecret,
                customerId = requireNotNull(session.customerId),
                card = card,
                billing = resolvedBilling
            ),
            object : Airwallex.PaymentListener<PaymentMethod> {
                override fun onSuccess(response: PaymentMethod) {
                    listener.onSuccess(response)
                }

                override fun onFailed(exception: AirwallexException) {
                    listener.onFailed(exception)
                }
            }
        )
    }

    if (session is AirwallexPaymentSession && !saveCard) {
        listener.onSuccess(
            PaymentMethod.Builder()
                .setType(PaymentMethodType.CARD.value)
                .setCard(card)
                .setBilling(resolvedBilling)
                .build()
        )
    } else {
        val clientSecret = when (session) {
            is AirwallexRecurringSession -> session.clientSecret
            is AirwallexPaymentSession -> session.paymentIntent.clientSecret
            is AirwallexRecurringWithIntentSession -> session.paymentIntent.clientSecret
            else -> null
        }
        createPaymentMethod(requireNotNull(clientSecret))
    }
}