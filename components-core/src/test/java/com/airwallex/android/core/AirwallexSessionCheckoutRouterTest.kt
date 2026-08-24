package com.airwallex.android.core

import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentIntentFixtures
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.model.Shipping
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AirwallexSessionCheckoutRouterTest {

    private val router = AirwallexSessionCheckoutRouter()
    private val testPaymentIntent = PaymentIntentFixtures.PAYMENT_INTENT

    private fun route(
        session: AirwallexSession,
        paymentMethod: PaymentMethod,
        paymentConsent: PaymentConsent? = null,
        launchType: String? = null,
        isAirwallexUIActivity: Boolean = false,
    ): AirwallexSessionCheckoutRoute = router.route(
        session = session,
        paymentMethod = paymentMethod,
        paymentConsent = paymentConsent,
        launchType = launchType,
        isAirwallexUIActivity = isAirwallexUIActivity,
    )

    private fun cardMethod(numberType: PaymentMethod.Card.NumberType? = null): PaymentMethod =
        PaymentMethod.Builder()
            .setType(PaymentMethodType.CARD.value)
            .setCard(PaymentMethod.Card.Builder().setNumberType(numberType).build())
            .build()

    private fun googlePayMethod(): PaymentMethod =
        PaymentMethod.Builder().setType(PaymentMethodType.GOOGLEPAY.value).build()

    private fun lpmMethod(): PaymentMethod =
        PaymentMethod.Builder().setType("alipaycn").build()

    private fun paymentSession(paymentIntent: PaymentIntent = testPaymentIntent): AirwallexPaymentSession =
        AirwallexPaymentSession.Builder(paymentIntent = paymentIntent, countryCode = "US").build()

    private fun recurringSession(): AirwallexRecurringSession =
        AirwallexRecurringSession.Builder(
            nextTriggerBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
            customerId = "test_customer",
            currency = "USD",
            amount = BigDecimal.valueOf(100),
            countryCode = "US",
            clientSecret = "test_client_secret",
        ).build()

    private fun recurringWithIntentSession(paymentIntent: PaymentIntent = testPaymentIntent): AirwallexRecurringWithIntentSession =
        AirwallexRecurringWithIntentSession.Builder(
            paymentIntent = paymentIntent,
            customerId = "test_customer",
            nextTriggerBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
            countryCode = "US",
        ).build()

    private fun unifiedSession(paymentIntent: PaymentIntent = testPaymentIntent): Session =
        Session.Builder(paymentIntent = paymentIntent, countryCode = "US").build()

    @Test
    fun `recurring session routes to old flow`() {
        assertEquals(AirwallexSessionCheckoutRoute.OldFlow, route(recurringSession(), cardMethod()))
    }

    @Test
    fun `non card or google pay method routes to old flow`() {
        assertEquals(AirwallexSessionCheckoutRoute.OldFlow, route(unifiedSession(), lpmMethod()))
    }

    @Test
    fun `card on unified session routes to new flow with same session`() {
        val session = unifiedSession()
        val result = assertIs<AirwallexSessionCheckoutRoute.NewFlow>(route(session, cardMethod()))
        assertEquals(session, result.unifiedSession)
    }

    @Test
    fun `card on payment session routes to new flow`() {
        assertIs<AirwallexSessionCheckoutRoute.NewFlow>(route(paymentSession(), cardMethod()))
    }

    @Test
    fun `card on recurring with intent session routes to new flow`() {
        assertIs<AirwallexSessionCheckoutRoute.NewFlow>(route(recurringWithIntentSession(), cardMethod()))
    }

    @Test
    fun `google pay on unified session routes to new flow`() {
        assertIs<AirwallexSessionCheckoutRoute.NewFlow>(route(unifiedSession(), googlePayMethod()))
    }

    @Test
    fun `api saved card with PAN consent routes to cvc required`() {
        val result = route(
            session = unifiedSession(),
            paymentMethod = cardMethod(PaymentMethod.Card.NumberType.PAN),
            paymentConsent = PaymentConsent(id = "123"),
            launchType = AnalyticsLogger.LaunchType.API,
            isAirwallexUIActivity = false,
        )
        assertEquals(AirwallexSessionCheckoutRoute.CvcRequired, result)
    }

    @Test
    fun `does not route to cvc required when launched from airwallex ui activity`() {
        val result = route(
            session = unifiedSession(),
            paymentMethod = cardMethod(PaymentMethod.Card.NumberType.PAN),
            paymentConsent = PaymentConsent(id = "123"),
            launchType = AnalyticsLogger.LaunchType.API,
            isAirwallexUIActivity = true,
        )
        assertIs<AirwallexSessionCheckoutRoute.NewFlow>(result)
    }

    @Test
    fun `does not route to cvc required when launch type is not api`() {
        val result = route(
            session = unifiedSession(),
            paymentMethod = cardMethod(PaymentMethod.Card.NumberType.PAN),
            paymentConsent = PaymentConsent(id = "123"),
            launchType = null,
            isAirwallexUIActivity = false,
        )
        assertIs<AirwallexSessionCheckoutRoute.NewFlow>(result)
    }

    @Test
    @Suppress("DEPRECATION")
    fun `unconvertible session routes to unknown session`() {
        val session = object : AirwallexSession() {
            override val customerId: String? = null
            override val shipping: Shipping? = null
            override val isBillingInformationRequired: Boolean = false
            override val isEmailRequired: Boolean = false
            override val currency: String = "USD"
            override val countryCode: String = "US"
            override val amount: BigDecimal = BigDecimal.ZERO
            override val returnUrl: String? = null
            override val googlePayOptions: GooglePayOptions? = null
            override val paymentMethods: List<String>? = null
            override val clientSecret: String? = null
        }
        val result = assertIs<AirwallexSessionCheckoutRoute.UnknownSession>(route(session, cardMethod()))
        assertEquals(session, result.session)
    }
}
