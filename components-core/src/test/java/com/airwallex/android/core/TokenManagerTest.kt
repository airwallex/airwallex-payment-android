package com.airwallex.android.core

import android.util.Base64
import com.airwallex.android.core.log.AnalyticsLogger
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Test
import kotlin.test.assertEquals

class TokenManagerTest {
    @Test
    fun `test accountId when update client secret`() {
        mockkObject(AnalyticsLogger)

        val payload = """
            {
              "type":"client-secret",
              "account_id": "edaf126c-f600-4653-95ea-4680b60fcce5"
            }
        """.trimIndent()
        mockkStatic(Base64::class)
        every { Base64.decode("def", Base64.DEFAULT) } returns payload.toByteArray()
        TokenManager.updateClientSecret("abc.def.gh")
        assertEquals(TokenManager.accountId, "edaf126c-f600-4653-95ea-4680b60fcce5")

        verify { AnalyticsLogger.updateAccountId(any()) }
    }
}