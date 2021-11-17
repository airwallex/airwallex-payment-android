package com.airwallex.android.core.exception

import com.airwallex.android.core.model.AirwallexError
import org.junit.Test
import java.net.HttpURLConnection
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AuthenticationExceptionTest {

    @Test
    fun testEquals() {
        val authenticationException = AuthenticationException(
            error = AirwallexError(
                code = "code",
                source = "source",
                message = "message"
            ),
            traceId = "traceId",
        )

        assertNotNull(authenticationException)
        assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, authenticationException.statusCode)
    }
}
