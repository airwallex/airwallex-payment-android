package com.airwallex.android.core.exception

import org.junit.Test
import kotlin.test.assertEquals

class InvalidRequestExceptionTest {

    @Test
    fun testEquals() {
        val invalidRequestException = InvalidRequestException(
            param = "param",
            traceId = "traceId",
            message = "message"
        )

        assertEquals("param", invalidRequestException.param)
        assertEquals("message", invalidRequestException.message)
    }
}
