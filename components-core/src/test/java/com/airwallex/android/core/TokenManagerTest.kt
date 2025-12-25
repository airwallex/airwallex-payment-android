package com.airwallex.android.core

import android.util.Base64
import android.util.Log
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.risk.AirwallexRisk
import io.mockk.every
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class TokenManagerTest {

    @Before
    fun setup() {
        // Clear all previous mocks
        unmockkAll()

        // Mock dependencies first before resetting state
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        mockkObject(AirwallexLogger)
        every { AirwallexLogger.debug(any()) } just runs
        mockkObject(AnalyticsLogger)
        every { AnalyticsLogger.updateAccountId(any()) } just runs
        mockkObject(AirwallexRisk)
        every { AirwallexRisk.setAccountId(any()) } just runs
        mockkStatic(Base64::class)

        // Reset TokenManager state using reflection to access private field
        TokenManager.accountId = null
        val clientSecretField = TokenManager::class.java.getDeclaredField("clientSecret")
        clientSecretField.isAccessible = true
        clientSecretField.set(TokenManager, null)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `test updateClientSecret with same clientSecret does not update accountId`() {
        val payload = """
            {
              "type":"client-secret",
              "account_id": "test-account-id"
            }
        """.trimIndent()
        every { Base64.decode("def", Base64.DEFAULT) } returns payload.toByteArray()

        // First update
        TokenManager.updateClientSecret("abc.def.gh")
        assertEquals("test-account-id", TokenManager.accountId)

        // Second update with same clientSecret - should not trigger accountId update
        TokenManager.updateClientSecret("abc.def.gh")

        // Verify that AnalyticsLogger.updateAccountId was called only once
        verify(exactly = 1) { AnalyticsLogger.updateAccountId("test-account-id") }
    }

    @Test
    fun `test updateClientSecret with different clientSecret updates accountId`() {
        val payload1 = """
            {
              "type":"client-secret",
              "account_id": "account-id-1"
            }
        """.trimIndent()
        val payload2 = """
            {
              "type":"client-secret",
              "account_id": "account-id-2"
            }
        """.trimIndent()
        every { Base64.decode("payload1", Base64.DEFAULT) } returns payload1.toByteArray()
        every { Base64.decode("payload2", Base64.DEFAULT) } returns payload2.toByteArray()

        // First update
        TokenManager.updateClientSecret("header.payload1.signature")
        assertEquals("account-id-1", TokenManager.accountId)

        // Second update with different clientSecret - should trigger accountId update
        TokenManager.updateClientSecret("header.payload2.signature")
        assertEquals("account-id-2", TokenManager.accountId)

        // Verify that AnalyticsLogger.updateAccountId was called twice
        verify(exactly = 1) { AnalyticsLogger.updateAccountId("account-id-1") }
        verify(exactly = 1) { AnalyticsLogger.updateAccountId("account-id-2") }
    }
}