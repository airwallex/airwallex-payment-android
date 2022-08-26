package com.airwallex.android.view

import android.app.Application
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.model.PaymentMethod
import io.mockk.*
import org.junit.Before
import org.junit.Test

class AirwallexCheckoutViewModelTest {
    private lateinit var viewModel: AirwallexCheckoutViewModel
    private val airwallex: Airwallex = mockk()
    private val session: AirwallexSession = mockk()
    private val paymentMethod: PaymentMethod = mockk()

    @Before
    fun setUp() {
        val application = mockk<Application>()
        every {
            airwallex.createPaymentConsentAndConfirmIntent(
                session,
                paymentMethod,
                null,
                any()
            )
        } just runs
        every {
            airwallex.checkout(
                session,
                paymentMethod,
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } just runs
        viewModel = AirwallexCheckoutViewModel(application, airwallex, session)
    }

    @Test
    fun `test checkout when saveCard is true`() {
        viewModel.checkout(
            paymentMethod = paymentMethod,
            paymentConsentId = null,
            cvc = null,
            saveCard = true
        )
        verify(exactly = 1) {
            airwallex.createPaymentConsentAndConfirmIntent(
                session,
                paymentMethod,
                null,
                any()
            )
        }
    }

    @Test
    fun `test checkout when saveCard is false`() {
        viewModel.checkout(
            paymentMethod = paymentMethod,
            paymentConsentId = null,
            cvc = null
        )
        verify(exactly = 1) {
            airwallex.checkout(
                session,
                paymentMethod,
                any(),
                null,
                any(),
                any(),
                any()
            )
        }
    }
}