package com.airwallex.android.view

import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.view.composables.PaymentElementConfiguration
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import java.util.Locale
import kotlin.test.assertEquals

class ActivityLaunchLocaleTest {

    @Test
    fun `payment methods args expose session locale tag`() {
        val session = localizedSession()
        val args = PaymentMethodsActivityLaunch.Args(
            recurringSession = session,
            configuration = mockk<PaymentElementConfiguration.PaymentSheet>()
        )

        assertEquals("zh-Hant-HK", args.localeTag)
    }

    @Test
    fun `add payment method args expose session locale tag`() {
        val session = localizedSession()
        val args = AddPaymentMethodActivityLaunch.Args(
            recurringSession = session,
            configuration = mockk<PaymentElementConfiguration.Card>(),
            isSinglePaymentMethod = false
        )

        assertEquals("zh-Hant-HK", args.localeTag)
    }

    private fun localizedSession() = mockk<AirwallexRecurringSession> {
        every { locale } returns Locale.forLanguageTag("zh-Hant-HK")
    }
}
