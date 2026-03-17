package com.airwallex.android.core.model

import com.airwallex.android.core.model.parser.ClientSecretParser
import org.json.JSONObject
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ClientSecretParserTest {

    @Test
    fun testParseClientSecret() {
        val clientSecret = ClientSecretParser().parse(
            JSONObject(
                """
                {
                    "client_secret": "test_secret_12345",
                    "expired_time": "2025-12-31T23:59:59+0000"
                }
                """.trimIndent()
            )
        )

        assertEquals("test_secret_12345", clientSecret.value)
        assertNotNull(clientSecret.expiredTime)
    }

    @Test
    fun testParseClientSecretWithDifferentDateFormat() {
        val clientSecret = ClientSecretParser().parse(
            JSONObject(
                """
                {
                    "client_secret": "another_secret",
                    "expired_time": "2026-01-15T12:30:45+0000"
                }
                """.trimIndent()
            )
        )

        assertEquals("another_secret", clientSecret.value)
        assertNotNull(clientSecret.expiredTime)
    }

    @Test
    fun testParseClientSecretWithEmptySecret() {
        val clientSecret = ClientSecretParser().parse(
            JSONObject(
                """
                {
                    "client_secret": "",
                    "expired_time": "2025-12-31T23:59:59+0000"
                }
                """.trimIndent()
            )
        )

        assertEquals("", clientSecret.value)
        assertNotNull(clientSecret.expiredTime)
    }
}
