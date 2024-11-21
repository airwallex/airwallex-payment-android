package com.airwallex.android.core.util

import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexRecurringWithIntentSession
import com.airwallex.android.core.AirwallexSession

object SessionUtils {

    fun getIntentId(session: AirwallexSession): String {
        return when (session) {
            is AirwallexPaymentSession -> {
                session.paymentIntent.id
            }

            is AirwallexRecurringWithIntentSession -> {
                session.paymentIntent.id
            }
            else -> ""
        }
    }
}
