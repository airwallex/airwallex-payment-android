package com.airwallex.android.core.exception

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class APIExceptionTest {

    @Test
    fun testEquals() {
        val apiException = APIException(
            traceId = "traceId",
            statusCode = 403,
            message = "message"
        )

        assertNotNull(apiException)
        assertEquals("message", apiException.message)
    }
}
