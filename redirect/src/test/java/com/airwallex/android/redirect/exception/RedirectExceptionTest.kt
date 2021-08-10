package com.airwallex.android.redirect.exception

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RedirectExceptionTest {

    @Test
    fun testExceptionFields() {
        val redirectException = RedirectException(
            message = "message"
        )

        assertNotNull(redirectException)
        assertEquals("message", redirectException.message)
    }

    @Test
    fun testExceptionEquals() {
        val redirectException = RedirectException(
            message = "message"
        )
        assertEquals(
            "com.airwallex.android.redirect.exception.RedirectException: message; status-code: 0, null",
            redirectException.toString()
        )
    }
}
