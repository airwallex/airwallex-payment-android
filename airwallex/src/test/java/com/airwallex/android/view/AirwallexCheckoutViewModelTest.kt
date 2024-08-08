package com.airwallex.android.view

import android.app.Application
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.ui.checkout.AirwallexCheckoutViewModel
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