package com.airwallex.android.googlepay

import android.app.Application
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.GooglePayOptions
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.Test

class GooglePayLauncherViewModelTest {
    @Test
    fun `test load payment data task`() {
        mockkStatic(PaymentsUtil::class)
        mockkStatic(PaymentDataRequest::class)
        mockkStatic(PaymentsClient::class)
        mockkStatic(Wallet::class)

        val application = mockk<Application>()
        val session = mockk<AirwallexSession>(relaxed = true)
        val googlePayOptions = GooglePayOptions()
        val paymentMethodType = mockk<AvailablePaymentMethodType>(relaxed = true)
        val mockClient = mockk<PaymentsClient>()
        val mockRequest = mockk<PaymentDataRequest>()
        val mockTask = mockk<Task<PaymentData>>()
        every { PaymentDataRequest.fromJson(any()) } returns mockRequest
        every { mockClient.loadPaymentData(mockRequest) } returns mockTask
        every { PaymentsUtil.createPaymentsClient(application) } returns mockClient

        val viewModel = GooglePayLauncherViewModel.Factory(
            application = application,
            args = GooglePayActivityLaunch.Args(session, googlePayOptions, paymentMethodType)
        ).create(GooglePayLauncherViewModel::class.java)
        viewModel.getLoadPaymentDataTask()
        verify { mockClient.loadPaymentData(mockRequest) }

        unmockkAll()
    }
}