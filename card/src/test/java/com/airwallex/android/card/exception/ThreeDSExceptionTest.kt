package com.airwallex.android.card.exception

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ThreeDSExceptionTest {

    @Test
    fun testExceptionFields() {
        val threeDSException = ThreeDSException(
            message = "message"
        )

        assertNotNull(threeDSException)
        assertEquals("message", threeDSException.message)
    }

    @Test
    fun testExceptionEquals() {
        val threeDSException = ThreeDSException(
            message = "message"
        )

        assertEquals(
            "com.airwallex.android.card.exception.ThreeDSException: message; status-code: 0, null",
            threeDSException.toString()
        )
    }
}
