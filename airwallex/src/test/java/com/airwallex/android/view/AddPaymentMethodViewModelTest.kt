 package com.airwallex.android.view

 import android.app.Application
 import androidx.arch.core.executor.testing.InstantTaskExecutorRule
 import com.airwallex.android.core.Airwallex
 import com.airwallex.android.core.AirwallexPaymentSession
 import com.airwallex.android.core.AirwallexSession
 import com.airwallex.android.core.ClientSecretRepository
 import com.airwallex.android.core.exception.AirwallexCheckoutException
 import com.airwallex.android.core.model.Billing
 import com.airwallex.android.core.model.ClientSecret
 import com.airwallex.android.core.model.CreatePaymentMethodParams
 import com.airwallex.android.core.model.PaymentMethod
 import com.nhaarman.mockitokotlin2.mock
 import io.mockk.*
 import org.junit.Rule
 import org.junit.Test
 import kotlin.test.assertEquals
 import kotlin.test.assertNotEquals
 import kotlin.test.assertNull
 import kotlin.test.assertTrue

 class AddPaymentMethodViewModelTest {
     @get:Rule
     val instantExecutorRule = InstantTaskExecutorRule()

     private val application = mockk<Application>()
     private val airwallex: Airwallex = mockk()
     private val paymentMethod: PaymentMethod = mockk()

     @Test
     fun `one off payment method includes provided billing when information is required by session`() {
         val card: PaymentMethod.Card = mockk()
         val billing: Billing = mockk()
         val session: AirwallexPaymentSession = mockk()

         every { session.isBillingInformationRequired } returns true
         every { card.cvc } returns "123"

         val viewModel = createViewModel(session)
         val payment = viewModel.createPaymentMethod(card, false, billing)
         val result = requireNotNull(payment.value as? AddPaymentMethodViewModel.PaymentMethodResult.Success)
         val resultBilling = requireNotNull(result.paymentMethod.billing)

         assertEquals(resultBilling, billing)
     }

     @Test
     fun `one off payment method does not include provided billing when information is not required by session`() {
         val card: PaymentMethod.Card = mockk()
         val billing: Billing = mockk()
         val session: AirwallexPaymentSession = mockk()

         every { session.isBillingInformationRequired } returns false
         every { card.cvc } returns "123"

         val viewModel = createViewModel(session)
         val payment = viewModel.createPaymentMethod(card, false, billing)
         val result = requireNotNull(payment.value as? AddPaymentMethodViewModel.PaymentMethodResult.Success)
         val resultBilling = result.paymentMethod.billing

         assertNull(resultBilling)
         assertNotEquals(resultBilling, billing)
     }

     @Test
     fun `one off payment card missing cvc error`() {
         val card: PaymentMethod.Card = mockk()
         val billing: Billing = mockk()
         val session: AirwallexPaymentSession = mockk()

         every { session.isBillingInformationRequired } returns true
         every { card.cvc } returns null

         val viewModel = createViewModel(session)
         val payment = viewModel.createPaymentMethod(card, false, billing)
         val result = requireNotNull(payment.value as? AddPaymentMethodViewModel.PaymentMethodResult.Error)

         val errorMessage = requireNotNull(result.exception.message)
         assertTrue { errorMessage.contains("CVC missing") }
     }

     @Test
     fun `stored payment method includes provided billing when information is required by session`() {
         val clientSecret = mockk<ClientSecret>(relaxed = true)
         val paymentMethod = mockk<PaymentMethod>()
         val clientSecretRepository = mockk<ClientSecretRepository>()
         val clientSecretListener = slot<ClientSecretRepository.ClientSecretRetrieveListener>()
         val paymentListener = slot<Airwallex.PaymentListener<PaymentMethod>>()

         mockkObject(ClientSecretRepository)
         every { ClientSecretRepository.getInstance() } returns clientSecretRepository
         every { clientSecretRepository.retrieveClientSecret(any(), capture(clientSecretListener)) } answers {
             clientSecretListener.captured.onClientSecretRetrieve(clientSecret)
         }
         every { airwallex.createPaymentMethod(any(), capture(paymentListener)) } answers {
             paymentListener.captured.onSuccess(paymentMethod)
         }

         val customerID = "Test_ID"
         val card: PaymentMethod.Card = mockk()
         val billing: Billing = mockk()
         val session: AirwallexPaymentSession = mockk()

         every { session.customerId } returns customerID
         every { session.isBillingInformationRequired } returns true
         every { card.cvc } returns "123"

         val viewModel = createViewModel(session)
         val payment = viewModel.createPaymentMethod(card, true, billing)
         val result = requireNotNull(payment.value as? AddPaymentMethodViewModel.PaymentMethodResult.Success)

         assertEquals(result.paymentMethod, paymentMethod)

         val params = CreatePaymentMethodParams(
             clientSecret.value,
             customerID,
             card,
             billing
         )
         verify { airwallex.createPaymentMethod(params, any()) }
     }

     @Test
     fun `stored payment method does not include provided billing when information is not required by session`() {
         val clientSecret = mockk<ClientSecret>(relaxed = true)
         val paymentMethod = mockk<PaymentMethod>()
         val clientSecretRepository = mockk<ClientSecretRepository>()
         val clientSecretListener = slot<ClientSecretRepository.ClientSecretRetrieveListener>()
         val paymentListener = slot<Airwallex.PaymentListener<PaymentMethod>>()

         mockkObject(ClientSecretRepository)
         every { ClientSecretRepository.getInstance() } returns clientSecretRepository
         every { clientSecretRepository.retrieveClientSecret(any(), capture(clientSecretListener)) } answers {
             clientSecretListener.captured.onClientSecretRetrieve(clientSecret)
         }
         every { airwallex.createPaymentMethod(any(), capture(paymentListener)) } answers {
             paymentListener.captured.onSuccess(paymentMethod)
         }

         val customerID = "Test_ID"
         val card: PaymentMethod.Card = mockk()
         val billing: Billing = mockk()
         val session: AirwallexPaymentSession = mockk()

         every { session.customerId } returns customerID
         every { session.isBillingInformationRequired } returns false
         every { card.cvc } returns "123"

         val viewModel = createViewModel(session)
         val payment = viewModel.createPaymentMethod(card, true, billing)
         val result = requireNotNull(payment.value as? AddPaymentMethodViewModel.PaymentMethodResult.Success)

         assertEquals(result.paymentMethod, paymentMethod)

         val params = CreatePaymentMethodParams(
             clientSecret.value,
             customerID,
             card,
             null
         )
         verify { airwallex.createPaymentMethod(params, any()) }
     }

     private fun createViewModel(session: AirwallexSession): AddPaymentMethodViewModel =
         AddPaymentMethodViewModel(application, airwallex, session)
 }