package com.airwallex.android.threedsecurity

import com.airwallex.android.threedsecurity.exception.WebViewConnectionException
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class WebViewConnectionExceptionTest {

    @Test
    fun testExceptionFields() {
        val webViewConnectionException = WebViewConnectionException(
            message = "message",
            e = RuntimeException()
        )

        assertNotNull(webViewConnectionException)
        assertEquals("message", webViewConnectionException.message)
    }

    @Test
    fun testExceptionEquals() {
        val webViewConnectionException = WebViewConnectionException(
            message = "message",
            e = RuntimeException()
        )

        assertEquals(
            "com.airwallex.android.threedsecurity.exception.WebViewConnectionException: message; status-code: 0, code null, source null, message message",
            webViewConnectionException.toString()
        )
    }
}
