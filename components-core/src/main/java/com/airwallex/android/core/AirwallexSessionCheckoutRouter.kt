package com.airwallex.android.core

import com.airwallex.android.core.extension.convertToSession
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodType

/**
 * The outcome of routing a [checkout][Airwallex.checkout] call to the flow that can service it.
 * Each variant maps to exactly one execution path in [Airwallex].
 */
internal sealed interface AirwallexSessionCheckoutRoute {
    /** A saved card paid through the API with a PAN consent: CVC must be captured first. */
    object CvcRequired : AirwallexSessionCheckoutRoute

    /** Recurring sessions and non-card/Google Pay methods run on the legacy flow. */
    object OldFlow : AirwallexSessionCheckoutRoute

    /** Card / Google Pay on a session that resolves to a unified [Session]. */
    data class NewFlow(val unifiedSession: Session) : AirwallexSessionCheckoutRoute

    /** A session that cannot be resolved to any known flow. */
    data class UnknownSession(val session: AirwallexSession) : AirwallexSessionCheckoutRoute
}

/**
 * Pure routing decision for [Airwallex.checkout] on the [AirwallexSession] family. Everything the
 * decision depends on is passed in, so it can be exercised without an activity, analytics globals,
 * or payment infrastructure.
 */
internal class AirwallexSessionCheckoutRouter {

    fun route(
        session: AirwallexSession,
        paymentMethod: PaymentMethod,
        paymentConsent: PaymentConsent?,
        launchType: String?,
        isAirwallexUIActivity: Boolean,
    ): AirwallexSessionCheckoutRoute {
        if (isCheckoutApiWithCvc(paymentMethod, paymentConsent, launchType, isAirwallexUIActivity)) {
            return AirwallexSessionCheckoutRoute.CvcRequired
        }

        val isCardOrGooglePay = paymentMethod.type == PaymentMethodType.GOOGLEPAY.value ||
            paymentMethod.type == PaymentMethodType.CARD.value
        if (session is AirwallexRecurringSession || !isCardOrGooglePay) {
            return AirwallexSessionCheckoutRoute.OldFlow
        }

        return toUnifiedSession(session)
            ?.let { AirwallexSessionCheckoutRoute.NewFlow(it) }
            ?: AirwallexSessionCheckoutRoute.UnknownSession(session)
    }

    private fun isCheckoutApiWithCvc(
        paymentMethod: PaymentMethod,
        paymentConsent: PaymentConsent?,
        launchType: String?,
        isAirwallexUIActivity: Boolean,
    ): Boolean {
        val isFromApi = !isAirwallexUIActivity && launchType == AnalyticsLogger.LaunchType.API
        return paymentConsent != null &&
            paymentMethod.card?.numberType == PaymentMethod.Card.NumberType.PAN &&
            isFromApi
    }

    private fun toUnifiedSession(session: AirwallexSession): Session? {
        return session as? Session
            ?: when (session) {
                is AirwallexPaymentSession -> session.convertToSession()
                is AirwallexRecurringWithIntentSession -> session.convertToSession()
                else -> null
            }
    }
}
