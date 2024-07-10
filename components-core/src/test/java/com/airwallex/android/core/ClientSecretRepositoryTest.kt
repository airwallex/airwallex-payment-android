package com.airwallex.android.core

import android.util.Log
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.ClientSecret
import com.airwallex.risk.AirwallexRisk
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class ClientSecretRepositoryTest {

    @Before
    fun setup() {
        mockkObject(TokenManager)
        mockkObject(AnalyticsLogger)
        mockkObject(AirwallexRisk)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun retrieveClientSecretTest() = runBlocking {
        val provider = mockk<ClientSecretProvider>()
        val secret = ClientSecret(
            value = "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MjA2ODAwMzcsImV4cCI6MTcyMDY4MzYzNywidHlwZSI6ImNsaWVudC1zZWNyZXQiLCJwYWRjIjoiTkwiLCJhY2NvdW50X2lkIjoiMjFmZjBiZTctMjMyOS00MzA1LWJiNDItNzQxNjJlOTNiZGNhIiwiaW50ZW50X2lkIjoiaW50X25sc3RibGh4cGd4eGhodm53ZWQiLCJjdXN0b21lcl9pZCI6ImN1c19ubHN0OHQ0NGZneHFwbzVrd2htIiwiYnVzaW5lc3NfbmFtZSI6Ikhvd2UtQWJib3R0In0.fnun8MSu0YrBzBLywk5k7_E0qRSr4DFp7c_GAnn1Ubw",
            expiredTime = Date()
        )
        mockkConstructor(JSONObject::class)
        every { provider.provideClientSecret(any()) } returns secret
        every { TokenManager.updateClientSecret(any()) } answers {
            TokenManager.accountId = "21ff0be7-2329-4305-bb42-74162e93bdca"
        }

        val repository = ClientSecretRepository(provider)
        repository.retrieveClientSecret("")

        assertEquals("21ff0be7-2329-4305-bb42-74162e93bdca", TokenManager.accountId)
        verify { AnalyticsLogger.updateAccountId("21ff0be7-2329-4305-bb42-74162e93bdca") }
        verify { AirwallexRisk.setAccountId("21ff0be7-2329-4305-bb42-74162e93bdca") }
    }
}