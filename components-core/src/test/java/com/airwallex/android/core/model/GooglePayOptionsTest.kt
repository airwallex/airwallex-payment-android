package com.airwallex.android.core.model

import com.airwallex.android.core.BillingAddressParameters
import com.airwallex.android.core.GooglePayOptions
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GooglePayOptionsTest {
    private val googlePayOptions by lazy {
        GooglePayOptions(
            allowedCardAuthMethods = listOf("3DS"),
            merchantId = "id",
            billingAddressParameters = BillingAddressParameters(BillingAddressParameters.Format.FULL)
        )
    }

    @Test
    fun testParams() {
        assertEquals(googlePayOptions.allowedCardAuthMethods?.first(), "3DS")
        assertEquals(googlePayOptions.merchantId, "id")
        assertNull(googlePayOptions.allowCreditCards)
        assertNull(googlePayOptions.allowPrepaidCards)
        assertNull(googlePayOptions.assuranceDetailsRequired)
        assertNull(googlePayOptions.billingAddressRequired)
        assertEquals(
            googlePayOptions.billingAddressParameters?.format,
            BillingAddressParameters.Format.FULL
        )
        assertEquals(googlePayOptions.billingAddressParameters?.phoneNumberRequired, false)
    }
}
