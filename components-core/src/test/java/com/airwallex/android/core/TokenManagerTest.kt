package com.airwallex.android.core

import android.util.Base64
import android.util.Log
import com.airwallex.android.core.log.AnalyticsLogger
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import org.junit.Before

class TokenManagerTest {

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        mockkObject(AnalyticsLogger)
        mockkStatic(Base64::class)
    }

//    @Test
//    fun `test accountId when update client secret`() {
//        val payload = """
//            {
//              "type":"client-secret",
//              "account_id": "edaf126c-f600-4653-95ea-4680b60fcce5"
//            }
//        """.trimIndent()
//        every { Base64.decode("def", Base64.DEFAULT) } returns payload.toByteArray()
//        TokenManager.updateClientSecret("abc.def.gh")
//        assertEquals("edaf126c-f600-4653-95ea-4680b60fcce5", TokenManager.accountId)
//    }
}