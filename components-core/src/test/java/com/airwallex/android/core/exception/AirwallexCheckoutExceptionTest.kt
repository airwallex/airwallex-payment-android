package com.airwallex.android.core.exception

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AirwallexCheckoutExceptionTest {

    @Test
    fun testEquals() {
        val airwallexCheckoutException = AirwallexCheckoutException(
            traceId = "traceId",
            statusCode = 0,
            message = "message"
        )

        assertNotNull(airwallexCheckoutException)
        assertEquals("message", airwallexCheckoutException.message)
    }
}
