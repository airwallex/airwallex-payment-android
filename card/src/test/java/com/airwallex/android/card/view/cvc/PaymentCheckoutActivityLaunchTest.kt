package com.airwallex.android.card.view.cvc

import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.model.PaymentMethod
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import java.util.Locale
import kotlin.test.assertEquals

class PaymentCheckoutActivityLaunchTest {

    @Test
    fun `args expose session locale tag`() {
        val session = mockk<AirwallexRecurringSession> {
            every { locale } returns Locale.CANADA_FRENCH
        }
        val args = PaymentCheckoutActivityLaunch.Args(
            recurringSession = session,
            paymentMethod = mockk<PaymentMethod>(),
            paymentConsent = null,
            cvc = null
        )

        assertEquals("fr-CA", args.localeTag)
    }
}
