package com.airwallex.android.view

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.ClientSecretRepository
import com.airwallex.android.core.GooglePayOptions
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.model.ClientSecret
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.TransactionMode
import com.airwallex.android.core.model.parser.AvailablePaymentMethodTypeResponseParser
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class PaymentMethodsViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `test fetchAvailablePaymentMethodTypes when session is recurring and has client secret`() =
        runTest {
            val viewModel = mockViewModel(transactionMode = TransactionMode.RECURRING)
            val result = viewModel.fetchAvailablePaymentMethodTypes()
            assertEquals(result.value?.getOrNull()?.first()?.name, "card")
        }

    @Test
    fun `test fetchAvailablePaymentMethodTypes when session is recurring and has no client secret`() =
        runTest {
            val viewModel = mockViewModel(false, TransactionMode.RECURRING)
            val result = viewModel.fetchAvailablePaymentMethodTypes()
            assertEquals(result.value?.isFailure, true)
        }

    @Test
    fun `test fetchAvailablePaymentMethodTypes when session is oneoff`() = runTest {
        val viewModel = mockViewModel(transactionMode = TransactionMode.ONE_OFF)
        val result = viewModel.fetchAvailablePaymentMethodTypes()
        assertEquals(result.value?.getOrNull()?.first()?.name, "card")
    }

    private fun mockViewModel(hasClientSecret: Boolean = true, transactionMode: TransactionMode):
        PaymentMethodsViewModel {
        val mockResponse = AvailablePaymentMethodTypeResponseParser().parse(
            JSONObject(
                """
        {
        "items":[ 
                  {
                   "name":"card",
                   "transaction_mode":${transactionMode.value},
                   "active":true,
                   "transaction_currencies":["dollar","RMB"],
                   "flows":["inapp"]
                  }   
            ],
            "has_more":false
        }
                """.trimIndent()
            )
        )

        val clientSecret = mockk<ClientSecret>(relaxed = true)
        val clientSecretRepository = mockk<ClientSecretRepository>()
        mockkObject(ClientSecretRepository)
        if (hasClientSecret) {
            coEvery { clientSecretRepository.retrieveClientSecret(any()) } returns clientSecret
        } else {
            coEvery { clientSecretRepository.retrieveClientSecret(any()) } throws AirwallexCheckoutException()
        }

        every { ClientSecretRepository.getInstance() } returns clientSecretRepository
        val application = mockk<Application>()
        val airwallex = mockk<Airwallex>()
        val recurringSession = AirwallexRecurringSession.Builder(
            nextTriggerBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
            customerId = "cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu",
            currency = "AUD",
            amount = BigDecimal.valueOf(100.01),
            countryCode = "CN"
        )
            .setMerchantTriggerReason(PaymentConsent.MerchantTriggerReason.SCHEDULED)
            .setRequireCvc(true)
            .build()
        val oneOffSession = AirwallexPaymentSession.Builder(
            PaymentIntent(
                id = "id",
                amount = BigDecimal.valueOf(100.01),
                currency = "AUD",
                clientSecret = "qadf"
            ),
            "AU",
            GooglePayOptions(merchantId = "merchantId")
        ).build()
        val session = when (transactionMode) {
            TransactionMode.ONE_OFF -> oneOffSession
            TransactionMode.RECURRING -> recurringSession
        }
        coEvery { airwallex.retrieveAvailablePaymentMethods(any(), any()) } returns mockResponse
        return PaymentMethodsViewModel(application, airwallex, session)
    }
}
