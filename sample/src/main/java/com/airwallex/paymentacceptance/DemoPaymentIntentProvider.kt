package com.airwallex.paymentacceptance

import com.airwallex.android.core.PaymentIntentProvider
import com.airwallex.paymentacceptance.repo.PACheckoutDemoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Simple implementation of PaymentIntentProvider that creates payment intents on demand.
 * This demonstrates the Express Checkout flow where payment intents are created
 * asynchronously when needed, rather than upfront.
 */
class DemoPaymentIntentProvider(
    override val currency: String = Settings.currency,
    override val amount: java.math.BigDecimal = Settings.price.toBigDecimal(),
    private val force3DS: Boolean = Settings.force3DS == "True",
    private val customerId: String? = Settings.cachedCustomerId
) : PaymentIntentProvider {

    override fun provide(callback: PaymentIntentProvider.PaymentIntentCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Create payment intent on demand using the same logic as the static flow
                val repository = PACheckoutDemoRepository()
                val paymentIntent = repository.getPaymentIntentFromServer(
                    force3DS = force3DS,
                    customerId = customerId
                )
                callback.onSuccess(paymentIntent)
            } catch (exception: Exception) {
                callback.onError(exception)
            }
        }
    }
}