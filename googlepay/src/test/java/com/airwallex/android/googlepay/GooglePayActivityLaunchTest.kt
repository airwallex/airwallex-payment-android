package com.airwallex.android.googlepay

import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.GooglePayOptions
import com.airwallex.android.core.model.AvailablePaymentMethodType
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class GooglePayActivityLaunchTest {

    @Test
    fun `args expose session locale tag`() {
        val session = mockk<AirwallexRecurringSession> {
            every { locale } returns Locale.GERMANY
        }
        val args = GooglePayActivityLaunch.Args.create(
            session = session,
            googlePayOptions = GooglePayOptions(),
            paymentMethodType = mockk<AvailablePaymentMethodType>()
        )

        assertEquals("de-DE", args.localeTag)
    }
}
