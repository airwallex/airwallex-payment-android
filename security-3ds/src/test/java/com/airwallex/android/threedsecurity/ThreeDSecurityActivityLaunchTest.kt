package com.airwallex.android.threedsecurity

import com.airwallex.android.core.model.Options
import com.airwallex.android.core.model.PaymentIntentContinueRequest
import org.junit.Assert.assertEquals
import org.junit.Test

class ThreeDSecurityActivityLaunchTest {

    @Test
    fun `args preserve explicit locale tag`() {
        val args = ThreeDSecurityActivityLaunch.Args(
            url = "https://example.com/3ds",
            body = "payload",
            options = Options.ContinuePaymentIntentOptions(
                clientSecret = "secret",
                paymentIntentId = "intent",
                request = PaymentIntentContinueRequest()
            ),
            localeTag = "pt-BR"
        )

        assertEquals("pt-BR", args.localeTag)
    }
}
