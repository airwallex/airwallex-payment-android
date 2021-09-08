package com.airwallex.android.card.exception

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DccExceptionTest {

    @Test
    fun testExceptionFields() {
        val dccException = DccException(
            message = "message"
        )

        assertNotNull(dccException)
        assertEquals("message", dccException.message)
    }

    @Test
    fun testExceptionEquals() {
        val dccException = DccException(
            message = "message"
        )
        assertEquals(
            "com.airwallex.android.card.exception.DccException: message; status-code: 0, null",
            dccException.toString()
        )
    }
}
