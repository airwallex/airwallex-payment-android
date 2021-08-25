package com.airwallex.android.core.exception

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class APIConnectionExceptionTest {

    @Test
    fun testEquals() {
        val apiException = APIConnectionException(
            message = "message",
            e = Exception()
        )

        assertNotNull(apiException)
        assertEquals("message", apiException.message)
    }
}
