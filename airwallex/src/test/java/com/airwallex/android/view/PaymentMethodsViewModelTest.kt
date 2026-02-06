package com.airwallex.android.view

import android.app.Application
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.PaymentIntent
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class PaymentMethodsViewModelTest {

    private lateinit var viewModel: PaymentMethodsViewModel
    private lateinit var application: Application
    private lateinit var airwallex: Airwallex
    private lateinit var session: AirwallexPaymentSession

    @Before
    fun setUp() {
        mockkObject(AnalyticsLogger)
        every { AnalyticsLogger.logAction(any(), any()) } just Runs

        application = mockk(relaxed = true)
        airwallex = mockk(relaxed = true)
        session = mockk(relaxed = true) {
            every { paymentIntent } returns mockk<PaymentIntent>(relaxed = true)
        }

        viewModel = PaymentMethodsViewModel(application, airwallex, session)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `test pageName returns correct value`() {
        assertEquals("payment_method_list", viewModel.pageName)
    }

    @Test
    fun `test trackPaymentSelection does not log when payment method is null`() {
        viewModel.trackPaymentSelection(null)

        verify(exactly = 0) {
            AnalyticsLogger.logAction(any(), any())
        }
    }

    @Test
    fun `test trackPaymentSelection does not log when payment method is empty`() {
        viewModel.trackPaymentSelection("")

        verify(exactly = 0) {
            AnalyticsLogger.logAction(any(), any())
        }
    }

    @Test
    fun `test trackPaymentSelection logs with different payment method types`() {
        val paymentMethods = listOf("card", "wechat", "alipay", "googlepay")

        paymentMethods.forEach { method ->
            viewModel.trackPaymentSelection(method)

            verify {
                AnalyticsLogger.logAction(
                    "select_payment",
                    mapOf("payment_method" to method)
                )
            }
        }
    }
}
